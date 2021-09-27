// =================================================
/**
 * RemovePossibleBodyFunctionAnnotationsAnnotator removes
 * all annotations around the zones that are marked as
 * PossibleBodyFunction.
 * 
 * These are zones that the annotators have marked where there 
 * is junk, but some of the junk (poorly ocr'd text), might
 * be pernisious. Any fp's that might have resulted in
 * being annotated should be be removed.
 *
 * 
 * @author  G.o.d.
 * @created 2020.05.01
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

import gov.nih.cc.rmd.gate.Possible_Body_Function;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;





public class RemovePossibleBodyFunctionAnnotationsAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * BodyReflexs from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();

    List<Annotation> junks = UIMAUtil.getAnnotations(pJCas, Possible_Body_Function.typeIndexID );
    
    if ( junks != null && !junks.isEmpty())
      for ( Annotation junk: junks ) {
        List<Annotation> allBadAnnotations = UIMAUtil.getAnnotationsBySpan(pJCas, junk.getBegin(), junk.getEnd());
        if ( allBadAnnotations != null && !allBadAnnotations.isEmpty())
          for ( Annotation badAnnotation : allBadAnnotations )
            if ( !badAnnotation.getClass().getCanonicalName().contains("Possible_Body_Function"))
              badAnnotation.removeFromIndexes();
       
      }
    
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   




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

