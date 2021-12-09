package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionQualifierAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionQualifierPolarityAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionTypeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyLocationAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyRangeOfMotionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyReflexAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyStrengthAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.U;

// =================================================
/**
 * BodyFunctionPipeline runs the pipeline for bodyFunction
 *        
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */


  public class BodyFunctionPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public BodyFunctionPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public BodyFunctionPipeline()  throws Exception {
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
	    String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.framework.bodyFunction.BodyFunctionModel");
      pipeline.setTypeDescriptorClassPath(typeDescriptor);  //<--- in the 60_03-type.descriptor/src/main/resources ... folder
	   
      
     
      FrameworkRMDSentencePipeline.setPipeline(pipeline, pArgs);
     
      
	    pipeline.add(              BodyLocationAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyStrengthAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyRangeOfMotionAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyReflexAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyFunctionQualifierAnnotator.class.getCanonicalName(), argsParameter);
	   
	    pipeline.add(              BodyFunctionAnnotator.class.getCanonicalName(), argsParameter);
	    pipeline.add(              BodyFunctionQualifierPolarityAnnotator.class.getCanonicalName(), argsParameter);
	     
	    
	   
	   // pipeline.add(              FilterWriter.class.getCanonicalName(), argsParameter ) ; 
	   
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

