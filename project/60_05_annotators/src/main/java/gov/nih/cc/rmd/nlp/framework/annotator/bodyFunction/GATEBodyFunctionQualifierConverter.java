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
 * GATEBodyFunctionQualifierConverter finds gate body function qualifiers
 * with attributes and converts them to separate labels
 * for BFQCategory_0, BFQCategory_Minus_1, BFQCategory_Plus_1 
 *   
 * 
 *
 *
 * @author  Guy Divita 
 * @created May 4, 2021
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

import gov.nih.cc.rmd.framework.BFQualifierCategory_0;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Minus_1;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Plus_1;
import gov.nih.cc.rmd.gate.Qualifier;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class GATEBodyFunctionQualifierConverter extends JCasAnnotator_ImplBase {
 
  

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

    
   
     process_BodyFunctionQualifiers( pJCas);

    
      
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

 

  

  // =================================================
  /**
   * process_BodyFunctionQualifiers transforms the attributes into separate annotations
   * 
   * @param pJCas
  */
  // =================================================
  private final void process_BodyFunctionQualifiers(JCas pJCas) {
    
  List<Annotation> manualBodyFunctionQualifiers  = UIMAUtil.getAnnotations(pJCas, Qualifier.typeIndexID, true);
    
   
    if ( manualBodyFunctionQualifiers != null && !manualBodyFunctionQualifiers.isEmpty() ) 
      for ( Annotation BodyFunctionQualifier : manualBodyFunctionQualifiers ) {
        
        String qualifierCategory = ((Qualifier) BodyFunctionQualifier ).getCategory();
        if ( qualifierCategory != null && qualifierCategory.trim().length() > 0 ) 
          // BFQCategory_0, BFQCategory_Minus_1, BFQCategory_Plus_1 
          if      ( qualifierCategory.contains("0"))  ceateBFQCategory_0( pJCas, BodyFunctionQualifier );
          else if ( qualifierCategory.contains("1"))  ceateBFQCategory_Plus_1( pJCas, BodyFunctionQualifier);
          else if ( qualifierCategory.contains("-1")) ceateBFQCategory_Minus_1( pJCas, BodyFunctionQualifier);
          else System.err.println(" odd kind: " + qualifierCategory);  
        
        BodyFunctionQualifier.removeFromIndexes();
        
      }
        
      
        
    
  } // end Method process_BodyFunctionQualifiers() --------


  // =================================================
  /**
   * ceateBFQCategory_Plus_1 
   * 
   * @param pJCas
   * @param BodyFunctionQualifierCategory
  */
  // =================================================
  private final void ceateBFQCategory_Plus_1(JCas pJCas, Annotation BodyFunctionQualifierCategory) {
    
    BFQualifierCategory_Plus_1 statement = new BFQualifierCategory_Plus_1( pJCas);
    statement.setBegin( BodyFunctionQualifierCategory.getBegin());
    statement.setEnd(   BodyFunctionQualifierCategory.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createStrengthAnnotation() ---------




  // =================================================
  /**
   * ceateBFQCategory_Minus_1 
   * 
   * @param pJCas
   * @param BodyFunctionQualifierCategory
  */
  // =================================================
  private final void ceateBFQCategory_Minus_1(JCas pJCas, Annotation BodyFunctionQualifierCategory) {
    
    BFQualifierCategory_Minus_1 statement = new BFQualifierCategory_Minus_1( pJCas);
    statement.setBegin( BodyFunctionQualifierCategory.getBegin());
    statement.setEnd(   BodyFunctionQualifierCategory.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createStrengthAnnotation() ---------
  

  // =================================================
  /**
   * ceateBFQCategory_0 
   * 
   * @param pJCas
   * @param BodyFunctionQualifierCategory
  */
  // =================================================
  private final void ceateBFQCategory_0(JCas pJCas, Annotation BodyFunctionQualifierCategory) {
    
    BFQualifierCategory_0 statement = new BFQualifierCategory_0( pJCas);
    statement.setBegin( BodyFunctionQualifierCategory.getBegin());
    statement.setEnd(   BodyFunctionQualifierCategory.getEnd() );
    statement.setId  ( this.getClass().getSimpleName() + "_" + annotationCtr);
    statement.addToIndexes();
    
  } // end Method createStrengthAnnotation() ---------


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
   *                --evaluateBodyFunctionQualifiers=false
   *                --evaluateBodyLocation=false
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;

 
  
  
  
} // end Class LineAnnotator() ---------------
