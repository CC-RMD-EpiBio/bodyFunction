/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing the java-nlp-framework code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *     When attributing the Body Function code, please make reference to:
 *        Divita G, Lo J, Zhou C, Coale K, Rasch E. Extracting Body Function from Clinical Text. 
 *        InAI4Function@ IJCAI 2021 (pp. 22-35).
 *      
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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

