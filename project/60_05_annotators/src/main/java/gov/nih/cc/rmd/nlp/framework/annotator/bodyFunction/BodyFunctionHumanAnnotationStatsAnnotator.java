/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * This class gives frequency stats for each file, and aggregate stats.  It should be run
 * separately for training sets and testing sets - that will answer part of the question
 * whether or not the populations are the same.
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.gate.Body_Function_TType;
import gov.nih.cc.rmd.gate.Qualifier;
import gov.nih.cc.rmd.model.SectionName;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.vinci.model.Gold;



public class BodyFunctionHumanAnnotationStatsAnnotator extends JCasAnnotator_ImplBase {
 
  

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

    // stopped here ----
      String pDocumentName = VUIMAUtil.getDocumentId(pJCas);
      String pDocumentType = getDocumentType( pDocumentName);
      
      int[] docTypez = this.docTypeHash.get(pDocumentType);
      if ( docTypez == null ) {
        docTypez = new int[1];
        this.docTypeHash.put( pDocumentType,  docTypez);
      }
      docTypez[0]++;
      
      
      totalFiles++;
      
     int bodyLocations = process_BodyLocation( pJCas, pDocumentType, pDocumentName);
     int[] functionTypes = process_BodyFunctionType( pJCas, pDocumentType, pDocumentName);
     int qualifers =  process_Qualifier( pJCas,pDocumentType, pDocumentName);
     int mentions =  process_BodyFunction( pJCas, pDocumentType, pDocumentName);
     int contexts = process_Contexts( pJCas, pDocumentType, pDocumentName );
     int possibleBFs = process_possibleBFs( pJCas, pDocumentType, pDocumentName);
      
      processDocStats( pJCas, pDocumentType, pDocumentName,  bodyLocations, functionTypes[STRENGTH], functionTypes[ROM], functionTypes[REFLEX], qualifers, mentions, contexts, possibleBFs);
      
      
      
       totalAnnotations+= bodyLocations + functionTypes[STRENGTH] + functionTypes[ROM] + functionTypes[REFLEX] + qualifers + mentions;
       totalLocations+= bodyLocations;
      totalStrengths+= functionTypes[STRENGTH];
       totalROMs+= functionTypes[ROM];
       totalReflexes+= functionTypes[REFLEX];
       totalAmbiguous+= functionTypes[AMBIGUOUS];
       totalQualifiers+= qualifers;
       totalContexts+=contexts;
       totalPossibleBF+=possibleBFs;
       
       
      
       totalBodyFunctions+= mentions;
       
      
       meanAnnotations  = (double) totalAnnotations / (double) totalFiles;
       meanLocations = (double)  totalLocations / (double)  totalFiles;
       meanStrengths = (double) totalStrengths / (double)totalFiles;
       meanROMs = (double) totalROMs / (double)totalFiles;
       meanReflexes = (double)  totalReflexes/ (double)totalFiles;
       meanQualifiers = (double) totalQualifiers /(double)totalFiles;
       meanBodyFunctions = (double) totalBodyFunctions / (double)totalFiles;
       meanContexts = (double) totalContexts / (double)  totalFiles ;
       meanPossibleBf = ((double) totalPossibleBF / (double) totalFiles);
       
       
      
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   


 
 // =================================================
  /**
   * processDocStats 
   * 
   * @param pJCas
   * @param pLocations
   * @param pROM
   * @param pStrength
   * @param pReflex
   * @param pQualifiers
   * @param pMentions
   * @param pContexts 
   * @param possibleBFs 
   * 
  */
  // =================================================
 private final void processDocStats(JCas pJCas, String pDocType, String pDocName, int pLocations, int pStrength, int pROM, int pReflex, int pQualifiers, int pMentions, int pContexts, int possibleBFs ) {
    
    String docText = pJCas.getDocumentText();
    int docLength = docText.length();
    int numberOfLines = U.getNewlineOffsets(docText).length;
    int numberOfTokens = countTokens( docText );
    
    
     totalChars+= docLength;
     totalLines+= numberOfLines;
     totalTokens+= numberOfTokens ;
     meanChars =  (double) totalChars / (double) this.totalFiles;
     meanLines =  (double) totalLines / (double) this.totalFiles;
     meanTokens = (double) totalTokens /(double) this.totalFiles;
    
    // "DocType|DocName|NumChars|NumLines|NumTokens|BodyLocation|Strength|Rom|Reflex|Qualifier|BodyFunction";
    String buff = pDocType + "|" + pDocName + "|" + docLength + "|" + numberOfLines + "|" + numberOfTokens + "|" + pLocations + "|" + pStrength + "|" + pROM + "|" + pReflex + "|" + pQualifiers  + "|" + pMentions + "|" + pContexts + "|" + possibleBFs ;   
 
    
    this.stats.add( buff);
 
 } // end Method processDocStats() -----------------
 
 
 



// =================================================
  /**
   * getDocumentType retrieves the document type from
   * the document name, because it's in the path
   * 
   * The first part of the file name is the doc type 
   * - all the way up to the _
   * @param documentName
   * @return String
  */
  // =================================================
  private String getDocumentType(String pDocumentName) {
   
    String returnVal = null;
    try {
    String docName = U.getFileNameOnly(pDocumentName);
    
    int underbar = docName.indexOf('_');
    returnVal = docName.substring(0, underbar);
    } catch ( Exception e) {
      e.printStackTrace();
      GLog.error_println("Issue getting the document type from the document name " + e.toString());
    }
    return ( returnVal ); 
    
    
    
  } // end Method getDocumentType() -----------------



// =================================================
  /**
   * process_Qualifier 
   * 
   * @param pJCas
   * @param pDocumentName 
   * @param pDocumentType 
   * @return int    the number of annotations found
   */
  // ================================================= 
    private final int process_Qualifier(JCas pJCas, String pDocumentType, String pDocumentName) {
   
    
 List<Annotation> manualQualifiers  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID, true);
    
    int returnVal = 0;
    if ( manualQualifiers != null && !manualQualifiers.isEmpty() ) {
      returnVal = manualQualifiers.size();
      for ( Annotation manualQualifier : manualQualifiers )  {
        
        String bfTypes = getBFType( pJCas, manualQualifier );
        String category = ((Qualifier) manualQualifier).getCategory();
        catalogGATEAnnotations( pJCas, pDocumentType, pDocumentName, manualQualifier, category );
        this.qualiferTable.add( bfTypes + "|" + category + "|" + U.normalize( manualQualifier.getCoveredText()) );
        
      }
    }
    
    return returnVal;
  } // end Method process_Qualifier() ----------------



  // =================================================
/**
 * getBFType returns the bf type for this qualifer
 * 
 * @param pJCas
 * @param manualQualifier
 * @return String
*/
// =================================================
    private final  String getBFType(JCas pJCas, Annotation manualQualifier) {
     
      String returnVal = null;
      
       List<Annotation> bfMentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,gov.nih.cc.rmd.gate.Body_Function.typeIndexID, manualQualifier.getBegin(), manualQualifier.getEnd(), false );
      if ( bfMentions != null && !bfMentions.isEmpty()) {
        Annotation bfMention = bfMentions.get(0);
        
        // ((gov.nih.cc.rmd.gate.Body_Function) bfMention).get
      }
        
       
       
      return returnVal;
    } // end Method getBFType() ----------------------




  // =================================================
  /**
   * process_bodyLocation 
   * 
   * @param pJCas
   * @param pDocumentName 
   * @param pDocumentType 
   * @return int   (the number of annotations found)
  */
  // =================================================
  private final int process_BodyLocation(JCas pJCas, String pDocumentType, String pDocumentName) {

    int returnVal = 0;
    List<Annotation> manualBodyLocations  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_location.typeIndexID, true);
    
    if ( manualBodyLocations != null && !manualBodyLocations.isEmpty() ) {
      returnVal = manualBodyLocations.size();
      for ( Annotation bodyLocation : manualBodyLocations ) 
        catalogGATEAnnotations( pJCas,  pDocumentType, pDocumentName,  bodyLocation, " ");
    }
    
    return returnVal;
  } // end Method process_BodyLocation() -----------



  // =================================================
  /**
   * process_bodyfunction type - [tbd] need to capture the types as attributes
   * 
   * @param pJCas
   * @param pDocumentName 
   * @param pDocumentType 
   * @return int[] (the numbers of annotations that have the type attributes in it)
  */
  // =================================================
  private final int[]  process_BodyFunctionType(JCas pJCas, String pDocumentType, String pDocumentName) {
    
    
    int[] returnVal = new int[4]; returnVal[0] = returnVal[1] = returnVal[2] = returnVal[3] = 0;
    
  List<Annotation> manualBodyFunctionType  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_Function_TType.typeIndexID, true);
    
    if ( manualBodyFunctionType != null && !manualBodyFunctionType.isEmpty() ) 
      for ( Annotation bodyfunctionType : manualBodyFunctionType ) {
        
        String aType = ((Body_Function_TType) bodyfunctionType).getFunction();
        catalogGATEAnnotations( pJCas,  pDocumentType, pDocumentName,  bodyfunctionType, aType);
       
          if ( aType != null ) {
            if      ( aType.contains("Strength"))  returnVal[STRENGTH]++;
            else if ( aType.contains("ROM"))  returnVal[ROM]++; 
            else if ( aType.contains("Reflex"))  returnVal[REFLEX]++; 
            else if ( aType.contains("Ambiguous")) returnVal[AMBIGUOUS]++;
          } else
            System.err.println("Missing body function type for annotation " + pDocumentName + " " + bodyfunctionType.getCoveredText() + "|" + bodyfunctionType.getBegin() + "|" + bodyfunctionType.getEnd() );
        
        
      }
    
    return returnVal;
    
  } // end Method process_BodyFunctionType() --------


//=================================================
 /**
  * process_bodyfunction  
  * 
  * @param pJCas
 * @param pDocumentName 
 * @param pDocumentType 
 * @return int  number of body function mentions
 */
 // =================================================
 private final int process_BodyFunction(JCas pJCas, String pDocumentType, String pDocumentName) {
   
 List<Annotation> manualBodyFunctions  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_Function.typeIndexID, true);
   
   int returnVal = 0;
   if ( manualBodyFunctions != null && !manualBodyFunctions.isEmpty() ) {
     returnVal = manualBodyFunctions.size();
     for ( Annotation bodyfunction : manualBodyFunctions ) 
       catalogGATEAnnotations( pJCas,  pDocumentType, pDocumentName,  bodyfunction, " ");
   
   }
   
   return returnVal;
 } // end Method process_BodyFunction() --------

  

//=================================================
/**
 * process_Contexts
 * 
 * @param pJCas
* @param pDocumentName 
* @param pDocumentType 
* @return int  number of body function mentions
*/
// =================================================
private final int process_Contexts(JCas pJCas, String pDocumentType, String pDocumentName) {
  
List<Annotation> manualContexts  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_Function_Context.typeIndexID, true);
  
  int returnVal = 0;
  if ( manualContexts != null && !manualContexts.isEmpty() ) {
    returnVal = manualContexts.size();
    for ( Annotation context : manualContexts ) {
     
      String isSectionName = isSectionName( pJCas, context );
      
      
      catalogGATEAnnotations( pJCas,  pDocumentType, pDocumentName,  context, isSectionName);
  
    }
  }
  
  return returnVal;
} // end Method process_BodyFunction() --------


//=================================================
/**
* process_PossibleBFs
* 
* @param pJCas
* @param pDocumentName 
* @param pDocumentType 
* @return int  number of body function mentions
*/
//=================================================
private final int process_possibleBFs(JCas pJCas, String pDocumentType, String pDocumentName) {

List<Annotation> manualpossibleBFs  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Possible_Body_Function.typeIndexID, true);

int returnVal = 0;
if ( manualpossibleBFs != null && !manualpossibleBFs.isEmpty() ) {
  returnVal = manualpossibleBFs.size();
  for ( Annotation possibleBF : manualpossibleBFs ) {
   
    catalogGATEAnnotations( pJCas,  pDocumentType, pDocumentName,  possibleBF, null);

  }
}

return returnVal;
} // end Method process_BodyFunction() --------



// =================================================
/**
 * isSectionName 
 * 
 * @param pJCas
 * @param context
 * @return "true|false"
*/
// =================================================
  private final String isSectionName(JCas pJCas, Annotation pContext) {
    String returnVal = "false";
 
    List<Annotation> sectionNames = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SectionName.typeIndexID, pContext.getBegin(), pContext.getEnd() );

    if ( sectionNames != null && !sectionNames.isEmpty())
      returnVal = "true";
  
  return returnVal;
} // end Method isSectionName() -------------------




//=================================================
 /**
  * catalogGATEAnnotations 
  * @param pJCas
  * @param pDocType
  * @param pDocName
  * @param pAnnotation
 *  @param anAttribute 
 */
 // =================================================
 private void catalogGATEAnnotations(JCas pJCas, String pDocType, String pDocName, Annotation pAnnotation, String anAttribute) {
   
  
   String docText = pJCas.getDocumentText();
   // ---------------------------------------------------------
   // docType|docName|annotationType|Annotation Text|begin|end 
   String annotationType = U.getClassName( pAnnotation.getClass().getName() );
   String annotationText = U.extremeNormalize2(pAnnotation.getCoveredText());
  
   
  //   "DocType|DocName|annotationType|Attribute|annotationText|begin|end"; 
  String buff = pDocType + "|" + pDocName + "|" + annotationType + "|" + anAttribute + "|" + annotationText + "|" + pAnnotation.getBegin() + "|" +  pAnnotation.getEnd() ;

   this.rows.add( buff);
  
   
 } // end Method createActionAnnotation() ----------



// =================================================
/**
 * countTokens does a dirty count of tokens in the document
 * using java's split on \\s regex.
 * 
 * @param docText
 * @return int 
*/
// =================================================
private final int countTokens(String docText) {
 
  String[] tokens = docText.split("\\s");
  
  return tokens.length;
  
} // end Method countTokens() --------------



//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  
  DecimalFormat df = new DecimalFormat("#.##");
  df.setRoundingMode(RoundingMode.HALF_UP);
  try {
    
    PrintWriter out = new PrintWriter( this.outputFileName + "_all.csv");
    out.print( "DocType|DocName|annotationType|Attribute|annotationText|begin|end\n");
    for ( String row : this.rows)
      out.print(row + "\n");
    
    out.close();
    
    
    out = new PrintWriter( this.outputFileName + "_Stats.csv");
    out.print( "DocType|DocName|NumChars|NumLines|NumTokens|BodyLocation|Strength|Rom|Reflex|Qualifier|BodyFunction|Context\n" );
    for ( String row : this.stats)
      out.print(row + "\n");
    
    out.close();
 

    out = new PrintWriter( this.outputFileName + "_Stats.txt");
    
    out.print("The total number of files are " + this.totalFiles + "\n");
    out.print("The total number of annotations are " +  this.totalAnnotations  + "\n" );
    out.print("The total number of body location annotations are " + this.totalLocations + "\n" );
    out.print("The total number of Strength annotations are " + this.totalStrengths + "\n" );
    out.print("The total number of ROM annotations are " + this.totalROMs + "\n" );
    out.print("The total number of Reflex annotations are " + this.totalReflexes + "\n" );
    out.print("The total number of Ambiguous annotations are " + this.totalAmbiguous + "\n" );
    out.print("The total number of Qualifier annotations are " + this.totalQualifiers + "\n" );
    out.print("The total number of body function annotations are " + this.totalBodyFunctions + "\n" );
    out.print("The total number of contexts are " + this.totalContexts + "\n");
    out.print("The total number of possible BFS are " + this.totalPossibleBF + "\n");
    
    Set<String> keys = this.docTypeHash.keySet();
    for ( String key : keys ) {
     int val[] = this.docTypeHash.get(key) ;
     out.print("The number of " + key + " Doc types is " + val[0] + "\n");
    }
    
    for ( String key : keys ) {
      int val[] = this.docTypeHash.get(key) ;
      double percentage =  ((double )((double )val[0]/this.totalFiles) * 100) ;
      out.print("The percentage of " + key + " Doc types in this set are " + percentage + "\n");
     }
    
    out.print("The mean number of annotations are " +  df.format(this.meanAnnotations)  + "\n" );
    out.print("The mean number of body location annotations are " + df.format(this.meanLocations) + "\n" );
    out.print("The mean number of Strength annotations are " + df.format(this.meanStrengths) + "\n" );
    out.print("The mean number of ROM annotations are " + df.format(this.meanROMs) + "\n" );
    out.print("The mean number of Reflex annotations are " + df.format(this.meanReflexes) + "\n" );
    out.print("The mean number of Qualifier annotations are " + df.format(this.meanQualifiers) + "\n" );
    out.print("The mean number of body function annotations are " + df.format(this.meanBodyFunctions) + "\n" );
    out.print("The mean number of body function contexts are " + df.format(this.meanContexts) + "\n" );
  // out.print("The mean number of possibleBF's are " + df.format(this.meanPossibleBf + "\n"));
    
    
    out.print("The mean number of chars per document are " + df.format(this.meanChars) + "\n" );
    out.print("The mean number of lines per document are " + this.meanLines + "\n" );
    out.print("The mean number of tokens per document are " + df.format( this.meanTokens) + "\n" );
   
    
   out.close();
  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.error_println("Issue with writing the report file " + this.outputFileName + " " + e.toString());
  }
  
  
  
  
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
} // end Method destroy() ------------

  
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
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
    String outputDir = U.getOption(pArgs, "--outputDir=" , "./out" ) + "/stats";
    
    try {
      U.mkDir( outputDir);
    } catch (Exception e ) {
      e.printStackTrace();
      GLog.error_println(this.getClass(), "initialize", "Issue with trying to create the outputDir " + outputDir + " " + e.toString());
      throw new ResourceInitializationException();
    }
    this.outputFileName = outputDir + "/" + "GATE_Corpus_";
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  List<String> rows = new ArrayList<String>( 10000);
  List<String> stats = new ArrayList<String>( 600 );
  private String outputFileName = null;
  
  
  int totalFiles = 0;
  int totalAnnotations  = 0;
  int totalLocations = 0;
  int totalStrengths = 0;
  int totalROMs = 0;
  int totalReflexes = 0;
  int totalAmbiguous = 0;
  int totalQualifiers = 0;
  int totalBodyFunctions = 0;
  int totalContexts = 0;
  int totalPossibleBF = 0;
  
  
  double meanAnnotations  = 0.0;
  double meanLocations = 0.0;
  double meanStrengths = 0.0;
  double meanROMs = 0.0;
  double meanReflexes = 0.0;
  double meanQualifiers = 0.0;
  double meanBodyFunctions = 0;
  double meanContexts = 0;
  double meanPossibleBf = 0.0;
  
  int totalChars = 0;
  int totalLines = 0;
  int totalTokens = 0;
  double meanChars = 0.0;
  double meanLines = 0.0;
  double meanTokens = 0.0;
  
  
  
  HashMap<String, int[]> docTypeHash = new HashMap<String , int[]>(500);
  
 
  private static final int STRENGTH = 0;
  private static final int ROM = 1;
  private static final int REFLEX = 2;
  private static final int AMBIGUOUS = 3;
  private List<String>  qualiferTable = new ArrayList<String>();
  
} // end Class LineAnnotator() ---------------

