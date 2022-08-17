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
 *         Divita G, Lo J, Zhou C, Coale K, Rasch E. Extracting Body Function from Clinical Text. 
 *         InAI4Function@ IJCAI 2021 (pp. 22-35).
 *      
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
// =================================================
/**
 * BodyFunction Annotator finds gate imported BodyFunction
 * annotations, and turns them into one of two
 * kinds
 *    BodyLocation  -->gold
 *    BodyStrength  -->gold
 *    RangeOfMOtion -->gold
 *    Reflex        -->gold
 * 
 *
 *
 * @author  Guy Divita 
 * @created May 20, 2020
 *
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
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.vinci.model.Gold;
import gov.nih.cc.rmd.framework.Manual_Section_Name;
import gov.nih.cc.rmd.gate.Qualifier;



public class GATEBodyFunctionToGoldAnnotator extends JCasAnnotator_ImplBase {
 
  

  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();

    // -------------------------
    // Strip Manual Section_Name annotations - they are causing problems
    strip_manual_sectionNames( pJCas );
    
    if ( this.evaluateBodyLocation )
      process_BodyLocation( pJCas);
    
    if ( this.evaluateBodyFunctionType )
      process_BodyFunctionType( pJCas);

    /*
   
    boolean processQualifier = true;
    if ( this.evaluatePolarityPlusOne ) {
      process_QualifierPolarity( pJCas, "+1");
      processQualifier = false;
    }
    if ( this.evaluatePolarityMinusOne ) {
      process_QualifierPolarity( pJCas, "-1");
       processQualifier = false;
    }
    if ( this.evaluatePolarityZero  ) {
      process_QualifierPolarity( pJCas, "0");
      processQualifier = false;
    }
    
    */
    if ( this.evaluateQualifier  )
      process_Qualifier( pJCas);
    
    
    if ( this.evaluateBodyFunction )
      process_BodyFunction( pJCas);
      
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

 
 // =================================================
  /**
   * strip_manual_sectionNames removes section names that came in
   * from pre-annotation into the gate file, then from the gate file,
   * turned into Manual_Section_Name
   * 
   * @param pJCas
  */
  // =================================================
  private void strip_manual_sectionNames(JCas pJCas) {
    
    List<Annotation> manualSectionNames = UIMAUtil.getAnnotations(pJCas, Manual_Section_Name.typeIndexID);
    
    if ( manualSectionNames != null && !manualSectionNames.isEmpty())
      for ( Annotation sectionName : manualSectionNames )
        sectionName.removeFromIndexes();
    
    
  } // end Method strip_manual_sectionNames() -----



// =================================================
  /**
   * process_Qualifier 
   * 
   * @param pJCas
  */
  // =================================================
  private final void process_Qualifier(JCas pJCas) {
    
 List<Annotation> manualQualifiers  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID, true);
    
    if ( manualQualifiers != null && !manualQualifiers.isEmpty() ) 
      for ( Annotation manualQualifier : manualQualifiers ) 
        createGoldAnnotation( pJCas, manualQualifier);
    
    
  } // end Method process_Qualifier() ----------------

  
//=================================================
 /**
  * process_QualifierPolarityPlusOne 
  * 
  * @param pJCas
  * @param pCategory [-1|0|+1]
 */
 // =================================================
 private final void process_QualifierPolarity(JCas pJCas, String pCategory) {
   
List<Annotation> manualQualifiers  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID, true);
   
   if ( manualQualifiers != null && !manualQualifiers.isEmpty() ) 
     for ( Annotation manualQualifier : manualQualifiers ) { 
       String category = ((Qualifier) manualQualifier).getCategory();
       if ( category != null && category.equals(pCategory ))
         createGoldAnnotation( pJCas, manualQualifier );
     }
   
   
 } // end Method process_QualifierPolarityPlusOne() ----------------
  
  

  // =================================================
  /**
   * process_bodyLocation 
   * 
   * @param pJCas
  */
  // =================================================
  private void process_BodyLocation(JCas pJCas) {

    List<Annotation> manualBodyLocations  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_location.typeIndexID, true);
    
    if ( manualBodyLocations != null && !manualBodyLocations.isEmpty() ) 
      for ( Annotation bodyLocation : manualBodyLocations ) 
        createGoldAnnotation( pJCas, bodyLocation);
    
    
  } // end Method process_BodyLocation() -----------



  // =================================================
  /**
   * process_bodyfunction type - [tbd] need to capture the types as attributes
   * 
   * @param pJCas
  */
  // =================================================
  private final void process_BodyFunctionType(JCas pJCas) {
    
  List<Annotation> manualBodyFunctionType  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_Function_TType.typeIndexID, true);
    
    if ( manualBodyFunctionType != null && !manualBodyFunctionType.isEmpty() ) 
      for ( Annotation bodyfunctionType : manualBodyFunctionType ) 
        createGoldAnnotation( pJCas, bodyfunctionType);
    
  } // end Method process_BodyFunctionType() --------


//=================================================
 /**
  * process_bodyfunction  
  * 
  * @param pJCas
 */
 // =================================================
 private final void process_BodyFunction(JCas pJCas) {
   
 List<Annotation> manualBodyFunctions  = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Body_Function.typeIndexID, true);
   
   if ( manualBodyFunctions != null && !manualBodyFunctions.isEmpty() ) 
     for ( Annotation bodyfunction : manualBodyFunctions ) 
       createGoldAnnotation( pJCas, bodyfunction);
   
 } // end Method process_BodyFunction() --------

  

//=================================================
 /**
  * createGoldAnnotation creates a Gold annotaton
  *    to capture the label being evaluated
  * @param pJCas
  * @param pTerm
 */
 // =================================================
 private void createGoldAnnotation(JCas pJCas, Annotation pTerm) {
   
   
   Gold statement = new Gold( pJCas);
   statement.setBegin( pTerm.getBegin());
   statement.setEnd(   pTerm.getEnd() );
 
 
   statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);

  
   
   statement.addToIndexes();
   String sectionName = VUIMAUtil.deriveSectionName(statement);
   statement.setSectionName(sectionName);
   
 } // end Method createActionAnnotation() ----------



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
   *                
   *                The parameters looked for are
   *                --evaluateBodyFunction=false
   *                --evaluateQualifier=false
   *                --evaluateBodyFunctionType=false
   *                --evaluateBodyLocation=false
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    this.evaluateBodyFunction     = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateBodyFunction=", "false" )); 
    this.evaluateBodyFunctionType = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateBodyFunctionType=", "false" )); 
    this.evaluateBodyLocation     = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateBodyLocation=", "false" )); 
    this.evaluateQualifier        = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluateQualifier=", "false" )); 
    
    this.evaluatePolarityPlusOne  = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluatePolarityPlusOne", "false"));
    this.evaluatePolarityMinusOne = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluatePolarityMinusOne", "false"));
    this.evaluatePolarityZero     = Boolean.parseBoolean(U.getOption(pArgs,   "--evaluatePolarityZero", "false"));
 
    
    
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;

  private boolean evaluateBodyFunction = false;
  private boolean evaluateQualifier = false;
  private boolean evaluateBodyFunctionType = false;
  private boolean evaluateBodyLocation = false;
  private boolean evaluatePolarityPlusOne  = false;
  private boolean evaluatePolarityMinusOne = false;
  private boolean evaluatePolarityZero     = false; 

 
  
  
  
} // end Class LineAnnotator() ---------------
