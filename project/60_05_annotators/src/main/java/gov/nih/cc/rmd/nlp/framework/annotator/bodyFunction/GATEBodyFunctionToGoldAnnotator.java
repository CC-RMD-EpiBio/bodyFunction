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
 * BodyFunction Annotator finds gate imported BodyFunction
 * annotations, and turns them into one of two
 * kinds
 *    BodyLocation  -->gold
 *    BodyStrength  -->gold
 *    RangeOfMOtion -->gold
 *    Reflex        -->gold
 * 
 *
 *
 * @author  Guy Divita 
 * @created May 20, 2020
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.vinci.model.Gold;
import gov.nih.cc.rmd.framework.Manual_Section_Name;
import gov.nih.cc.rmd.gate.Qualifier;



public class GATEBodyFunctionToGoldAnnotator extends JCasAnnotator_ImplBase {
 
  

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

    // -------------------------
    // Strip Manual Section_Name annotations - they are causing problems
    strip_manual_sectionNames( pJCas );
    
    if ( this.evaluateBodyLocation )
      process_BodyLocation( pJCas);
    
    if ( this.evaluateBodyFunctionType )
      process_BodyFunctionType( pJCas);

    /*
   
    boolean processQualifier = true;
    if ( this.evaluatePolarityPlusOne ) {
      process_QualifierPolarity( pJCas, "+1");
      processQualifier = false;
    }
    if ( this.evaluatePolarityMinusOne ) {
      process_QualifierPolarity( pJCas, "-1");
       processQualifier = false;
    }
    if ( this.evaluatePolarityZero  ) {
      process_QualifierPolarity( pJCas, "0");
      processQualifier = false;
    }
    
    */
    if ( this.evaluateQualifier  )
      process_Qualifier( pJCas);
    
    
    if ( this.evaluateBodyFunction )
      process_BodyFunction( pJCas);
      
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

 
 // =================================================
  /**
   * strip_manual_sectionNames removes section names that came in
   * from pre-annotation into the gate file, then from the gate file,
   * turned into Manual_Section_Name
   * 
   * @param pJCas
  */
  // =================================================
  private void strip_manual_sectionNames(JCas pJCas) {
    
    List<Annotation> manualSectionNames = UIMAUtil.getAnnotations(pJCas, Manual_Section_Name.typeIndexID);
    
    if ( manualSectionNames != null && !manualSectionNames.isEmpty())
      for ( Annotation sectionName : manualSectionNames )
        sectionName.removeFromIndexes();
    
    
  } // end Method strip_manual_sectionNames() -----



// =================================================
  /**
   * process_Qualifier 
   * 
   * @param pJCas
  */
  // =================================================
  private final void process_Qualifier(JCas pJCas) {
    
 List<Annotation> manualQualifiers  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID, true);
    
    if ( manualQualifiers != null && !manualQualifiers.isEmpty() ) 
      for ( Annotation manualQualifier : manualQualifiers ) 
        createGoldAnnotation( pJCas, manualQualifier);
    
    
  } // end Method process_Qualifier() ----------------

  
//=================================================
 /**
  * process_QualifierPolarityPlusOne 
  * 
  * @param pJCas
  * @param pCategory [-1|0|+1]
 */
 // =================================================
 private final void process_QualifierPolarity(JCas pJCas, String pCategory) {
   
List<Annotation> manualQualifiers  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID, true);
   
   if ( manualQualifiers != null && !manualQualifiers.isEmpty() ) 
     for ( Annotation manualQualifier : manualQualifiers ) { 
       String category = ((Qualifier) manualQualifier).getCategory();
       if ( category != null && category.equals(pCategory ))
         createGoldAnnotation( pJCas, manualQualifier );
     }
   
   
 } // end Method process_QualifierPolarityPlusOne() ----------------
  
  

  // =================================================
  /**
   * process_bodyLocation 
   * 
   * @param pJCas
  */
  // =================================================
  private void process_BodyLocation(JCas pJCas) {

    List<Annotation> manualBodyLocations  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_location.typeIndexID, true);
    
    if ( manualBodyLocations != null && !manualBodyLocations.isEmpty() ) 
      for ( Annotation bodyLocation : manualBodyLocations ) 
        createGoldAnnotation( pJCas, bodyLocation);
    
    
  } // end Method process_BodyLocation() -----------



  // =================================================
  /**
   * process_bodyfunction type - [tbd] need to capture the types as attributes
   * 
   * @param pJCas
  */
  // =================================================
  private final void process_BodyFunctionType(JCas pJCas) {
    
  List<Annotation> manualBodyFunctionType  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_Function_TType.typeIndexID, true);
    
    if ( manualBodyFunctionType != null && !manualBodyFunctionType.isEmpty() ) 
      for ( Annotation bodyfunctionType : manualBodyFunctionType ) 
        createGoldAnnotation( pJCas, bodyfunctionType);
    
  } // end Method process_BodyFunctionType() --------


//=================================================
 /**
  * process_bodyfunction  
  * 
  * @param pJCas
 */
 // =================================================
 private final void process_BodyFunction(JCas pJCas) {
   
 List<Annotation> manualBodyFunctions  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_Function.typeIndexID, true);
   
   if ( manualBodyFunctions != null && !manualBodyFunctions.isEmpty() ) 
     for ( Annotation bodyfunction : manualBodyFunctions ) 
       createGoldAnnotation( pJCas, bodyfunction);
   
 } // end Method process_BodyFunction() --------

  

//=================================================
 /**
  * createGoldAnnotation creates a Gold annotaton
  *    to capture the label being evaluated
  * @param pJCas
  * @param pTerm
 */
 // =================================================
 private void createGoldAnnotation(JCas pJCas, Annotation pTerm) {
   
   
   Gold statement = new Gold( pJCas);
   statement.setBegin( pTerm.getBegin());
   statement.setEnd(   pTerm.getEnd() );
 
 
   statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);

  
   
   statement.addToIndexes();
   String sectionName = VUIMAUtil.deriveSectionName(statement);
   statement.setSectionName(sectionName);
   
 } // end Method createActionAnnotation() ----------



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
    
    this.evaluateBodyFunction     = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateBodyFunction=", "false" )); 
    this.evaluateBodyFunctionType = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateBodyFunctionType=", "false" )); 
    this.evaluateBodyLocation     = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateBodyLocation=", "false" )); 
    this.evaluateQualifier        = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateQualifier=", "false" )); 
    
    this.evaluatePolarityPlusOne  = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluatePolarityPlusOne", "false"));
    this.evaluatePolarityMinusOne = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluatePolarityMinusOne", "false"));
    this.evaluatePolarityZero     = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluatePolarityZero", "false"));
 
    
    
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;

  private boolean evaluateBodyFunction = false;
  private boolean evaluateQualifier = false;
  private boolean evaluateBodyFunctionType = false;
  private boolean evaluateBodyLocation = false;
  private boolean evaluatePolarityPlusOne  = false;
  private boolean evaluatePolarityMinusOne = false;
  private boolean evaluatePolarityZero     = false; 

 
  
  
  
} // end Class LineAnnotator() ---------------

