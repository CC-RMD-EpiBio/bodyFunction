package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionHumanAnnotationStatsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionToCopperAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionToGoldAnnotator;


// =================================================
/**
 * BodyFunctionHumanAnnotationStatsPipeline runs the pipeline for bodyFunction
 *        
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */


  public class BodyFunctionHumanAnnotationStatsPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public BodyFunctionHumanAnnotationStatsPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public BodyFunctionHumanAnnotationStatsPipeline()  throws Exception {
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
   
    pipeline.add( BodyFunctionHumanAnnotationStatsAnnotator.class.getCanonicalName(), argsParameter  );  
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

