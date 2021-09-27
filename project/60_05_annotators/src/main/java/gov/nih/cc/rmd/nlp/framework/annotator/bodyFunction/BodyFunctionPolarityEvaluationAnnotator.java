// =================================================
/**
 * BodyFunctionPolarityEvaluationAnnotator 
 * 
 *   iterates through each of the gold version of the 
 *   qualifiers (Qualifier)
 *       finds if there is an overlapping copper qualifier (bfQualifier)
 *       if not
 *         count missed qualifier
 *       if so
 *         if gold qualifier polarity match
 *            Plus1  ++  tpPlus1
 *            Minu1  ++  tpMinus1
 *            Zero   ++  tpZero
 *         else if gold qualifier is 
 *            plus1 but copper is not ++  fnPlus1
 *            minus1 but copper is not ++ fnMinus1
 *            zero but copper is not ++   fnZero
 *            
 *        %Plus1 correct    tpPlus1/ tpPlus1 + fnPLus1   == that's recall
 *        %Minus1 correct   tpMinus1 / tpMinus1 + fnMinus1 
 *        %zero   correct   tpZero  / tpZero + fnZero  
 *            
 *
 * @author  Guy Divita 
 * @created May 20, 2020
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.BFQualifier;
import gov.nih.cc.rmd.gate.Qualifier;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;



public class BodyFunctionPolarityEvaluationAnnotator extends JCasAnnotator_ImplBase {
 
  

  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
      this.performanceMeter.startCounter();
      String copperCategory = null;
      
      String docId = VUIMAUtil.getDocumentId( pJCas );

      List<Annotation> goldQualifiers = UIMAUtil.getAnnotations(pJCas, Qualifier.typeIndexID);

      if (goldQualifiers != null && !goldQualifiers.isEmpty())
        for (Annotation goldQualifier : goldQualifiers) {

          String goldCategory = ((Qualifier) goldQualifier).getCategory();
          Annotation copperQualifier = getCopperQualifier(pJCas, goldQualifier);
          if (copperQualifier != null) 
             copperCategory = ((BFQualifier) copperQualifier).getPolarity();
          
          processMatch(pJCas, docId, goldQualifier, copperQualifier, goldCategory, copperCategory);

          
        }

      this.performanceMeter.stopCounter();

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", "Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }

    } // end Method process() --------------------------
  
   // =================================================
  /**
   * processMatch 
   * 
   * @param pJCas
   * @param docId
   * @param goldQualifier
   * @param copperQualifier
   * @param goldCategory
   * @param copperCategory
  */
  // =================================================
  private final void processMatch(JCas pJCas,
                                  String docId,
                                  Annotation goldQualifier, 
                                  Annotation copperQualifier, 
                                  String pGoldCategory,
                                  String pCopperCategory) {
    
    
    this.totalGold++;
    
    String goldCategory = pGoldCategory;
    String copperCategory = pCopperCategory;
    if ( goldCategory == null )
        goldCategory = "0";
    if ( goldCategory.equals("+1")) goldCategory = "1";
    if ( copperCategory == null)
      copperCategory = "0";
    if ( copperCategory.equals("+1"))
      copperCategory = "1";
    
    
    
    if ( copperQualifier != null  ) 
      this.totalCopper++;
     
          if ( goldCategory.equals( copperCategory )) {
            switch ( goldCategory ) {
              case "1" : 
              case "+1": {
                this.totalGoldPlus1++;  
                this.tpPlus1++;   
                addRow( pJCas,"tp", docId, goldQualifier, copperQualifier, goldCategory,copperCategory); 
                break;
                }
              case "-1": { this.totalGoldMinus1++; this.tpMinus1++;  addRow( pJCas, "tp",docId, goldQualifier, copperQualifier, goldCategory,copperCategory); break;}
              case "0" : { this.totalGoldZero++;   this.tpZero++;    addRow( pJCas, "tp",docId, goldQualifier, copperQualifier, goldCategory,copperCategory); break;}
            } // end Switch
          } else {
            switch ( goldCategory ) {
              case "1" :
              case "+1": {  this.totalGoldPlus1++;   this.fnPlus1++;   addRow( pJCas, "fn", docId, goldQualifier, copperQualifier, goldCategory,copperCategory); break;}
              case "-1": {  this.totalGoldMinus1++;  this.fnMinus1++;  addRow( pJCas, "fn", docId, goldQualifier, copperQualifier, goldCategory,copperCategory); break;}
              case "0" : {  this.totalGoldZero++;    this.fnZero++;    addRow( pJCas, "fn", docId, goldQualifier, copperQualifier, goldCategory,copperCategory); break;}
            } // end switch
          } // end if both gold and copper are filled out 
        
  } // end Method processMatch() ---------------------
  
  
  // =================================================
  /**
   * addRow 
   *      TP|mention|goldQualifier|goldCategory|copperCategory
   * @param pJCas
   * @param pTP_or_FN
   * @param pDocId
   * @param pGoldQualifier
   * @param pGopperQualifier
   * @param pGoldCategory
   * @param pCopperCategory
  */
  // =================================================
  private final void addRow(JCas pJCas, 
                       String pTP_or_FN,
                       String pDocId,
                       Annotation pGoldQualifier,
                       Annotation pCopperQualifier, 
                       String pGoldCategory,
                       String pCopperCategory) {
   
    String docId = U.spacePadRight(15, pDocId);
    String beginOffset = U.spacePadRight(5, String.valueOf( pGoldQualifier.getBegin()) );
    String  endOffset  = U.spacePadRight(5, String.valueOf( pGoldQualifier.getEnd()) );
    String   utterance = U.spacePadRight(40,getUtteranceForMention( pJCas, pGoldQualifier ) );
    String     mention = U.spacePadRight(20, U.normalize(pGoldQualifier.getCoveredText()));
    String  goldCategory = U.spacePadLeft(4, pGoldCategory);
    String  copperCategory = U.spacePadLeft(4, pCopperCategory);
    String copperBFKind =  BodyFunctionQualifierAnnotator.getBFKind( pJCas, pCopperQualifier);
    
    if ( copperBFKind == null ) {
      System.err.println("how ?");
    }
    
    String buff = pTP_or_FN + "|" + docId + "|" + beginOffset + "|" + endOffset + "|" + utterance + "|" + mention + "|" + goldCategory + "|" + copperCategory + "|" + copperBFKind ; 
    
    if ( pTP_or_FN.equals("tp"))
      this.tpRows.add( buff );
    else
      this.fnRows.add( buff );
    
    
  } // end Method addRow() ---------------------------


  // =================================================
  /**
   * getUtteranceForMention returns the surrounding sentence/slotValue/Line
   * 
   * @param pJCas
   * @param pAnnotation
   * @return String
  */
  // =================================================
  private String getUtteranceForMention(JCas pJCas, Annotation pAnnotation) {
   
    String returnVal = null;
    List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Sentence.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
    
    if ( sentences != null && !sentences.isEmpty())
      returnVal = sentences.get(0).getCoveredText();
    else {
      List<Annotation> slotValues = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SlotValue.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
      
      if ( slotValues != null && !slotValues.isEmpty())
        returnVal = slotValues.get(0).getCoveredText();
      else {
        List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Line.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
        if ( lines != null && !lines.isEmpty())
          returnVal = lines.get(0).getCoveredText();
      }
     }
    if ( returnVal != null && returnVal.trim().length() > 0 && returnVal.length() > 40 )
      returnVal = returnVal.substring( 0, 40);
    
    returnVal = U.extremeNormalize( returnVal);
    
    return returnVal;
    
    
  } // end Method getUtteranceForMention() -----------


  // =================================================
  /**
   * getCopperQualifier returns the closest overlapping machine created qualifier 
   * 
   * @param pJCas
   * @param pGoldQualifier
   * @return Annotation
  */
  // =================================================
  private final Annotation getCopperQualifier(JCas pJCas, Annotation pGoldQualifier) {
   
    Annotation returnVal = null;
    List<Annotation> qualifiers = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  BFQualifier.typeIndexID, pGoldQualifier.getBegin(), pGoldQualifier.getEnd() );
    
    if ( qualifiers != null && !qualifiers.isEmpty())
      returnVal = qualifiers.get(0);
    
    return returnVal;
  } // end Method getCopperQualifier() ---------------



//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  
  writeEvalReport();
  
  
  
  
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
} // end Method destroy() 

  
  // =================================================
/**
 * writeEvalReport
 * 
*/
// =================================================
  private void writeEvalReport() {
  
    PrintWriter out = null;
    try {
      
      out = new PrintWriter( this.outputFile );
      
      String overallStats = getOverallStats();
      out.print( overallStats);
      out.print("\n\n\n");
     
      out.print("False Negatives\n");
      out.print( RowsHeader );
      for ( int i = 0; i <  this.fnRows.size(); i++ ) {
       out.print(U.zeroPad(i,  4)); 
       out.print("|");
       out.print( this.fnRows.get(i));
       out.print("\n");
      }
      
      out.print("\n\n\n");
      out.print("True Positives\n");
      out.print(RowsHeader);
      for ( int i = 0; i <  this.tpRows.size(); i++ ) {
        out.print(U.zeroPad(i,  4)); 
        out.print("|");
        out.print( this.tpRows.get(i));
        out.print("\n");
       }
       
      
      
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL, this.getClass(), "writeReport", " issue with writing the Eval report" + e.toString());
    }
  
  } // end Method writeEvlalReport() -----------------

  // =================================================
  /**
   * getOverallStats 
   * 
   * @return String
  */
  // =================================================
  private final String getOverallStats() {
    
    String returnVal = null;
    StringBuffer buff = new StringBuffer();
    
    buff.append("   the number of Qualifiers                                          = " + U.spacePadLeft(10, this.totalGold + "\n"));
    buff.append("   The number of Machine Qualifiers that overlap the Gold Qualifiers = " + U.spacePadLeft(10, this.totalCopper + "\n" ));
    buff.append("   The number of false Negitive Plus 1 categories                    = " + U.spacePadLeft(10, this.fnPlus1 + "\n" ));
    buff.append("   The number of false Negitive Minus 1 categories                   = " + U.spacePadLeft(10, this.fnMinus1 + "\n" ));
    buff.append("   The number of false Negitive 0 categories                         = " + U.spacePadLeft(10, this.fnZero + "\n" ));
    
    buff.append("   The number of gold +1 polarity                                    = " + U.spacePadLeft(10, this.totalGoldPlus1 + "\n" ));
    buff.append("   The number of gold -1 polarity                                    = " + U.spacePadLeft(10, this.totalGoldMinus1 + "\n" ));
    buff.append("   The number of gold 0 polarity                                     = " + U.spacePadLeft(10, this.totalGoldZero + "\n" ));
    
     
    buff.append("   The number of True Positive Plus 1 categories                    = " + U.spacePadLeft(10, this.tpPlus1 + "\n" ));
    buff.append("   The number of True Positive Minus 1 categories                   = " + U.spacePadLeft(10, this.tpMinus1 + "\n" ));
    buff.append("   The number of True Positive 0 categories                         = " + U.spacePadLeft(10, this.tpZero + "\n" ));
    
    double recallPlus1  = (this.tpPlus1  + 0.00001) / (this.totalGoldPlus1 + 0.00001); 
    double recallMinus1 = (this.tpMinus1 + 0.00001) / (this.totalGoldMinus1 + 0.00001); 
    double recallZero   = (this.tpZero   + 0.00001) / (this.totalGoldZero + 0.00001); 
    
    buff.append("   The number of True Positive Plus 1 categories over total gold plus 1          = " + U.spacePadLeft(10, U.formatDouble(recallPlus1) + "\n" ));
    buff.append("   The number of True Positive Minus 1 categories over the total gold minus 1    = " + U.spacePadLeft(10, U.formatDouble(recallMinus1) + "\n" ));
    buff.append("   The number of True Positive 0 categories over the total gold 0                = " + U.spacePadLeft(10, U.formatDouble(recallZero )+ "\n" ));
    
    returnVal = buff.toString();
    
    
   
    return returnVal;
  } // end Method getOverallStats() -----------------

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
   *                
   *                The parameters looked for are
   *                --evaluateBodyFunction=false
   *                --evaluateQualifier=false
   *                --evaluateBodyFunctionType=false
   *                --evaluateBodyLocation=false
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
   
    
    try {
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    this.outputDir = U.getOption(pArgs,   "--outputDir=", "some/output/dir" ); 
    String outputEvalDir = this.outputDir + "/eval";
    
   
      U.mkDir(outputEvalDir);
   
      this.outputFile = outputEvalDir + "/" + "QualifierPolarityEval.txt";
    
    
   
    
    this.tpRows = new ArrayList<String>();
    this.fnRows = new ArrayList<String>();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue with trying to initialize this annotator " + e.toString() );
      throw new ResourceInitializationException();
    }
      
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private String outputDir = null;
  private String outputFile = null;

 
   private int fnPlus1 = 0;
   private int fnMinus1 = 0;
   private int fnZero = 0;
   private int tpPlus1 = 0;
   private int tpMinus1 = 0;
   private int tpZero = 0;
   
   private int totalGoldPlus1 = 0;
   private int totalGoldMinus1 = 0;
   private int totalGoldZero = 0;
   
   private int totalGold = 0;
   private int totalCopper = 0;
   private List<String> tpRows = null;
   private List<String> fnRows = null;
  
   String RowsHeader = 
   
   "----+--+---------------+-----+-----+----------------------------------------+--------------------+----+------\n" +
   "   N|Ty|DocId          |begin|end  |Utterance                               | Qualifier          |Gold|Copper\n" + 
   "----+--+---------------+-----+-----+----------------------------------------+--------------------+----+------\n" ;

  
} // end Class LineAnnotator() ---------------

