package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.noOp.NoOpAnnotator;

// =================================================
/**
 * BodyFunctionAddNewlines  runs a pipeline that adds
 * newlines were they should be for BTRIS data
 *        
 * 
 * @author  G.o.d.
 * @created 2020.08.04
 *
 */


  public class BodyFunctionAddNewlinesPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public BodyFunctionAddNewlinesPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public BodyFunctionAddNewlinesPipeline()  throws Exception {
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
	   
      
	    pipeline.add(             NoOpAnnotator.class.getCanonicalName(), argsParameter);
	    
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

