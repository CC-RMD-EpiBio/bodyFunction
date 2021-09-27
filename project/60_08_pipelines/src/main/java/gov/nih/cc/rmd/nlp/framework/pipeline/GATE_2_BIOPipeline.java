package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionQualifierConverter;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionTypeConverter;


// =================================================
/**
 * GATE_2_BIOPipeline runs the pipeline to convert the human annotated files
 * (body function type -> Strength |ROM |Reflex labels
 * then to convert the files to bio format
 *        
 * 
 * @author  G.o.d.
 * @created 2020.09.04
 *
 */


  public class GATE_2_BIOPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public GATE_2_BIOPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public GATE_2_BIOPipeline()  throws Exception {
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
	   
      
     
      
      FrameworkRMDSentencePipeline.setPipeline(pipeline, pArgs);
     
      
	    pipeline.add(              GATEBodyFunctionTypeConverter.class.getCanonicalName(), argsParameter);
	    pipeline.add(              GATEBodyFunctionQualifierConverter .class.getCanonicalName(), argsParameter);
	   
	   
	    
	   
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

