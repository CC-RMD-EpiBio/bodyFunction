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
 * BodyFunction Annotator finds gate body function type
 * with attributes and converts them to separate labels
 * for Strength, RangeOfMotion, Reflex
 *   
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

import gov.nih.cc.rmd.framework.BFT_Ambiguous;
import gov.nih.cc.rmd.framework.RangeOfMotion;
import gov.nih.cc.rmd.framework.Reflex;
import gov.nih.cc.rmd.framework.Strength;
import gov.nih.cc.rmd.gate.Body_Function_TType;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class GATEBodyFunctionTypeConverter extends JCasAnnotator_ImplBase {
 
  

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

    
   
     process_BodyFunctionType( pJCas);

    
      
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

 

  

  // =================================================
  /**
   * process_bodyfunction type - [tbd] need to capture the types as attributes
   * 
   * @param pJCas
  */
  // =================================================
  private final void process_BodyFunctionType(JCas pJCas) {
    
  List<Annotation> manualBodyFunctionTypes  = UIMAUtil.getAnnotations(pJCas, Body_Function_TType.typeIndexID, true);
    
   
    if ( manualBodyFunctionTypes != null && !manualBodyFunctionTypes.isEmpty() ) 
      for ( Annotation bodyFunctionType : manualBodyFunctionTypes ) {
        
        String functionKind = ((Body_Function_TType) bodyFunctionType ).getFunction();
        if ( functionKind != null && functionKind.trim().length() > 0 ) 
          if      ( functionKind.contains("Strength"))  createStrengthAnnotation( pJCas, bodyFunctionType);
          else if ( functionKind.contains("ROM"))       createRangeOfMotionAnnotation( pJCas, bodyFunctionType);
          else if ( functionKind.contains("Reflex"))    createReflexAnnotation( pJCas, bodyFunctionType);
          else if ( functionKind.contains("Ambiguous"))    createAmbiguousAnnotation( pJCas, bodyFunctionType);
          else System.err.println(" odd kind: " + functionKind);  
        
        bodyFunctionType.removeFromIndexes();
        
      }
        
      
        
    
  } // end Method process_BodyFunctionType() --------


  // =================================================
  /**
   * createStrengthAnnotation 
   * 
   * @param pJCas
   * @param bodyFunctionType
  */
  // =================================================
  private final void createStrengthAnnotation(JCas pJCas, Annotation pTerm) {
    
    Strength statement = new Strength( pJCas);
    statement.setBegin( pTerm.getBegin());
    statement.setEnd(   pTerm.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createStrengthAnnotation() ---------


  // =================================================
  /**
   * createRangeOfMotionAnnotation 
   * 
   * @param pJCas
   * @param bodyFunctionType
  */
  // =================================================
 private final void createRangeOfMotionAnnotation(JCas pJCas, Annotation pTerm) {
    
    RangeOfMotion statement = new RangeOfMotion( pJCas);
    statement.setBegin( pTerm.getBegin());
    statement.setEnd(   pTerm.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createRangeOfMotionAnnotation() ---------

  

  // =================================================
  /**
   * createReflexAnnotation 
   * 
   * @param pJCas
   * @param bodyFunctionType
  */
  // =================================================
    private final void createReflexAnnotation(JCas pJCas, Annotation pTerm) {
    
    Reflex statement = new Reflex( pJCas);
    statement.setBegin( pTerm.getBegin());
    statement.setEnd(   pTerm.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createReflexAnnotation() ---------



    // =================================================
    /**
     * createAmbiguousAnnotation 
     * 
     * @param pJCas
     * @param bodyFunctionType
    */
    // =================================================
      private final void createAmbiguousAnnotation(JCas pJCas, Annotation pTerm) {
      
      BFT_Ambiguous statement = new BFT_Ambiguous( pJCas);
      statement.setBegin( pTerm.getBegin());
      statement.setEnd(   pTerm.getEnd() );
      statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
      statement.addToIndexes();
      
    } // end Method createAmbigiousAnnotation() ---------




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
 
  
  
  
} // end Class LineAnnotator() ---------------

