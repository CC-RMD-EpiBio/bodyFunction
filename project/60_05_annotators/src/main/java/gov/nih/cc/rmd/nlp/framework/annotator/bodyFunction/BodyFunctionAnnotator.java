/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing the java-nlp-framework code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *     When attributing the Body Function code, please make reference to:
 *         Divita G, Lo J, Zhou C, Coale K, Rasch E. Extracting Body Function from Clinical Text. 
 *         InAI4Function@ IJCAI 2021 (pp. 22-35).
 *      
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
// =================================================
/**
 * BodyFunctionAnnotator creates Body Function annotations
 * that span utterances that have body function evidence
 * in it - body location, range of motion, strength, reflex
 * quantification
 * 
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.BFQualifier;
import gov.nih.cc.rmd.framework.BodyFunction;
import gov.nih.cc.rmd.framework.BodyFunctionMention;
import gov.nih.cc.rmd.framework.BodyLocation;
import gov.nih.cc.rmd.model.SectionName;
import gov.nih.cc.rmd.model.SemanticWindow;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.Concept;





public class BodyFunctionAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  public static final String BodyFunction_LRAGR_FILES  =   
      "resources/vinciNLPFramework/bodyFunction/bodyLocation/bodyLocation.lragr"  + ":" +
      "resources/vinciNLPFramework/bodyFunction/bodyStrength/bodyStrength.lragr"   + ":" + 
      "resources/vinciNLPFramework/bodyFunction/RangeOfMotion.lragr" + ":" +
      "resources/vinciNLPFramework/bodyFunction/bodyReflex.lragr" + ":" + 
      "resources/vinciNLPFramework/bodyFunction/balanceAndCoordination.lragr" + ":" + 
      "resources/vinciNLPFramework/bodyFunction/pain.lragr" + ":" + 
      "resources/vinciNLPFramework/bodyFunction/nonBodyFunction.lragr" + ":" + 
      "resources/vinciNLPFramework/bodyFunction/qualifiers/BodyFunctionQualifiersOntologyTerminologyV1.lragr" ;
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * BodyFunctions from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
      this.performanceMeter.startCounter();

      processOddMentions( pJCas);
      
      uniqueSentences ( pJCas);
      
      List<Annotation> allEvidences = setBodyFunctionEvidenceMarkersToFalse( pJCas);
      
      List<Annotation> bodyFunctionUtteranceWithBodyFunctions = getBodyFunctionTypeEvicences( pJCas);
      
      
      if ( bodyFunctionUtteranceWithBodyFunctions != null && !bodyFunctionUtteranceWithBodyFunctions.isEmpty() ) {
        
        for ( Annotation utterance : bodyFunctionUtteranceWithBodyFunctions ) {
          List<Annotation> evidences = getBodyFunctionEvidenceFromUtterance( pJCas, utterance );
          
          int beginOffset = evidences.get(0).getBegin();
          int endOffset = evidences.get(  evidences.size() -1).getEnd();
          
          if ( !attributedToThePatient( pJCas, beginOffset, endOffset )) {
            // make sure there is at least a body function type annotation in the evidences
          //  if ( evidencesIncludeBodyFunctionType( evidences ))
              createBodyFunctionMention( pJCas, beginOffset, endOffset , evidences);
          }
          

          // ------------------------------------
          // Handle grip strength
          //   create body location from grip strength only if 
          //   there are no other body locations in the utterance
          // ------------------------------------
         // filterOutGripStrengthBodyLocationMentions(pJCas, utterance);
          
          setBodyFunctionEvidenceMarkersToTrue( evidences);
          
          
        }
      }
      
     
      
    
      // remove the evidences that were not used
      if ( allEvidences != null && !allEvidences.isEmpty())
        for ( Annotation evidence : allEvidences ) {
          if ( ((BodyFunction)evidence).getParent() == null )
            if (  !checkForBodyFunctionContext( evidence ))
              evidence.removeFromIndexes();
            else 
              ;;
        }
        //  if ( !((Concept) evidence).getMarked())
        //   evidence.removeFromIndexes();
        //
      // -----------------------------------
      // do some repair here - look for qualifiers that are in mentions, but do not have
      // bfkinds to them.  
       repairOrphanMentions( pJCas );
      
     
      // ---------------------------------
      // unique the set of body function mentions
      // if there is a mention that covers another mention, take the larger mention
      // remove the smaller mention
      uniqTheMentions( pJCas);
      
     
      
      this.performanceMeter.stopCounter();
    
    } catch (Exception e)  {
        e.printStackTrace();
        System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     //   throw new AnalysisEngineProcessException();
    }
    
    } // end Method process() ----------------
           
  
// =================================================
  /**
   * evidencesIncludeBodyFunctionType returns true if any of the evidences includes a strength, rom, reflex
   * piece of evidence
   * 
   * @param pEvidences
   * @return boolean
  */
  // =================================================
  private boolean evidencesIncludeBodyFunctionType(List<Annotation> pEvidences) {
   
    boolean returnVal = false;
    
    if ( pEvidences != null && !pEvidences.isEmpty())
      for ( Annotation evidence : pEvidences ) {
        String aType = evidence.getClass().getCanonicalName().toLowerCase();
        if ( aType.contains("strength") ||
             aType.contains("rom") ||
             aType.contains("reflex")) {
              returnVal = true;
              break;
        }
        
      }
    
    return returnVal;
  } // end Method evidencesIncludeBodyFunctionType() ----


// =================================================
  /**
   * checkForBodyFunctionContext returns true if
   * this orphan piece of evidence would become 
   * a mention because it's in a relevant section
   * 
   * 
   * @param pSomeEvidence
   * @return boolean
  */
  // =================================================
  private boolean checkForBodyFunctionContext(Annotation pSomeEvidence) {
    boolean returnVal = false;
    
   
    
    
    return returnVal;
  } // end Method checkForBodyFunctionContext() ----


// =================================================
  /**
   * repairOrphanMentions
   * 
   * @param pJCas
  */
  // =================================================
  private final void repairOrphanMentions(JCas pJCas) {
    
    List<Annotation> qualifiers = UIMAUtil.getAnnotations(pJCas, BFQualifier.typeIndexID);
    if ( qualifiers != null && !qualifiers.isEmpty())
      for ( Annotation aQualifier : qualifiers ) {
        String kind =  BodyFunctionQualifierAnnotator.getBFKind(pJCas, aQualifier);
        if ( kind == null || kind.equals("Ambiguous")) {
          repairQualifier( pJCas, aQualifier);
        } // orphan spotted
      } // loop through qualifiers
    
  } // end Method repairOrphanMentions() ----------


// =================================================
/**
 * repairQualifier 
 * 
 * @param pJCas
 * @param aQualifier
*/
// =================================================
  private void repairQualifier(JCas pJCas, Annotation aQualifier) {
  
    // find the closest mention to the left of the qualifier
    // expand the length of the mention to include this qualifier
    // point the qualifier parent to this mention
    Annotation mention = null;
    int begin = aQualifier.getBegin() -4;
    int anEnd = aQualifier.getBegin() -1;
    String id = VUIMAUtil.getDocumentId(pJCas);
    String buff = pJCas.getDocumentText();
    
    List<Annotation>mentions = null;
    System.err.println("looking at " + id + ":" + aQualifier.getCoveredText() + ":" + aQualifier.getBegin() );
    while ( (mentions == null || mentions.isEmpty() ) && begin > 0 && begin >= aQualifier.getBegin() -20 ) {
      mentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BodyFunctionMention.typeIndexID, begin, anEnd );
      if ( mentions == null || mentions.isEmpty() )
        begin = begin -2;
    //  System.err.println("backtracking to "  + "|" + buff.substring( begin, aQualifier.getEnd()) + "|");
    }
    
    if ( mentions != null && !mentions.isEmpty()) {
      Annotation aMention = mentions.get(0);
      aMention.setEnd( aQualifier.getEnd());
      System.err.println("the new mention now is |" + aMention.getCoveredText());
      ((BodyFunction) aQualifier).setParent( (Concept) aMention);  
     
    } else {
      
    
       begin = aQualifier.getEnd() +2;
       anEnd = begin + 2;
       int eof = pJCas.getDocumentText().length();
       while ( (mentions == null || mentions.isEmpty() ) && anEnd < eof && anEnd < aQualifier.getEnd() + 20 ) {
        mentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BodyFunctionMention.typeIndexID, begin, anEnd );
        if ( mentions == null || mentions.isEmpty() )
          anEnd = anEnd +2;
        System.err.println("going forward to "  + "|" + buff.substring( begin, anEnd) + "|");
      }
      
      if ( mentions != null && !mentions.isEmpty()) {
        Annotation aMention = mentions.get(0);
        aMention.setBegin( aQualifier.getBegin());
       //  System.err.println("the new mention now is |" + aMention.getCoveredText());
        ((BodyFunction) aQualifier).setParent( (Concept) aMention);  
       
      } else {
        aQualifier.removeFromIndexes();
      }
      
      
    }
    
    
   
    
    
  
  } // end Method repairQualifier() ---------------


//=================================================
 /**
  * uniqTheMentions sorts, uniq's and takes the larger of overlapping mentions
  * 
  * @param pJCas
 */
 // =================================================
 private final void uniqTheMentions(JCas pJCas) {
   uniqTheMentions1(pJCas );
   uniqTheMentions2(pJCas );
   
 } // end Method uniqTheMentions() -----------------
  // =================================================
  /**
   * uniqTheMentions sorts, uniq's and takes the larger of overlapping mentions
   * 
   * @param pJCas
  */
  // =================================================
  private final void uniqTheMentions1(JCas pJCas) {
  
    List<Annotation> bfms = UIMAUtil.getAnnotations(pJCas, BodyFunctionMention.typeIndexID);
    if ( bfms != null ) {
      UIMAUtil.uniqueAnnotations(bfms);
      UIMAUtil.sortByOffset(bfms);
      
     
      Annotation previousAnnotation = null;
      for ( Annotation annotation: bfms ) {
        
        if ( annotation.getBegin() == 1030) 
          System.err.println("1030");
       if ( previousAnnotation != null &&  annotation.getEnd() == previousAnnotation.getEnd() &&
             annotation.getBegin() > previousAnnotation.getBegin() ) 
        annotation.removeFromIndexes();
         
       
       
       previousAnnotation = annotation;
      }
    }
    
    
  } // end Method uniqTheMentions() ------------------

  // =================================================
  /**
   * uniqTheMentions sorts, uniq's and takes the larger of overlapping mentions
   * 
   * @param pJCas
  */
  // =================================================
  private final void uniqTheMentions2(JCas pJCas) {
  
    List<Annotation> bfms = UIMAUtil.getAnnotations(pJCas, BodyFunctionMention.typeIndexID);
    if ( bfms != null ) {
      UIMAUtil.uniqueAnnotations(bfms);
      UIMAUtil.sortByOffset(bfms);
      
     
      Annotation previousAnnotation = null;
      for ( Annotation annotation: bfms ) {
        
        if ( annotation.getBegin() == 1030) 
          System.err.println("1030");
       if ( previousAnnotation != null &&  annotation.getBegin() == previousAnnotation.getBegin() &&
             annotation.getEnd() < previousAnnotation.getEnd() ) 
         previousAnnotation.removeFromIndexes();
       
       previousAnnotation = annotation;
      }
    }
    
    
  } // end Method uniqTheMentions2() ------------------
  // =================================================
  /**
   * processOddMentions does a dictionary lookup for 
   * body function mentions that do not have the usual 
   * elements of body function type, location, and qualifiers
   * 
   * 
   * @param pJCas
   * @throws Exception 
  */
  // =================================================
   private void processOddMentions(JCas pJCas) throws Exception {
   
     
     List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID );
     
     if ( terms != null && !terms.isEmpty())
       for ( Annotation term: terms ) {
         
         String categories = ((LexicalElement) term ).getSemanticTypes();
         if ( categories != null && categories.contains("BodyFunctionMention"))
           if ( 
               !((LexicalElement) term ).getSubject().contains("other")   &&
             !BodyFunctionAnnotator.filterBySection( pJCas, ((LexicalElement) term ) ) )
             createBodyFunction(pJCas, term );
       }
     
     // ---------------------------------------
     // picks up mentions in values of the slots
     List<Annotation> dependentContents = UIMAUtil.getAnnotations(pJCas, DependentContent.typeIndexID );
     if (dependentContents != null && !dependentContents.isEmpty() )
       for ( Annotation dependentContent : dependentContents ) {
         String buff = dependentContent.getCoveredText(); 
         if (buff != null &&  
            ( buff.toLowerCase().contains("musculoskeletal:") || buff.toLowerCase().contains("neurological:")) &&
                !buff.toLowerCase().contains("detailed exam") )
                
                createBodyFunctionAux(pJCas, dependentContent.getBegin(), dependentContent.getEnd() );
       }
     
     // ---------------------------------------
     // picks up mentions that are sectionNames : sentence
     List<Annotation> sectionNames = UIMAUtil.getAnnotations(pJCas, SectionName.typeIndexID );
     if (sectionNames != null && !sectionNames.isEmpty() )
       for ( Annotation sectionName : sectionNames ) {
         String buff = sectionName.getCoveredText(); 
         if ( buff != null && buff.toLowerCase().contains("musculoskeletal:") || buff.toLowerCase().contains("neurological:") ) { 
           // get the next sentence
           List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Sentence.typeIndexID,  sectionName.getEnd() + 3,  sectionName.getEnd() + 5 );
           if ( sentences != null && !sentences.isEmpty() ) {
             String sentenceText = sentences.get(0).getCoveredText();
             if ( sentenceText.toLowerCase().contains("positive") ||
                 sentenceText.toLowerCase().contains("negative") ||
                 sentenceText.toLowerCase().contains("normal") )
               createBodyFunctionAux(pJCas, sectionName.getBegin(), sentences.get(0).getEnd() );
           }
         }
          
       }
     
     // ---------------------------------------
     // picks up mentions that are in sentences
     List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID );
     if (sentences != null && !sentences.isEmpty() )
       for ( Annotation sentence : sentences ) {
         String buff = sentence.getCoveredText(); 
         if ( buff != null && buff.toLowerCase().contains("musculoskeletal negative") || buff.toLowerCase().contains("neurological positive") ) 
           createBodyFunctionAux(pJCas, sentence.getBegin(), sentence.getEnd() );
       }
   
  }

  // =================================================
  /**
   * attributedToThePatient finds the terms between these offsets to see
   * if any of the terms are attributed to the patient.  We don't want to
   * trust mentions that are from the patient.
   * 
   * @param pJCas
   * @param pBeginOffset
   * @param pEndOffset
   * @return boolean
  */
  // =================================================
  private boolean attributedToThePatient(JCas pJCas, int pBeginOffset, int pEndOffset) {
    boolean returnVal = false;
    
    
    List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pBeginOffset, pEndOffset);
    
    
    if ( terms != null && !terms.isEmpty()) 
      for (Annotation term: terms ) 
        if ( ((LexicalElement)term).getAttributedToPatient() ) {
          returnVal = true;
          break;
        }
      
    return returnVal;
  } // end Method attributedToThePatient() -----------

  // =================================================
  /**
   * uniqueSentences 
   * 
   * @param pJCas
  */
  // =================================================
   private final void uniqueSentences(JCas pJCas) {
    
     
     List<Annotation> sentences = UIMAUtil.getAnnotations( pJCas, Sentence.typeIndexID);
     if ( sentences != null )
       UIMAUtil.uniqueAnnotations(sentences);
    
  }

  // =================================================
  /**
   * filterOutGripStrengthBodyLocationMentions removes grip strength
   * from utterance as a body location if there are other body location
   * mentions in the utterance
   * 
   * @param pJCas 
   * 
   * @param pUtterance
  */
  // =================================================
  private void filterOutGripStrengthBodyLocationMentions(JCas pJCas, Annotation pUtterance) {
   
    List<Annotation> bodyLocations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BodyLocation.typeIndexID,  pUtterance.getBegin(), pUtterance.getEnd() );

    if ( bodyLocations != null && !bodyLocations.isEmpty() && bodyLocations.size() > 1) {
     
      for ( Annotation bodyLocation: bodyLocations ) {
        String buff = bodyLocation.getCoveredText().toLowerCase();
        if ( buff.contains("grip strength"))
          bodyLocation.removeFromIndexes();
      }
    
    }
    
  } // end Method filterOutGripStrengthBodyLocationMentions() ----

  // =================================================
  /**
   * setBodyFunctionEvidenceMarkersToFalse turns the marks
   * to false so that that those that are within
   * body function mentions can be turned on
   * And the remaining false ones to be removed
   * 
   * @param pJCas
  */
  // =================================================
   private final List<Annotation> setBodyFunctionEvidenceMarkersToFalse(JCas pJCas) {
   
     List<Annotation>bodyLocations = setBodyFunctionEvidenceMarkersToFalse( pJCas, BodyLocation.typeIndexID);
     List<Annotation>bodyFunctions = setBodyFunctionEvidenceMarkersToFalse( pJCas, BodyFunction.typeIndexID);
     List<Annotation>qualifiers    = setBodyFunctionEvidenceMarkersToFalse( pJCas, BFQualifier.typeIndexID);
    
     List<Annotation> allEvidences = new ArrayList<Annotation> ();
     allEvidences.addAll( bodyLocations);
     allEvidences.addAll( bodyFunctions);
     allEvidences.addAll( qualifiers );
     
     
     return allEvidences;
     
  } // end Method setBodyFunctionEvicencesToFalse() ---

   // =================================================
   /**
    * setBodyFunctionEvidenceMarkersToTrue turns the marks
    * to true so that that those that are within
    * body function mentions can be turned on
    * And the remaining false ones to be removed
    * 
    * @param pJCas
   */
   // =================================================
    private final void setBodyFunctionEvidenceMarkersToTrue( List<Annotation> pEvidences) {
    
      if ( pEvidences !=null && !pEvidences.isEmpty())
        for ( Annotation evidence: pEvidences )
          ((Concept) evidence).setMarked( true);
      
     
   } // end Method setBodyFunctionEvicencesToTrue() ---

   
   
  // =================================================
  /**
   * setBodyFunctionEvidenceMarkersToFalse  turns the marks
   * to false so that that those that are within
   * body function mentions can be turned on
   * And the remaining false ones to be removed
   * 
   * 
   * @param pJCas
   * @param pTypeindexid
   * @return List<Annotation>
  */
  // =================================================
   private final List<Annotation> setBodyFunctionEvidenceMarkersToFalse(JCas pJCas, int pTypeindexid) {
  
     
    List<Annotation> evidences = UIMAUtil.getAnnotations( pJCas, pTypeindexid, true );
    
    if ( evidences != null && !evidences.isEmpty()) 
      for ( Annotation evidence : evidences )
        ((Concept) evidence).setMarked( false);
    
    return evidences;
    
  } // end Method setBodyFunctionEvicencesToFalse() ---

  // =================================================
  /**
   * createBodyFunctionMention creates a BodyFunctionMention 
   * around the body function evidences
   * 
   * Each of the evidences get body function mention attached as a parent.
   * 
   * @param pJCas
   * @param pBeginOffset
   * @param pEndOffset
   * @param pEvidences
   * @return BodyFunctionMention
  */
  // =================================================
 private final BodyFunctionMention createBodyFunctionMention(JCas pJCas, int pBeginOffset, int pEndOffset, List<Annotation> evidences) {
   
   BodyFunctionMention statement = new BodyFunctionMention( pJCas);
   
   statement.setBegin( pBeginOffset);
   statement.setEnd(    pEndOffset);
   statement.setId( this.getClass().getName() + "_2_" + this.annotationCtr++);
   statement.addToIndexes();
   
   
   for ( Annotation evidence : evidences )
     ((BodyFunction)evidence).setParent(  statement);
   
   
   return statement;
    
  } // end Method createBodyFunctionMention() --------

  // =================================================
  /**
   * getBodyFunctionEvidenceFromUtterance returns with a list 
   * of body function evidences that are a part of this utterance
   * 
   * @param pUtterance
   * @return List<Annotation>
  */
  // =================================================
  private final List<Annotation> getBodyFunctionEvidenceFromUtterance(JCas pJCas, Annotation pUtterance) {
   
    
    List<Annotation> allAnnotations = new ArrayList<Annotation>();
    
    List<Annotation> bodyLocations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BodyLocation.typeIndexID, pUtterance.getBegin(), pUtterance.getEnd(), false );

    List<Annotation> bodyFunctionTypes = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BodyFunction.typeIndexID, pUtterance.getBegin(), pUtterance.getEnd(), true );
    
    List<Annotation> bodyFunctionQuantification = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BFQualifier.typeIndexID, pUtterance.getBegin(), pUtterance.getEnd(), true );
    if ( bodyLocations != null )
      allAnnotations.addAll( bodyLocations);
    allAnnotations.addAll( bodyFunctionTypes);
    allAnnotations.addAll( bodyFunctionQuantification );
    
     UIMAUtil.sortByOffset( allAnnotations);
    
    return allAnnotations;
  } // end Method getBodyFunctionEvidenceFromUtterance() ---

  // =================================================
  /**
   * getBodyFunctionTypeEvicences 
   * 
   * @param pJCas
   * @return List<Annotation>  uniqued
  */
  // =================================================
 private List<Annotation> getBodyFunctionTypeEvicences(JCas pJCas) {

   List<Annotation> returnVal = null;
   
   List<Annotation> bodyFunctionEvidences = new ArrayList<Annotation>();  
   // find sentences that have body function terms in them
   List<Annotation> bodyFunctionTerms = UIMAUtil.getAnnotations(pJCas, BodyFunction.typeIndexID, true );
    
    
    if ( bodyFunctionTerms != null && !bodyFunctionTerms.isEmpty())
      for ( Annotation term: bodyFunctionTerms ) {
        
        // find the sentences that have this mention in them
        // Don't consider body location - body locations are everywhere
        
        String bodyFunctionClass = term.getClass().getName();
        
       
        if (!bodyFunctionClass.contains("Location")) {
          Annotation utterance = getUtteranceFromAnnotation( pJCas, term ); 
          if ( utterance != null) {
            bodyFunctionEvidences.add(  utterance);
           // ((Utterance)utterance).setMarked(false);
          } else {
            // Occasionally, there won't be an utterance, but a semantic window
            // that covers this term
          
            System.err.println("Shouldn't be here ");
          }
        }
          
        
      }
      
 
    
    if ( bodyFunctionEvidences != null && !bodyFunctionEvidences.isEmpty()) {
       returnVal = UIMAUtil.uniqueAnnotationList(bodyFunctionEvidences);
    }
      
    return returnVal;
 
 } // end Method getBodyFunctionTypeEvicences() ----
 
    

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
          List<Annotation> semanticWindows = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SemanticWindow.typeIndexID, pTerm.getBegin(), pTerm.getEnd() );
          
          if ( semanticWindows != null && !semanticWindows.isEmpty())
            utterance = semanticWindows.get(0);
          else {
        
          // [TBD - Table|Figure|CheckBox  - these are not created yet ]
          }
          }
      }
      }
      return utterance;
    } // end Method getUtteranceFromAnnotation) -------

    // =================================================
    /**
     * filterBySection returns false if this term is in
     * a Plan/Education/goals/medications section or sections that should be
     * filtered out
     * 
     * @param pTerm
     * @return boolean 
     * @throws Exception 
    */
    // =================================================
    public static boolean filterBySection(JCas pJCas, LexicalElement pTerm) throws Exception {
      
      boolean returnVal = true;
      String sectionName = null;
   
      // is the term within a slot value?  if so, use the content heading from the slot to filter on
      List<Annotation> slotValues = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SlotValue.typeIndexID, pTerm.getBegin(), pTerm.getEnd(), false );
      if ( slotValues != null && !slotValues.isEmpty() )
        sectionName = ((SlotValue)slotValues.get(0)).getContentHeaderString();
      
      else
      
       sectionName = pTerm.getSectionName();
      
      
      
      returnVal = sectionsToFilterOut( sectionName );
     
          
      
      return returnVal;
    } // end Method filterBySection() ----------------
    
    
    // =================================================
    /**
     * isGoodBFSection returns true if this term is in 
     * a section or slot value that is known to be a
     * context for body function
     * 
     * @param pTerm
     * @return boolean 
     * @throws Exception 
    */
    // =================================================
    public static boolean isGoodBFSection(JCas pJCas, LexicalElement pTerm) throws Exception {
      
      boolean returnVal = false;
      String sectionName = null;
   
      // is the term within a slot value?  if so, use the content heading from the slot to filter on
      List<Annotation> slotValues = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SlotValue.typeIndexID, pTerm.getBegin(), pTerm.getEnd(), false );
      if ( slotValues != null && !slotValues.isEmpty() )
        sectionName = ((SlotValue)slotValues.get(0)).getContentHeaderString();
      
      else
      
       sectionName = pTerm.getSectionName();
      
      
      
      returnVal = isGoodBFSection( sectionName );
     
          
      
      return returnVal;
    } // end Method isGoodBFSection() ----------------
    
    

    // =================================================
  /**
   * isGoodBFSection returns false if this section name
   * is known to be a good context for body function
   * 
   * @param sectionName
   * @return boolean 
   * @throws Exception 
  */
  // =================================================
    public final static boolean isGoodBFSection(String pSectionName) throws Exception {
   
      
      if (_GOOD_BF_SectionsPatterns == null )
        load_BF_GoodSections();
        
      boolean returnVal = false;
      if ( pSectionName  != null && pSectionName.length() < 60 )
        for ( String pattern : _GOOD_BF_SectionsPatterns ) 
          if ( pSectionName.toLowerCase().contains( pattern ) ) {
            returnVal = true;
            break;
          }
      
      return returnVal;
    } // end Method isGoodBFSection() ---------------

  // =================================================
/**
 * sectionsToFilterOut returns false if this term is in
     * a Plan/Education/goals section
 * 
 * @param pSectionName
 * @return boolean 
 * @throws Exceptin
*/
// =================================================
  public final static boolean sectionsToFilterOut(String pSectionName) throws Exception {
 
    boolean returnVal = false;
    
    if ( _BAD_BF_SectionsPatterns == null )
      load_BF_BadSections();
    
    if ( pSectionName  != null && pSectionName.length() < 60 )
      for ( String pattern : _BAD_BF_SectionsPatterns ) 
        if ( pSectionName.toLowerCase().contains( pattern ) ) {
          returnVal = true;
          break;
        }
    
        /*
        
      sectionName.toLowerCase().contains( "goal" ) ||
      ( sectionName.toLowerCase().contains( "plan" ) && ( !sectionName.toLowerCase().contains("impression"))) ||
      // sectionName.toLowerCase().contains( "details" )||
      sectionName.toLowerCase().contains( "educat" ) ||
      sectionName.toLowerCase().contains( "refer" ) ||
      sectionName.toLowerCase().contains("intervention") ||
      sectionName.toLowerCase().contains("family history") ||
      sectionName.toLowerCase().contains("vital") ||
      sectionName.toLowerCase().contains("medication") ||
      sectionName.toLowerCase().contains("gait") ||
      sectionName.toLowerCase().contains("balance") ||
      sectionName.toLowerCase().contains("coordination") ||
      sectionName.toLowerCase().contains("mobility") ||
      sectionName.toLowerCase().contains("sitting") ||
      sectionName.toLowerCase().contains("motor learning") ||
      sectionName.toLowerCase().contains("motor function") || // Motor function (motor control, motor learning)
      sectionName.toLowerCase().contains("recommendation") ||
      sectionName.toLowerCase().contains("assessment") ||
      sectionName.toLowerCase().contains("a/p") ||
      sectionName.toLowerCase().contains("occupational profile") ||
      sectionName.toLowerCase().contains("illness") ||
      sectionName.toLowerCase().contains("fa") ||
      sectionName.toLowerCase().contains("sensation") ||
      sectionName.toLowerCase().contains("sensory") ||
      sectionName.toLowerCase().contains("history of present illness") ||
      sectionName.toLowerCase().contains("psychiatric") ||
      sectionName.toLowerCase().contains("a/p") ||
      sectionName.toLowerCase().contains("patient/significant others comments") ||
      sectionName.toLowerCase().contains("developmental history") ||
      sectionName.toLowerCase().contains("posture") ||
      sectionName.toLowerCase().contains("pain impact") ||
      sectionName.toLowerCase().contains("HPI") ||
    //  sectionName.toLowerCase().contains("neurological" ) || 
    //  sectionName.toLowerCase().contains("musculoskeletal" ) || 

  //    sectionName.toLowerCase().contains( "hist" ) ||
  //    sectionName.toLowerCase().contains( "hx" ) ||
      sectionName.toLowerCase().contains( "follow" ) ) )
      returnVal = false;
  */
    
    
  return returnVal;
} // end Method sectionsToFilterOut() ----------------

  // =================================================
    /**
     * filterBySection figures out the section this token is
     * if it can, then figures out if it is in a section
     * that should be filtered out. 
     * 
     * @param pToken
     * @return boolean
     * @throws Exception 
    */
    // =================================================
    public static boolean filterBySection(JCas pJCas, Annotation pToken) throws Exception {
      
      boolean returnVal = true;
    
      String sectionName = VUIMAUtil.getSectionName(pJCas, pToken);
      
      returnVal = sectionsToFilterOut( sectionName);
      
      return returnVal;
      
    } // end Method filterBySection() ----------------

  // =================================================
  /**
   * createBodyFunction 
   * 
   * @param pJCas
   * @param pTerm
   * @return Function
  */
  // =================================================
  private final BodyFunctionMention createBodyFunction(JCas pJCas, Annotation pTerm) {
   
    
    BodyFunctionMention statement = new BodyFunctionMention( pJCas);
    statement.setBegin( pTerm.getBegin());
    statement.setEnd(   pTerm.getEnd() );
  
  
    StringArray cuiz = null;
    String cuis = null;
    cuiz = ((LexicalElement) pTerm).getEuis();
    if ( cuiz != null && cuiz.size() > 0 )
      cuis = VUIMAUtil.convertStringArrayToDelimitedString(cuiz.toArray());
    
    String conceptNames =  ((LexicalElement) pTerm).getCitationForm();
    String semanticTypes =  ((LexicalElement) pTerm).getSemanticTypes();
    String sectionName =  ((LexicalElement) pTerm).getSectionName();
    
  
    statement.setId  ( this.getClass().getSimpleName() + "_0_" + annotationCtr);
    statement.setCuis( cuis);
    statement.setCategories(semanticTypes);
    statement.setConceptNames(conceptNames);

    statement.addToIndexes();
    if ( sectionName == null )
       sectionName = VUIMAUtil.deriveSectionName(statement);
    statement.setSectionName(sectionName);
    
    
    statement.setMarked(false);
    
    List<Annotation> evidences = findEvidences(pJCas, statement);
    if ( evidences != null && !evidences.isEmpty())
      for ( Annotation evidence: evidences )
        ((BodyFunction)evidence).setParent( statement);
    
    return statement;
    
  } // end Method createBodyFunction() ----------

  // =================================================
  /**
   * createBodyFunction 
   * This method takes the oddball cases
   * 
   * @param pJCas
   * @param pBegin
   * @param pEnd
   * @return Function
  */
  // =================================================
  private final BodyFunctionMention createBodyFunctionAux(JCas pJCas, int pBegin, int pEnd ) {
   
    
    BodyFunctionMention statement = new BodyFunctionMention( pJCas);
    statement.setBegin( pBegin );
    statement.setEnd(   pEnd );
  
  
    statement.setId  ( this.getClass().getSimpleName() + "_Aux_" + annotationCtr);
   
    statement.setCategories("BodyFunctionMention");
   
    statement.addToIndexes();
    String sectionName = VUIMAUtil.deriveSectionName(statement);
    statement.setSectionName(sectionName);
    
    statement.setMarked(false);
    
    List<Annotation>evidences = findEvidences(pJCas, statement );
    if ( evidences != null && !evidences.isEmpty() )
      for ( Annotation evidence: evidences  )
         ((BodyFunction)evidence).setParent(statement);
    
    
    return statement;
    
  } // end Method createBodyFunction() ----------




// =================================================
  /**
   * findEvidences retrieves body function evidences from
   * the boundaries of this mention - the body function mention
   * should not be one of the things returned.
   * @param pJCas
   * @param pMention
   * @return List<Annotation>
  */
  // =================================================
  private final List<Annotation> findEvidences(JCas pJCas, BodyFunctionMention pMention) {
   
    List<Annotation> returnVal = null;
    List<Annotation> evidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BodyFunction.typeIndexID, pMention.getBegin(), pMention.getEnd(), true );

    if ( evidences != null && !evidences.isEmpty() ) {
      returnVal = new ArrayList<Annotation>( evidences.size());
      for ( Annotation evidence: evidences ) {
         if ( !evidence.getClass().getName().contains( "Mention") )
           returnVal.add( evidence );
        }
    }
    return returnVal;
          
        
  } // end Method findEvidences() --------------------

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
    
    load_BF_BadSections(  );
    load_BF_GoodSections(  );
    
    
      
  } // end Method initialize() -------
 
  // =================================================
  /**
   * load_BF_BadSections loads sections to be filtered out
   * from the resource BF_BadSections.txt
   * 
   * @param pArgs
   * @param ResourceInitializationException
  */
  // =================================================
  private final static void load_BF_BadSections() throws ResourceInitializationException {
    
    
    try {
     
      if ( _BAD_BF_SectionsPatterns == null || _BAD_BF_SectionsPatterns.size() == 0 ) {
        String buff = U.readClassPathResource("resources/vinciNLPFramework/bodyFunction/BF_BadSections.txt");
      
        String[] rows = U.split(buff, "\n");
      
        for ( int i = 0; i < rows.length; i++ )
          if ( rows[i] != null && rows[i].trim().length() > 0 && !rows[i].startsWith("#"))
            _BAD_BF_SectionsPatterns.add( rows[i].trim().toLowerCase());
      }  
      } catch ( Exception e) {
        e.printStackTrace();
        GLog.error_println( "load_BF_BadSections: Issue loading the bf bad sections list" + e.toString());
        throw new ResourceInitializationException();
      }
      // System.err.println("Bad BF_sections loaded");
  } // end Method load_BF_BadSections() ----
  
  
//=================================================
 /**
  * load_BF_GoodSections loads sections to be filtered IN
  * from the resource BF_GoodSections.txt
  * 
  * @param pArgs
  * @param ResourceInitializationException
 */
 // =================================================
 private static final void load_BF_GoodSections() throws ResourceInitializationException {
   
   
   try {
    
     if ( _GOOD_BF_SectionsPatterns == null || _GOOD_BF_SectionsPatterns.size() == 0 ) {
       String buff = U.readClassPathResource("resources/vinciNLPFramework/bodyFunction/BF_GoodSections.txt");
     
       String[] rows = U.split(buff, "\n");
     
       for ( int i = 0; i < rows.length; i++ )
         if ( rows[i] != null && rows[i].trim().length() > 0 && !rows[i].startsWith("#"))
           _GOOD_BF_SectionsPatterns.add( rows[i].trim().toLowerCase());
     }  
     } catch ( Exception e) {
       e.printStackTrace();
       GLog.error_println( "load_BF_GoodSections: Issue loading the bf good sections list" + e.toString());
       throw new ResourceInitializationException();
     }
     // System.err.println("Bad BF_sections loaded");
 } // end Method load_BF_BadSections() ----

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private static List<String> _BAD_BF_SectionsPatterns = new ArrayList<String>();
  private static List<String> _GOOD_BF_SectionsPatterns = new ArrayList<String>();
  
  
  
} // end Class LineAnnotator() ---------------

