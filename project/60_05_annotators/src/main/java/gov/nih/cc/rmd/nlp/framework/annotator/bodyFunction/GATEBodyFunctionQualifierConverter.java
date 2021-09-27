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
 * GATEBodyFunctionQualifierConverter finds gate body function qualifiers
 * with attributes and converts them to separate labels
 * for BFQCategory_0, BFQCategory_Minus_1, BFQCategory_Plus_1 
 *   
 * 
 *
 *
 * @author  Guy Divita 
 * @created May 4, 2021
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

import gov.nih.cc.rmd.framework.BFQualifierCategory_0;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Minus_1;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Plus_1;
import gov.nih.cc.rmd.gate.Qualifier;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class GATEBodyFunctionQualifierConverter extends JCasAnnotator_ImplBase {
 
  

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

    
   
     process_BodyFunctionQualifiers( pJCas);

    
      
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

 

  

  // =================================================
  /**
   * process_BodyFunctionQualifiers transforms the attributes into separate annotations
   * 
   * @param pJCas
  */
  // =================================================
  private final void process_BodyFunctionQualifiers(JCas pJCas) {
    
  List<Annotation> manualBodyFunctionQualifiers  = UIMAUtil.getAnnotations(pJCas, Qualifier.typeIndexID, true);
    
   
    if ( manualBodyFunctionQualifiers != null && !manualBodyFunctionQualifiers.isEmpty() ) 
      for ( Annotation BodyFunctionQualifier : manualBodyFunctionQualifiers ) {
        
        String qualifierCategory = ((Qualifier) BodyFunctionQualifier ).getCategory();
        if ( qualifierCategory != null && qualifierCategory.trim().length() > 0 ) 
          // BFQCategory_0, BFQCategory_Minus_1, BFQCategory_Plus_1 
          if      ( qualifierCategory.contains("0"))  ceateBFQCategory_0( pJCas, BodyFunctionQualifier );
          else if ( qualifierCategory.contains("1"))  ceateBFQCategory_Plus_1( pJCas, BodyFunctionQualifier);
          else if ( qualifierCategory.contains("-1")) ceateBFQCategory_Minus_1( pJCas, BodyFunctionQualifier);
          else System.err.println(" odd kind: " + qualifierCategory);  
        
        BodyFunctionQualifier.removeFromIndexes();
        
      }
        
      
        
    
  } // end Method process_BodyFunctionQualifiers() --------


  // =================================================
  /**
   * ceateBFQCategory_Plus_1 
   * 
   * @param pJCas
   * @param BodyFunctionQualifierCategory
  */
  // =================================================
  private final void ceateBFQCategory_Plus_1(JCas pJCas, Annotation BodyFunctionQualifierCategory) {
    
    BFQualifierCategory_Plus_1 statement = new BFQualifierCategory_Plus_1( pJCas);
    statement.setBegin( BodyFunctionQualifierCategory.getBegin());
    statement.setEnd(   BodyFunctionQualifierCategory.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createStrengthAnnotation() ---------




  // =================================================
  /**
   * ceateBFQCategory_Minus_1 
   * 
   * @param pJCas
   * @param BodyFunctionQualifierCategory
  */
  // =================================================
  private final void ceateBFQCategory_Minus_1(JCas pJCas, Annotation BodyFunctionQualifierCategory) {
    
    BFQualifierCategory_Minus_1 statement = new BFQualifierCategory_Minus_1( pJCas);
    statement.setBegin( BodyFunctionQualifierCategory.getBegin());
    statement.setEnd(   BodyFunctionQualifierCategory.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createStrengthAnnotation() ---------
  

  // =================================================
  /**
   * ceateBFQCategory_0 
   * 
   * @param pJCas
   * @param BodyFunctionQualifierCategory
  */
  // =================================================
  private final void ceateBFQCategory_0(JCas pJCas, Annotation BodyFunctionQualifierCategory) {
    
    BFQualifierCategory_0 statement = new BFQualifierCategory_0( pJCas);
    statement.setBegin( BodyFunctionQualifierCategory.getBegin());
    statement.setEnd(   BodyFunctionQualifierCategory.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createStrengthAnnotation() ---------


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
   *                --evaluateBodyFunctionQualifiers=false
   *                --evaluateBodyLocation=false
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

