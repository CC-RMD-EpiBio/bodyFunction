package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.RelabelAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionPolarityEvaluationAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionToCopperAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionToGoldAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.GATEBodyFunctionTypeConverter;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.RemovePossibleBodyFunctionAnnotationsAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.evaluate.EvaluateWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.evaluate.TokenEvaluateWriter;
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


  public class BodyFunctionEvaluationPipeline extends BodyFunctionPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public BodyFunctionEvaluationPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public BodyFunctionEvaluationPipeline()  throws Exception {
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

	  FrameworkPipeline pipeline = super.createPipeline(pArgs);
    
	  boolean evaluatePolarity  = Boolean.valueOf(U.getOption(pArgs,   "--evaluatePolarity=", "false"));
    
	  String args[] = U.addArg(pArgs, "--labelPairs=", "Strength:BodyFunctionKind|RangeOfMotion:BodyFunctionKind|Reflex:BodyFunctionKind");
	  
    UimaContextParameter argsParameter = new UimaContextParameter("args", args, "String", true, true);
    pipeline.addAtBeginning(  GATEBodyFunctionToGoldAnnotator.class.getCanonicalName(), argsParameter );
    
    pipeline.addAtEnd(      RemovePossibleBodyFunctionAnnotationsAnnotator.class.getCanonicalName(),argsParameter);
    pipeline.addAtEnd      (BodyFunctionToCopperAnnotator.class.getCanonicalName(), argsParameter  );
      
    pipeline.addAtEnd       ( EvaluateWriter.class.getCanonicalName(), argsParameter  );  // <--- can only be done 
                                                                                                   //      here if not in
                                                                                                   //      scale-out mode
                                                                                                   //      Use here when you want
                                                                                                   //      to see xmi output
     pipeline.add(             RelabelAnnotator.class.getCanonicalName(), argsParameter);
    
     pipeline.addAtEnd       ( TokenEvaluateWriter.class.getCanonicalName(), argsParameter  ); 
     
     if ( evaluatePolarity )
       pipeline.addAtEnd(   BodyFunctionPolarityEvaluationAnnotator.class.getCanonicalName(), argsParameter );    
    
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline

