package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunction2MentionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionQualifierAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionToCopperAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionTypeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyLocationAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyRangeOfMotionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyReflexAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyStrengthAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionRemoveAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionToGoldAnnotator;

// =================================================
/**
 * BodyFunctionPipeline runs the pipeline for bodyFunction
 *        
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */


  public class BodyFunctionFromGATEPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public BodyFunctionFromGATEPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public BodyFunctionFromGATEPipeline()  throws Exception {
     super( );
      
    } // End Constructor() -----------------------------
    
    
	// =======================================================
	/**
	 * createPipeline defines the pipeline
	 * 
	 * @param pArgs
	 * @return FrameworkPipeline
	 */
	// =======================================================
	@Override
  public FrameworkPipeline createPipeline(String[] pArgs) {

	    FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
	    UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);
      pipeline.setTypeDescriptorClassPath("gov.nih.cc.rmd.framework.bodyFunction.BodyFunctionModel");  //<--- in the 60_03-type.descriptor/src/main/resources ... folder
	   
      pipeline.add(  GATEBodyFunctionRemoveAnnotator.class.getCanonicalName(), argsParameter );
       
     
      FrameworkRMDSentencePipeline.setPipeline(pipeline, pArgs);
     
      
	    pipeline.add(              BodyLocationAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyStrengthAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyRangeOfMotionAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyReflexAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyFunctionQualifierAnnotator.class.getCanonicalName(), argsParameter);
	   
	    pipeline.add(              BodyFunctionAnnotator.class.getCanonicalName(), argsParameter);
	  //  pipeline.add(              BodyFunctionTypeAnnotator.class.getCanonicalName(), argsParameter);
	    
	    pipeline.add(              BodyFunction2MentionAnnotator.class.getCanonicalName(), argsParameter);
	    
	    pipeline.add(              FilterWriter.class.getCanonicalName(), argsParameter ) ; 
	   
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

