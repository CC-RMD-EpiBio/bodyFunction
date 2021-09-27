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
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionNoOpAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionRemoveAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionToGoldAnnotator;

// =================================================
/**
 * BodyFunctionPipeline does next to nothing other than set the descriptor, and remove the
 * document annotation.
 *        
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */


  public class FromGATEPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public FromGATEPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public FromGATEPipeline()  throws Exception {
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
	   
      pipeline.add(  GATEBodyFunctionNoOpAnnotator.class.getCanonicalName(), argsParameter );
       
     
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

