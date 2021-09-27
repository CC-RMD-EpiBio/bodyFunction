// =================================================
/**
 * BodyQualifierPolarityAnnotator assigns a polarity to the qualifer (-1,0,1)
 * and the whole mention
 * 
 *  
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
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.BFQualifier;
import gov.nih.cc.rmd.framework.BFQualifierCategory_0;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Minus_1;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Plus_1;
import gov.nih.cc.rmd.gate.GATE_BFQualifierCategory_0;
import gov.nih.cc.rmd.gate.GATE_BFQualifierCategory_Minus_1;
import gov.nih.cc.rmd.gate.GATE_BFQualifierCategory_Plus_1;
import gov.nih.cc.rmd.gate.Qualifier;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;





public class BodyFunctionQualifierPolarityAnnotator extends JCasAnnotator_ImplBase {
 
  
  
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
  
   
    List<Annotation> mentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID );
    
    if ( mentions != null && !mentions.isEmpty() )
      for ( Annotation mention: mentions ) {
        assignPolarity( pJCas, mention );
        
        if ( this.evaluateQualifier ) {
          createBFPolarityCategories( pJCas, mention );
        }
      }
      
     if ( this.evaluateQualifier ) {
       List<Annotation> goldBFMentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID );
       
       if ( goldBFMentions != null && !goldBFMentions.isEmpty()) {
         for ( Annotation goldBFMention : goldBFMentions )
           createGATEBFPolarityCategories( pJCas, goldBFMention );
       }
     
     }
    
     
     this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  
  } // end Method process() ----------------
   



  // =================================================
  /**
   * createGATEBFPolarityCategories 
   * 
   * @param pJCas
   * @param pGoldMention
  */
  // =================================================
   private void createGATEBFPolarityCategories(JCas pJCas, Annotation pGoldMention) {
    
    String category = ((Qualifier)pGoldMention).getCategory();
    
    if  ( category != null ) {
      switch ( category ) {
        case "+1" :  createQualifierCategoryAnnotation( pJCas, new GATE_BFQualifierCategory_Plus_1 (pJCas), pGoldMention );   break;
        case "-1" :  createQualifierCategoryAnnotation( pJCas, new GATE_BFQualifierCategory_Minus_1(pJCas), pGoldMention );   break;  
        case "0"  :  createQualifierCategoryAnnotation( pJCas, new GATE_BFQualifierCategory_0      (pJCas), pGoldMention );   break;  
        default:
       }
    }
     
    
   } // end Method createGATEBFPolarityCategories() --------

   // =================================================
   /**
    * createBFPolarityCategories 
    * 
    * @param pJCas
    * @param pGoldMention
   */
   // =================================================
    private void createBFPolarityCategories(JCas pJCas, Annotation pGoldMention) {
     
     String category = ((Qualifier)pGoldMention).getCategory();
     
    
     
     if  ( category != null ) {
       switch ( category ) {
         case "+1" :  createQualifierCategoryAnnotation( pJCas, new BFQualifierCategory_Plus_1(pJCas), pGoldMention );   break;
         case "-1" :  createQualifierCategoryAnnotation( pJCas, new BFQualifierCategory_Minus_1(pJCas), pGoldMention );   break;  
         case "0"  :  createQualifierCategoryAnnotation( pJCas, new BFQualifierCategory_0(pJCas),       pGoldMention );   break;  
         default:
        }
     }
      
     
    } // end Method createBFPolarityCategories() --------





// =================================================
  /**
   * createQualifierCategoryAnnotation 
   * 
   * @param pJCas
   * @param pStatement
   * @param pMention
  */
  // =================================================
  private final void createQualifierCategoryAnnotation(JCas pJCas, Object pStatement, Annotation pGoldMention) {
   
    
    
    
  } // end Method createQualifierCategoryAnnotation() ----




  // =================================================
  /**
   * assignPolarity
   * 
   * @param pJCas
   * @param pMention
  */
  // =================================================
  private void assignPolarity(JCas pJCas, Annotation pMention) {
   
    // --------------
    // Find the Qualifiers for this mention
    // --------------
    List<Annotation> qualifiers = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BFQualifier.typeIndexID,  pMention.getBegin(), pMention.getEnd());
    
    ArrayList<String> polarities = new ArrayList<String>();
    StringBuffer buff = new StringBuffer();
    if ( qualifiers != null && !qualifiers.isEmpty() )
      for ( Annotation qualifier: qualifiers) {
        buff.append( qualifier.getCoveredText());
        buff.append(":");
        polarities.add(   ((BFQualifier)qualifier).getPolarity());
        ;
      }
    
    
    
    
  } // end Method assignPolarity () ---------------






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
    this.evaluateQualifier        = Boolean.valueOf(U.getOption(pArgs,   "--evaluateQualifier=", "false" ));
    String evaluatePolarityPlusOne  = U.getOption(pArgs,   "--evaluatePolarityPlusOne", "false");
    String evaluatePolarityMinusOne = U.getOption(pArgs,   "--evaluatePolarityMinusOne", "false");
    String evaluatePolarityZero     = U.getOption(pArgs,   "--evaluatePolarityZero", "false");
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  boolean evaluateQualifier = false;
  
  
  
  
} // end Class LineAnnotator() ---------------

