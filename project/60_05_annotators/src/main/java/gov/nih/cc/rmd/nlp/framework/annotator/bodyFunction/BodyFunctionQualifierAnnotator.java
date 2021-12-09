// =================================================
/**
 * BodyQualifierAnnotator finds BodyFunction Qualifier evidence in text
 * 
 *  This rule based evidence relies on three aspects - 
 *  it looks for other body function evidence (range of motion, strength, reflex) mentions
 *  then looks for 
 *    1. Dictionary based body function qualifiers (normal, WNL, good, poor, .... )
 *    2. Regular expressions on non-lexical tokens to find elements like 4/5 
 *
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.BodyFunction;
import gov.nih.cc.rmd.framework.BodyFunctionMention;
import gov.nih.cc.rmd.framework.RangeOfMotion;
import gov.nih.cc.rmd.framework.Reflex;
import gov.nih.cc.rmd.framework.Strength;
import gov.nih.cc.rmd.framework.BFQualifier;
import gov.nih.cc.rmd.framework.model.Date;
import gov.nih.cc.rmd.framework.model.Redacted;
import gov.nih.cc.rmd.framework.model.UnitOfMeasure;
import gov.nih.cc.rmd.gate.Body_Function;
import gov.nih.cc.rmd.model.SectionName;
import gov.nih.cc.rmd.model.SemanticWindow;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.WordToken;





public class BodyFunctionQualifierAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * BodyQualifiers from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
  
    int docLength = pJCas.getDocumentText().length();
    
    // find sentences that have body function terms in them
    List<Annotation> bodyFunctionTerms = UIMAUtil.getAnnotations(pJCas, BodyFunction.typeIndexID, true );
    
    if ( bodyFunctionTerms != null && !bodyFunctionTerms.isEmpty())
      for ( Annotation term: bodyFunctionTerms ) {
        
        // find the sentences that have this mention in them
        // Don't consider body location - body locations are everywhere
        
       List<Annotation> overlappingTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.type, term.getBegin(), term.getEnd() );
       LexicalElement aTerm = null;
       if ( overlappingTerms != null && !overlappingTerms.isEmpty()) 
         aTerm = (LexicalElement) overlappingTerms.get(0);
       else
         continue;
         
        String bodyFunctionClass = term.getClass().getName();
        
        if (!bodyFunctionClass.contains("BodyLocation") || isInBFSection(pJCas, aTerm))
        {
        Annotation utterance = getUtteranceFromAnnotation( pJCas, term );
        boolean found = false;
        if ( (utterance != null) && !((gov.va.chir.model.VAnnotation) utterance).getMarked() ) { 
         
          List<Annotation> qualifiers = new ArrayList<Annotation>();
         found = lookForBodyFunctionQualifiersInUtterance( pJCas, utterance, qualifiers );
        
     
         ((gov.va.chir.model.VAnnotation) utterance).setMarked( true);    // use the perishable "marked" tag on the utterance
                                                                          // to avoid processing the utterance multiple times
                                                                          // if there are multiple body function evidences in it
         
                                                                          // there's a possible bug here where the utterances
                                                                          // could be marked in a prior annotator 
       
         if ( !found ) {
           Annotation semanticWindow = null;
          // retrieve next utterance and look there
           // if the utterance ends with a ":"
           if ( utternaceEndsWithColon( pJCas, utterance )) {
              semanticWindow = getSpanWindow(  pJCas, docLength, utterance);
              if ( semanticWindow != null )  
                found = lookForBodyFunctionQualifiersInUtterance( pJCas, semanticWindow, qualifiers );
                if ( found )
                  ((gov.va.chir.model.VAnnotation) utterance).setMarked( true); 
           }
         }
         
            
         }
           
         
        
       
        }
      } // end loop through each piece of evidence
    
   
    
    // 
    // unique the qualifiers 
    //
    List<Annotation> qualifiers = UIMAUtil.getAnnotations( pJCas, BFQualifier.typeIndexID);
    if ( qualifiers != null)
      UIMAUtil.uniqueAnnotations(qualifiers );
   
    
    // --------------------------------------
    // Special rules 
    //  - combine contagious qualifiers into one  
    //  - context to reduce false positives - if qualifiers are around pain, they should be removed
    //
    // 
    filterOutPainQualifiers ( pJCas);
    
    // ---------------------------------------
    // Weed out historical/conditional/negated/subjective/in the wrong section annotations
    // ---------------------------------------
    filterOutBadContextQualifiers( pJCas);
    
    
    mergeQualifiers( pJCas );
    
  
   
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  
  } // end Method process() ----------------
   

// =================================================
  /**
   * isInBFSection returns true if the evidence (bodyLocation, most likely)
   * is in a section that sets the body function context.  A section
   * name like "Strength"  or Neurological
   * 
   * @param pJCas
   * @param pTerm
   * @return boolean
   * @throws Exception 
  */
  // =================================================
  private final boolean isInBFSection(JCas pJCas, Annotation pTerm) throws Exception {
    boolean returnVal = false;
    
    returnVal = BodyFunctionAnnotator.isGoodBFSection(pJCas, (LexicalElement) pTerm);
    
    
    return returnVal;
  } // end Method isInBFSection() ------------------


// =================================================
  /**
   * getBFSections finds all sectionName annotations,
   * looks to see if these overlap with body parts,
   * strength, rom, reflex, musculetial, neurological
   * 
   * @param pJCas
   * @return List<Annotation>
  */
  // =================================================
  private List<Annotation> getBFSectionZones(JCas pJCas) {
    List<Annotation> returnVal = null;
    
    
    List<Annotation> sectionNames = UIMAUtil.getAnnotations(pJCas, SectionName.typeIndexID );
    
    if ( sectionNames != null && !sectionNames.isEmpty())
      for ( Annotation sectionName : sectionNames )
        if ( isBFSection (pJCas, sectionName )) {
          if ( returnVal == null )
            returnVal = new ArrayList<Annotation>();
          Annotation sectionZone = ((SectionName) sectionName).getSection();
          returnVal.add( sectionZone);
        }
            
    return returnVal;
  } // end Method getBFSectionZones() --------------


// =================================================
/**
 * isBFSection * looks to see if these overlap with body parts,
   * strength, rom, reflex, musculetial, neurological
 * 
 * @param pJCas
 * @param pSectionName
 * @return boolean
*/
// =================================================
private final boolean isBFSection(JCas pJCas, Annotation pSectionName) {
  
  boolean returnVal = false;
  List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSectionName.getBegin(), pSectionName.getEnd() );
  
  if ( terms != null && !terms.isEmpty())
    for ( Annotation term: terms ) {
     String kinds = ((LexicalElement) term).getSemanticTypes();
     if ( kinds != null && (
         kinds.toLowerCase().contains("strength") ||
         kinds.toLowerCase().contains("rom") ||
         kinds.toLowerCase().contains("range of motion") ||
         kinds.toLowerCase().contains("reflex") ||
         kinds.toLowerCase().contains("location") ||
        kinds.toLowerCase().contains("location") )) {
       
       returnVal = true;
       break;
     }
         
         
    }
    
    
  return returnVal;
} // end Method isBFSection() ---------------------


// =================================================
  /**
   * getSpanWindow retrieves the SemanticWindow that
   * is an aggregation of sentences that were joined together
   * because they are sentence fragments like list elements
   * or joined by : or ;
   * 
   * This should be capped by a period
   *
   * @param pJCas
   * @param pUtterance
   * @return Annotation
  */
  // =================================================
  private Annotation getSpanWindow(JCas pJCas, int pDocLength,  Annotation pUtterance) {
  
    Annotation statement = new SemanticWindow( pJCas);
    statement.setBegin( pUtterance.getBegin());
    
    int _end = pUtterance.getEnd() + 266;
    if ( _end >= pDocLength -1 )
      _end = pDocLength -1;
    
    statement.setEnd( _end);
    
    String coveredText = statement.getCoveredText();
    
    int firstPeriod = coveredText.indexOf( ". ");
    
    if ( firstPeriod > -1) {
      _end = statement.getBegin() + firstPeriod ;
      statement.setEnd(_end );
    }
      
    statement.addToIndexes();
    
    return statement;
  } // end Method getNextUtterance() ---------------


// =================================================
  /**
   * utternaceEndsWithColon look at the next tokens after
   * the utterance to see if there is a colon or semi-colon
   * (utterances don't include the trailing punctuation)
   * 
   * @param pJCas
   * @param utterance
   * @return boolean 
  */
  // =================================================
  private final boolean utternaceEndsWithColon(JCas pJCas, Annotation pUtterance) {
    
    boolean returnVal = false;
    
    String utteranceText = pUtterance.getCoveredText().trim();
    
    if ( utteranceText.endsWith(":") || utteranceText.endsWith(";")  )
      returnVal = true;
    
    else {
    
    int _begin = pUtterance.getEnd();
    int _end = _begin + 10;
    int docLength = pJCas.getDocumentText().length(); 
    if ( _end > docLength ) _end = docLength -1;
    List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, _begin, _end );
    
    if ( tokens != null && !tokens.isEmpty()) {
      Annotation firstToken = tokens.get(0);
      String firstTokenText =  firstToken.getCoveredText();
      if ( firstTokenText.contains(":") || firstTokenText.contains(";"))
        returnVal = true;
    }
    }
    return returnVal;
  } // end Method utternaceEndsWithColon() ---------


// =================================================
  /**
   * lookForBodyFunctionQualifiersInUtterance 
   * 
   * @param pJCas
   * @param pUtterance
   * @param qualifiers 
   * @return boolean
   * @throws Exception 
  */
  // =================================================
  private final boolean lookForBodyFunctionQualifiersInUtterance(JCas pJCas, Annotation pUtterance, List<Annotation> pQualifiers) throws Exception {
    
    boolean returnVal = false;
    // mark dictionary based qualifiers within this utterance
    if ( lookForDictionaryBodyFucntionQualifersinUtterance( pJCas, pUtterance, pQualifiers) )
      returnVal = true;
    
    // mark regular expression based qualifiers within this utterance
    if ( LookForRegularExpressionBodyFunctionQualifiersInUtterance( pJCas, pUtterance, pQualifiers) )
      returnVal = true;
    
    // look for tokens that are xxx/5 and xxx/10 qualifiers within this utterance
    //if ( LookForBodyFunctionQualifierScalesInUtterance( pJCas, pUtterance, pQualifiers) )
    //  returnVal = true;
    
    // mark unitOfMeasure based qualifiers within this utterance
    if (  LookForUnitOfMeasureBodyFunctionQualifiersInUtterance( pJCas, pUtterance, pQualifiers) )
      returnVal = true;
    
    return returnVal;
    
  } // end Method lookForBodyFunctionQualifiersInUtterance() ------
  
  // =================================================
  /**
   * filterByBodyLocationForNonScaleQualifiers 
   * returns true if there are qualifiers, 
   *    AND 
   *       those qualifiers are in an utterance not triggered by a bodyLocation class piece of evidence
   *       OR
   *       those qualifiers are in an utterance that have a x/5 or x/10 qualifier
   * 
   * @param pJCas
   * @param pUtterance
   * @param pBodyFunctionClass
   * @param pQualifiers 
   * @return boolean
   */
  // =================================================
  private final boolean filterByBodyLocationForNonScaleQualifiers(JCas pJCas, Annotation pUtterance, String pBodyFunctionClass, List<Annotation> pQualifiers) {
    boolean returnVal = false;
  
    
    if (pBodyFunctionClass.contains("BodyLocation")) {
      for ( Annotation qualifier: pQualifiers) {
        String buff = qualifier.getCoveredText();
        if ( buff.contains("/5") || buff.contains("/10")) {
           returnVal = true;
           
        }
      } // end loop thru qualifiers
    
    } else 
       returnVal = true;
    
    return returnVal;
 
  } // end Method filterByBodyLocationForNonScaleQualifiers() ------
// =================================================
/**
 * LookForBodyFunctionQualifierScalesInUtterance [TBD] summary
 * 
 * @param pJCas
 * @param pUtterance
 * @param pQualifiers 
 * @return boolean
*/
// =================================================
private boolean LookForBodyFunctionQualifierScalesInUtterance(JCas pJCas, Annotation pUtterance, List<Annotation> pQualifiers) {
 
  boolean returnVal = false;
  List<Annotation> scaleTokensReversed = null;
  
  List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan( pJCas, WordToken.typeIndexID, pUtterance.getBegin(), pUtterance.getEnd());

  
  if ( tokens != null && !tokens.isEmpty()) 
    for (  int i = tokens.size() -1; i >=0; i-- ) {
      Annotation aToken = tokens.get(i);
     
      String aWord = aToken.getCoveredText().trim();
      if ( aWord.contains("/5") || aWord.contains("/10")) {
        scaleTokensReversed = new ArrayList<Annotation>();
        scaleTokensReversed.add( aToken);
        for ( int j = 1; j < i; j++) {
          Annotation candidateToken = tokens.get(i-j);
          String candidateWord = candidateToken.getCoveredText().trim();
          if ( candidateWord.contains("+") || candidateWord.contains("-")|| candidateWord.contains("to"))
            scaleTokensReversed.add( candidateToken);
          if ( U.containsLetters( candidateWord) )
            break;
          else if (U.containsNumber( candidateWord ))
            scaleTokensReversed.add(candidateToken);
        } // end loop through words to left
      } // a scale seen
    } // end loop through tokens
  
    if ( scaleTokensReversed != null && !scaleTokensReversed.isEmpty() ) {
      Annotation lastWord =  scaleTokensReversed.get(0) ;
      Annotation firstWord = scaleTokensReversed.get( scaleTokensReversed.size() -1);
      String sectionName = ((WordToken) lastWord).getSectionName();
      System.err.println( "Section name = " + sectionName);
      
    //  if ( !BodyFunctionAnnotator.filterBySection(pJCas, lastWord) )
    //    if (!overlapsRedaction (pJCas, firstWord)  ) {
      
          @SuppressWarnings("unchecked")
          List<Annotation> scaleTokens = (List<Annotation>) U.reverse(scaleTokensReversed);
          pQualifiers.add(  createBodyFunctionQualifierToken(pJCas, scaleTokens.get(0).getBegin(),  scaleTokens.get(scaleTokens.size() -1).getEnd() ));  
          returnVal = true;
         
     //   }
    } // end if there are scale Tokens
  
  return returnVal;
  
} // end Method LookForBodyFunctionQualifierScalesInUtterance() -----


// =================================================
  /**
   * LookForRegularExpressionBodyFunctionQualifiersInUtterance 
   * iterates though the tokens of utterance, looking for 
   * tokens that do not belong to terms, and have punctuation
   * or numbers in them
   * 
   * @param pJCas
   * @param pUtterance
   * @param pQualifiers 
   * @return boolean
   * @throws Exception 
  */
  // =================================================
  private final boolean LookForRegularExpressionBodyFunctionQualifiersInUtterance(JCas pJCas, Annotation pUtterance, List<Annotation> pQualifiers) throws Exception {
    
    boolean returnVal = false;
    List<Annotation> numbers = UIMAUtil.getAnnotationsBySpan( pJCas, gov.nih.cc.rmd.framework.model.PotentialNumber.typeIndexID, pUtterance.getBegin(), pUtterance.getEnd());

    
    if ( numbers != null && !numbers.isEmpty()) 
      for (  Annotation aNumber : numbers ) 
        if ( aNumber != null ) {
        
      
          List<Annotation> numberTerms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, aNumber.getBegin(), aNumber.getEnd() );
          
          if ( numberTerms != null && !numberTerms.isEmpty() ) {
            String categories = getCategories(pJCas, numberTerms);
            if ( categories == null || (!categories.contains("time")))
              if ( !containsDate ( pJCas, numberTerms.get(0).getBegin(),  numberTerms.get(0).getEnd() ) )
                if ( !BodyFunctionAnnotator.filterBySection(pJCas, aNumber ) )
                  if (!overlapsRedaction (pJCas, aNumber ) ) {
                    pQualifiers.add( createBodyFunctionQualifierToken(pJCas, numberTerms.get(0).getBegin(),  numberTerms.get(0).getEnd() ));  
                    returnVal = true;
                  }
          }
        }
    
    return returnVal;
  } // end Method LookForRegularExpressionBodyFunctionQualifiersInUtterance() ------



// =================================================
/**
 * getCategories returns the concatenated categories of each of the terms
 * @param pJCas
 * @param pTerms
 * @return String
*/
// =================================================
private String getCategories(JCas pJCas, List<Annotation> pTerms) {
  
  String returnVal = null;
  StringBuffer categories = new StringBuffer();
  if ( pTerms != null )

    for ( Annotation term : pTerms ) {
      String aSemanticType =  ((LexicalElement) term ).getSemanticTypes ();
      if ( aSemanticType == null ) {
        StringArray aSemanticTypez = ((LexicalElement) term ).getOtherFeatures();
        if ( aSemanticTypez != null)
          aSemanticType = UIMAUtil.stringArrayToString(aSemanticTypez);
      }
      categories.append(":" + aSemanticType );
    }
  
  if ( categories.length() > 0)
    returnVal = categories.toString();
  
  return returnVal;
} // end Method getCategories() --------------------


// =================================================
/**
 * containsDate returns true if this span contains a date annotation
 * 
 * @param pJCas
 * @param pBegin
 * @param pEnd
 * @return boolean
*/
// =================================================
private boolean containsDate(JCas pJCas, int pBegin, int pEnd) {
 
  boolean returnVal = false;
  
  List<Annotation> dates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Date.typeIndexID, pBegin, pEnd );
 
  if ( dates != null && !dates.isEmpty())
    returnVal = true;
  else {
    List<Annotation> times = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pBegin, pEnd );
    
    if ( times!= null && !times.isEmpty() ) {
      for ( Annotation possibleTime : times ) {
        StringArray otherFeaturez = (( LexicalElement) possibleTime ).getOtherFeatures();
        if ( otherFeaturez != null ) {
          String possibleTime2 = UIMAUtil.stringArrayToString(otherFeaturez );
          if ( possibleTime2 != null && possibleTime2.toLowerCase().contains("time" ) ) {
            returnVal = true;
            break;
          }
          
        }
      }
    }
  }
    
  return returnVal;
  
} // end Method containsDate() ---------------------


// =================================================
/**
 * lookForDictionaryBodyFucntionQualifersinUtterance
 * looks for terms within the utterance labeled as
 * "Qualifier"
 * 
 * @param pJCas
 * @param pUtterance
 * @param pQualifiers 
 * @return boolean true if a qualifier was found
 * @throws Exception 
*/
// =================================================
private final boolean lookForDictionaryBodyFucntionQualifersinUtterance(JCas pJCas, Annotation pUtterance, List<Annotation> pQualifiers) throws Exception {
  List<Annotation> terms = UIMAUtil.getAnnotationsBySpan( pJCas, LexicalElement.typeIndexID, pUtterance.getBegin(), pUtterance.getEnd());
  boolean returnVal = false;
  if ( terms != null && !terms.isEmpty()) 
    for ( Annotation term : terms ) 
      if ( term != null ) {
       String buff = term.getCoveredText();
   
    
        String categories = ((LexicalElement) term ).getSemanticTypes();
        if ( categories != null && categories.contains("Qualifier") && !categories.contains("time") )
          if ( !BodyFunctionAnnotator.filterBySection( pJCas, ((LexicalElement) term ) ) )  
            if (!overlapsRedaction (pJCas, term ) ) {
              pQualifiers.add( createBodyFunctionQualifier(pJCas, term ));
              returnVal = true;
            }
      }
    
  return returnVal;
} // end Method lookForDictionaryBodyFucntionQualifersinUtterance() ---



// =================================================
/**
 * overlapsRedaction returns true if this term overlaps 
 * a redacted area of text
 * 
 * @param pJCas
 * @param pTerm
 * @return boolean
*/
// =================================================
  private final boolean overlapsRedaction(JCas pJCas, Annotation pTerm) {
 
    boolean returnVal = false;
    
    List<Annotation> redactions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Redacted.typeIndexID, pTerm.getBegin(), pTerm.getEnd() );
    
    if ( redactions != null && !redactions.isEmpty())
      returnVal = true;
    
    return returnVal;
} // end Method overlapsRedaction() ---------------


//=================================================
/**
* LookForUnitOfMeasureBodyFunctionQualifiersInUtterance
* looks for terms within the utterance labeled as
* "UnitOfMeasure"
* 
* @param pJCas
* @param pUtterance
 * @param pQualifiers 
* @return boolean true if a qualifier was found
 * @throws Exception 
*/
//=================================================
private final boolean LookForUnitOfMeasureBodyFunctionQualifiersInUtterance(JCas pJCas, Annotation pUtterance, List<Annotation> pQualifiers) throws Exception {

   List<Annotation> terms = UIMAUtil.getAnnotationsBySpan( pJCas, UnitOfMeasure.typeIndexID, pUtterance.getBegin(), pUtterance.getEnd());
   boolean returnVal = false;
   if ( terms != null && !terms.isEmpty()) 
     for ( Annotation term : terms ) {
       
       if ( !containsDate( pJCas, term.getBegin(), term.getEnd()) ) 
         if ( !BodyFunctionAnnotator.filterBySection( pJCas, term  ) ) {
         
         pQualifiers.add( createBodyFunctionQualifier(pJCas, term ));
         returnVal = true;
       }
     }
   return returnVal;
} // end Method LookForUnitOfMeasureBodyFunctionQualifiersInUtterance() ---



// =================================================
/**
 * filterOutPainQualifiers looks for the word "pain" within a window of
 * the utterance - if pain is within this window, this
 * qualifier is not a body function qualifier, but a pain qualifier.
 * 
 * @param pJCas
*/
// =================================================
private final void filterOutPainQualifiers(JCas pJCas) {
  
  List<Annotation> qualifiers = UIMAUtil.getAnnotations( pJCas, BFQualifier.typeIndexID);

  
  if ( qualifiers != null && !qualifiers.isEmpty()) 
    
    for ( Annotation qualifier : qualifiers ) 
        filterOutPainQualifiers( pJCas, qualifier);
  
} // end Method filterOutPainQualifiers() ----------


// =================================================
/**
 * filterOutPainQualifiers looks for the terms that have the category
 * of pain, age or time
 *
 * the utterance - if so, removes this qualifier.
 * 
 * @param pJCas
 * @param pQualifier
*/
// =================================================
private final void filterOutPainQualifiers(JCas pJCas, Annotation pQualifier) {
 
  Annotation utterance = getUtteranceFromAnnotation( pJCas,  pQualifier) ;
  
  String qualifier = pQualifier.getCoveredText();
 
  
  
  if ( utterance != null ) {
   
    String utteranceString  = utterance.getCoveredText();
    boolean andNotSeen = false;
    if ( utteranceString.toLowerCase().contains(" and ") ) {// || utteranceString.toLowerCase().contains(", ") || utteranceString.toLowerCase().contains("; ")) {
     
      // ---------------------------
      //  tbd
      // limit the scope for a confounding term to be within the bounds of a phrase   
      //                                                             don't block --->         xxxx confounding term xxxx , xxxx qualifier
      //                                                             don't block --->       qualifier xxxx , xxxx confounding term xxxxx
      //                                                             block->                 qualifier xxxx counfounding term xxxxx
      andNotSeen = true;
    }
    
    List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, utterance.getBegin(), utterance.getEnd());
    if ( terms != null && !terms.isEmpty()) {
      for ( Annotation term : terms ) {
        String categoriez = ((LexicalElement) term ).getSemanticTypes() ;
        
        String buff = term.getCoveredText();
     
      
        if ( categoriez != null ) {
          String[] categories = U.split(categoriez, ":" );
          
          if ( categories != null && categories.length > 0 ) 
            for ( String category : categories ) {
              if ( 
                  !category.toLowerCase().contains("page") && 
                  
                  ( (category.toLowerCase().contains("pain") &&  !andNotSeen ) ||             // taking this out catches 15 more tps at the cost of 67 more fp's
                   category.toLowerCase().contains("coordination") && !andNotSeen )  ||
                   category.toLowerCase().contains("confoundingterm" ) ||
                   category.contains("age") // ||
              //     category.toLowerCase().contains("time") 
                   ) 
                 
                    {
              
                if ( withinContextWindow( pJCas, term, pQualifier )) {
                    pQualifier.removeFromIndexes();
                    return;
                }
              }
              
            }
        }
      }
    }
  }
  
} // end Method filterOutPainQualifiers() -----------


// =================================================
/**
 * filterOutBadContextQualifiers
 *  Weed out historical/conditional/negated/subjective/in the wrong section annotations
 * 
 * @param pJCas
*/
// =================================================
private final void filterOutBadContextQualifiers(JCas pJCas) {

 List<Annotation> qualifiers = UIMAUtil.getAnnotations( pJCas, BFQualifier.typeIndexID);

  
  if ( qualifiers != null && !qualifiers.isEmpty()) 
    
    for ( Annotation qualifier : qualifiers ) 
        filterOutBadContextQualifier( pJCas, qualifier);
  
  
} // end Method filterOutBadContextQualifier() ---


// =================================================
/**
 * filterOutBadContextQualifier 
 * 
 * @param pJCas
 * @param pQualifier
*/
// =================================================
private void filterOutBadContextQualifier(JCas pJCas, Annotation pQualifier) {
  
   boolean filterOut = false;
   String assertionStatus = ((BFQualifier) pQualifier).getAssertionStatus();
   boolean historical = ((BFQualifier) pQualifier).getHistoricalStatus();
   boolean conditional = ((BFQualifier) pQualifier).getConditionalStatus();
   boolean subjective = ((BFQualifier) pQualifier).getAttributedToPatient();
   // String aboutPatient = ((BFQualifier) pQualifier).getSubjectStatus();
   
   
   
   /*
   if (assertionStatus != null && assertionStatus.toLowerCase().contains("negated"))
     filterOut = true;
   else 
     */
   
     if ( historical || conditional || subjective)
     filterOut = true;
   
   if ( filterOut )
     pQualifier.removeFromIndexes(pJCas);
   
     
} // end Method filterOutBadContextQualifier() -----


// =================================================
/**
 * withinContextWindow returns true if these annotations
 * are within two tokens of each other
 * 
 * @param pJCas
 * @param pTerm
 * @param pQualifier
 * @return boolean 
*/
// =================================================
private boolean withinContextWindow(JCas pJCas, Annotation pTerm, Annotation pQualifier) {
  
  boolean returnVal = false;
 
  List<Annotation> wordTokens1 = UIMAUtil.getAnnotationsBySpan( pJCas, WordToken.typeIndexID, pTerm.getBegin(), pTerm.getEnd() );
  List<Annotation> wordTokens2 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID, pQualifier.getBegin(), pQualifier.getEnd() );
  
  try {
  int wordToken1 = ((WordToken)wordTokens1.get(0)).getTokenNumber();
  int wordToken2 = ((WordToken)wordTokens2.get(0)).getTokenNumber();
  
  int diff = wordToken2 - wordToken1 ;
  if ( Math.abs( diff ) < 6)
    returnVal = true;
  } catch (Exception e) {
    // System.err.println("here");
  }
  
  
  return returnVal;
}


// =================================================
  /**
   * mergeQualifiers merges contigouus qualifers into one
   * 
   * @param pJCas
  */
  // =================================================
 private final void mergeQualifiers(JCas pJCas) {
   
   List<Annotation> qualifiers = UIMAUtil.getAnnotations( pJCas, BFQualifier.typeIndexID);

   
   if ( qualifiers != null && !qualifiers.isEmpty()) 
     
     for ( int i = 0; i < qualifiers.size() -1; i++ ) {
      
       Annotation qualifier1 = qualifiers.get(i);
       Annotation qualifier2 = qualifiers.get(i+1);
       
       if ( qualifier1.getEnd() +2 <= qualifier1.getBegin() ) {
         mergeQualifiers( pJCas, qualifier1, qualifier2);
         i++;  // <--- slight bug here will merge ab  not abc
       }
       
     }
   
    
  } // end mergeQualifiers() -----------------------


// =================================================
/**
 * mergeQualifiers creates a new qualifier, and removes
 * the qualifiers that made up the new one
 * 
 * @param pJCas
 * @param qualifier1
 * @param qualifier2
*/
// =================================================
private final void mergeQualifiers(JCas pJCas, Annotation qualifier1, Annotation qualifier2) {

  
  createBodyFunctionQualifierToken( pJCas, qualifier1.getBegin(), qualifier2.getEnd());
  
  qualifier1.removeFromIndexes();
  qualifier2.removeFromIndexes();
  
} // end Method mergeQualifiers() ------------------


  // =================================================
  /**
   * getUtteranceFromAnnotation returns the utterance (SlotValue|Sentence|SectionName?|Table|Figure|CheckBox|
   * 
   * It won't return the lower level utterances of dependentContent or ContentHeading 
   * 
   * @param pJCas
   * @param pTerm   (this can be any Annotation)
   * @return Annotation
  */
  // =================================================
  public static Annotation getUtteranceFromAnnotation(JCas pJCas, Annotation pTerm) {
    Annotation utterance = null;
    
    List<Annotation> slotValuez = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SlotValue.typeIndexID, pTerm.getBegin(), pTerm.getEnd() );
    
    if ( slotValuez != null && !slotValuez.isEmpty())
      utterance = slotValuez.get(0);
    else {
      List<Annotation> sentencez = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, pTerm.getBegin(), pTerm.getEnd() );
    
    if ( sentencez != null && !sentencez.isEmpty())
      utterance = sentencez.get(0);
    else {
      
      List<Annotation> sectionNamez = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SectionName.typeIndexID, pTerm.getBegin(), pTerm.getEnd() );
      
      if ( sectionNamez != null && !sectionNamez.isEmpty())
        utterance = sectionNamez.get(0);
      else {
        // [TBD - Table|Figure|CheckBox  - these are not created yet ]
      }
    }
    }
    return utterance;
  } // end Method getUtteranceFromAnnotation) -------


// =================================================
  /**
   * createBodyQualifier 
   * 
   * @param pJCas
   * @param pTerm
   * @return Qualifier
  */
  // =================================================
  private final BFQualifier createBodyFunctionQualifier(JCas pJCas, Annotation pTerm) {
   
    
    BFQualifier statement = null;
    List<Annotation> existingQualifiers = UIMAUtil.getAnnotationsBySpan(pJCas, BFQualifier.typeIndexID, pTerm.getBegin(), pTerm.getEnd() );
    
    if ( existingQualifiers != null && !existingQualifiers.isEmpty() ) 
      statement = (BFQualifier) existingQualifiers.get(0);
    else {
    statement = new BFQualifier( pJCas);
    statement.setBegin( pTerm.getBegin());
    statement.setEnd(   pTerm.getEnd() );
  
  
    StringArray cuiz = null;
    String cuis = null;
    cuiz = ((LexicalElement) pTerm).getEuis();
    if ( cuiz != null && cuiz.size() > 0 )
      cuis = VUIMAUtil.convertStringArrayToDelimitedString(cuiz.toArray());
    
    String conceptNames =  ((LexicalElement) pTerm).getCitationForm();
    String semanticTypes =  ((LexicalElement) pTerm).getSemanticTypes();
  
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr++);
    statement.setCuis( cuis);
    statement.setCategories(semanticTypes);
    statement.setConceptNames(conceptNames);
    
    statement.setConditionalStatus( ((LexicalElement) pTerm).getConditional() );
    statement.setSubjectStatus( ((LexicalElement) pTerm).getSubject());
    String assertionStatus = ((LexicalElement) pTerm).getNegation_Status() ;
    statement.setAssertionStatus( assertionStatus);
    statement.setHistoricalStatus( ((LexicalElement) pTerm).getHistorical() );
    
    StringArray otherFeatures = ((LexicalElement)pTerm).getOtherFeatures();
    StringBuffer polarities = new StringBuffer();
    if ( otherFeatures != null && otherFeatures.size() > 0)
      for ( int i = 0; i < otherFeatures.size(); i++ ) {
        String feature = otherFeatures.get(i);
        if ( feature.contains("-1|") || feature.endsWith("|-1") ) 
          polarities.append("-1:");
        else if  ( feature.startsWith("0|") || feature.contains("|0|") || feature.endsWith("|0")  )
          polarities.append("0:");
        else if  ( feature.contains("+1|") || feature.endsWith("+1") ) 
          polarities.append("+1:");
        
      }
      String polarity = "0";
      if ( polarities.toString().trim().length() > 0 ) {
        polarity = polarities.toString().substring(0, polarities.length() -1);

      } else 
         polarity = assignPolarity ( pJCas, statement, statement.getCoveredText());
       
      if ( pTerm.getCoveredText().toLowerCase().contains("normal") && polarity.contains("0"))
          System.err.println("Shouldnt be here");
      
      
    statement.setPolarity( polarity);
        
      

    statement.addToIndexes();
    String sectionName = VUIMAUtil.deriveSectionName(statement);
    statement.setSectionName(sectionName);
    
    statement.setMarked(false);
    }
    
   
    return statement;
    
  } // end Method createBodyQualifier() ----------


//=================================================
 /**
  * changeNegatedPhrase changes the polarity to the opposite if the term is negated
  *     unless the term has a negation in the term itself
  * @param pJCas
  * @param pQualifier
  * @param pCategory
  * @return String ("+1|0|-1)
 */
 // =================================================
   private final String changeNegatedPhrase(JCas pJCas, Annotation pQualifier, String pCategory) {
   
     String returnVal = pCategory;
     String assertionStatus = ((BFQualifier) pQualifier).getAssertionStatus();
     
     if ( assertionStatus != null && assertionStatus.equals("Negated"))
        if ( !pQualifier.getCoveredText().startsWith("no "))
          returnVal = opositeCategory( pCategory);
    
   return returnVal;
 } // end Method changeNegatedPhrase() ------------


// =================================================
/**
 * opositeCategory 
 * @param pCategory
 * @return String
*/
// =================================================
private String opositeCategory(String pCategory) {
  
  String returnVal = "0";
  
  if ( pCategory.equals("+1"))
    returnVal = "-1";
  else if ( pCategory.equals("-1"))
    returnVal = "+1";
  
  return returnVal;
} // end Method oppositeCategory() ----------------




//=================================================
 /**
  * createBodyQualifier creates a qualifier from a token span (likely because it's a set of numbers)
  * 
  * @param pJCas
  * @param pBegin
  * @param pEnd
  * @return BQualifier
 */
 // =================================================
 private final BFQualifier createBodyFunctionQualifierToken(JCas pJCas, int pBegin, int pEnd ) {
  
  
   BFQualifier statement = new BFQualifier( pJCas);
   statement.setBegin( pBegin);
   statement.setEnd(   pEnd );
 
   
   String cuis = null;
  
   String conceptNames =  null;
   String semanticTypes =  "Qualifier";
 
   statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr++);
   statement.setCuis( cuis);
   statement.setCategories(semanticTypes);
   statement.setConceptNames(conceptNames);

   statement.addToIndexes();
   String sectionName = VUIMAUtil.deriveSectionName(statement);
   statement.setSectionName(sectionName);
   
   statement.setMarked(false);
   
   String polarity = assignPolarity ( pJCas, statement, statement.getCoveredText());
   statement.setPolarity( polarity);
   
  
   return statement;
   
 } // end Method createBodyQualifier() ----------


// =================================================
/**
 * assignPolarity divines strength and rom polarity
 * 
 * @param coveredText
 * @return String
*/
// =================================================
 private final String assignPolarity(JCas pJCas, Annotation pQualifier, String coveredText) {
  
   String returnVal = "notAssignedYet";
   
   // divine what kind of bf this qualifier came from
   // Cannot divine the qualifier type until the mention is made
   // mention is not made yet at this point.  we will
   // have qualifiers that don't get made into mentions because
   // there is no bf type.
   // 
   // String bfKind = getBFKind( pJCas, pQualifier); 
  
      
    returnVal = assignStrengthPolarity( coveredText);
    if ( returnVal.equals("notAssignedYet"))
     returnVal = assignROMPolarity( coveredText);
    if ( returnVal.equals("notAssignedYet"))
       returnVal = assignReflexPolarity( coveredText); 
    if ( returnVal.equals("notAssignedYet"))
      returnVal = "0";
   
   
  return returnVal;
} // end Method assignPolarity() -------------------


// =================================================
/**
 * getBFKind returns Strength, RangeOfMotion, Reflex, or Ambiguous  
 * 
 * @param pJCas
 * @param pQualifier
 * @return String
*/
// =================================================
 final static String getBFKind(JCas pJCas, Annotation pQualifier) {
  String returnVal = "Ambiguous";
  ArrayList<String> buff = new ArrayList<String>(3);
  
  if ( pQualifier != null ) {
   
    List<Annotation> mentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BodyFunctionMention.typeIndexID, pQualifier.getBegin(), pQualifier.getEnd());
    if ( mentions != null && !mentions.isEmpty()) {
      Annotation aMention = mentions.get(0);
      
      if ( aMention != null ) {
        List<Annotation> strengthEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Strength.typeIndexID, aMention.getBegin(), aMention.getEnd());
        List<Annotation> romEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, RangeOfMotion.typeIndexID, aMention.getBegin(), aMention.getEnd());
        List<Annotation> reflexEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Reflex.typeIndexID, aMention.getBegin(), aMention.getEnd());
          
        if ( strengthEvidences != null && !strengthEvidences.isEmpty())
          buff.add( "Strength");
        if ( romEvidences != null && !romEvidences.isEmpty())
          buff.add( "RangeOfMotion");
        if ( reflexEvidences != null && !reflexEvidences.isEmpty())
          buff.add( "Reflex");
      }
      if ( buff.isEmpty())
       System.err.println("Something wrong here :" + aMention.getCoveredText());
      else
        returnVal = U.list2String(buff, ':');
    }  
  }
  return returnVal;
} // end Method getBFKind() ------------------------


// =================================================
/**
 * assignStrengthPolarity divines the polarity from 
 * the scores 5/5 ... x/10
 * 
 * 5/5 = +1
 * x/5 = -1
 * 10/10 = +1
 *  x/10 = -1;
 * 
 * @param pValue
 * @return String
*/
// =================================================
 private final String assignStrengthPolarity(String pValue) {
  String returnVal = "notAssignedYet";
  
  if      ( pValue.contains("5/5") )  
    returnVal = "+1";
  else if ( pValue.contains("/5" ))  
    returnVal = "-1";
  else if ( pValue.contains("10/10")) 
    returnVal = "+1";
  else if ( pValue.contains("/10"))  
    returnVal = "-1";
  
  
  return returnVal;
} // end Method assignStrengthPolarity() -----------


// =================================================
/**
 * assignROMPolarity XX degrees - most of these are
 * going to be -1;
 * 
 * @param coveredText
 * @return String
*/
// =================================================
private final String assignROMPolarity(String coveredText) {
  
  String returnVal = "notAssignedYet";
  
  if ( coveredText.toLowerCase().contains("degrees" ) || coveredText.toLowerCase().contains("deg" ) ) {
    if ( coveredText.toLowerCase().contains("positive" ) )
      returnVal = "0";
     else if (coveredText.contains("45") || coveredText.contains("50" ))
      returnVal = "+1";
     else 
       returnVal = "-1";
  } else if ( coveredText.toLowerCase().contains("mild" ) ||
      coveredText.toLowerCase().contains("limit" ) ||
      coveredText.toLowerCase().contains("lack" ) ||
      coveredText.toLowerCase().contains("and" ) ||
      coveredText.toLowerCase().contains("improved" ))
      returnVal = "-1";

  
  return returnVal;
} // end Method assignROMPolarity() ----------------


// =================================================
/**
 * assignReflexPolarity
 * 0 = no response; always abnormal
 * 1+ = a slight but definitely present response; may or may not be normal
 * 2+ = a brisk response; normal
 * 3+ = a very brisk response; may or may not be normal
 * 4+ = a tap elicits a repeating reflex (clonus); always abnormal
 * 
 * @param coveredText
 * @return String
*/
// =================================================
private String assignReflexPolarity(String coveredText) {
  String returnVal = "-1";
  
  if ( coveredText.contains("0" ))
      returnVal = "-1";
  else if ( coveredText.contains("1") || coveredText.contains("1+") ||  coveredText.contains("+1" ))
    returnVal = "-1";
  else if ( coveredText.contains("2") || coveredText.contains("2+")  || coveredText.contains("+2"))
    returnVal = "+1";
  else if ( coveredText.contains("3") || coveredText.contains("3+")  || coveredText.contains("+3"))
    returnVal = "0";
  else if ( coveredText.contains("4") || coveredText.contains("4+")  || coveredText.contains("+4"))
    returnVal = "-1";
  else if ( coveredText.toLowerCase().contains("clonus"))
    returnVal = "-1";
  
  
  
  return returnVal;
}


// =================================================
/**
 * assignAmbiguousPolarity [TBD] summary
 * 
 * @param coveredText
 * @return
*/
// =================================================
private String assignAmbiguousPolarity(String coveredText) {
  String returnVal = "0";
  return returnVal;
}


//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  
  
  
  
} // end Class LineAnnotator() ---------------

