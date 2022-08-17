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
 * BodyQualifierPolarityAnnotator assigns a polarity to the qualifer (-1,0,1)
 * and the whole mention
 * 
 *  
 *
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.BFQualifier;
import gov.nih.cc.rmd.framework.BFQualifierCategory_0;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Minus_1;
import gov.nih.cc.rmd.framework.BFQualifierCategory_Plus_1;
import gov.nih.cc.rmd.gate.GATE_BFQualifierCategory_0;
import gov.nih.cc.rmd.gate.GATE_BFQualifierCategory_Minus_1;
import gov.nih.cc.rmd.gate.GATE_BFQualifierCategory_Plus_1;
import gov.nih.cc.rmd.gate.Qualifier;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;





public class BodyFunctionQualifierPolarityAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * BodyQualifiers from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
  
   
    List<Annotation> mentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID );
    
    if ( mentions != null && !mentions.isEmpty() )
      for ( Annotation mention: mentions ) {
        assignPolarity( pJCas, mention );
        
        if ( this.evaluateQualifier ) {
          createBFPolarityCategories( pJCas, mention );
        }
      }
      
     if ( this.evaluateQualifier ) {
       List<Annotation> goldBFMentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.gate.Qualifier.typeIndexID );
       
       if ( goldBFMentions != null && !goldBFMentions.isEmpty()) {
         for ( Annotation goldBFMention : goldBFMentions )
           createGATEBFPolarityCategories( pJCas, goldBFMention );
       }
     
     }
    
     
     this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  
  } // end Method process() ----------------
   



  // =================================================
  /**
   * createGATEBFPolarityCategories 
   * 
   * @param pJCas
   * @param pGoldMention
  */
  // =================================================
   private void createGATEBFPolarityCategories(JCas pJCas, Annotation pGoldMention) {
    
    String category = ((Qualifier)pGoldMention).getCategory();
    
    if  ( category != null ) {
      switch ( category ) {
        case "+1" :  createQualifierCategoryAnnotation( pJCas, new GATE_BFQualifierCategory_Plus_1 (pJCas), pGoldMention );   break;
        case "-1" :  createQualifierCategoryAnnotation( pJCas, new GATE_BFQualifierCategory_Minus_1(pJCas), pGoldMention );   break;  
        case "0"  :  createQualifierCategoryAnnotation( pJCas, new GATE_BFQualifierCategory_0      (pJCas), pGoldMention );   break;  
        default:
       }
    }
     
    
   } // end Method createGATEBFPolarityCategories() --------

   // =================================================
   /**
    * createBFPolarityCategories 
    * 
    * @param pJCas
    * @param pGoldMention
   */
   // =================================================
    private void createBFPolarityCategories(JCas pJCas, Annotation pGoldMention) {
     
     String category = ((Qualifier)pGoldMention).getCategory();
     
    
     
     if  ( category != null ) {
       switch ( category ) {
         case "+1" :  createQualifierCategoryAnnotation( pJCas, new BFQualifierCategory_Plus_1(pJCas), pGoldMention );   break;
         case "-1" :  createQualifierCategoryAnnotation( pJCas, new BFQualifierCategory_Minus_1(pJCas), pGoldMention );   break;  
         case "0"  :  createQualifierCategoryAnnotation( pJCas, new BFQualifierCategory_0(pJCas),       pGoldMention );   break;  
         default:
        }
     }
      
     
    } // end Method createBFPolarityCategories() --------





// =================================================
  /**
   * createQualifierCategoryAnnotation 
   * 
   * @param pJCas
   * @param pStatement
   * @param pMention
  */
  // =================================================
  private final void createQualifierCategoryAnnotation(JCas pJCas, Object pStatement, Annotation pGoldMention) {
   
    
    
    
  } // end Method createQualifierCategoryAnnotation() ----




  // =================================================
  /**
   * assignPolarity
   * 
   * @param pJCas
   * @param pMention
  */
  // =================================================
  private void assignPolarity(JCas pJCas, Annotation pMention) {
   
    // --------------
    // Find the Qualifiers for this mention
    // --------------
    List<Annotation> qualifiers = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BFQualifier.typeIndexID,  pMention.getBegin(), pMention.getEnd());
    
    ArrayList<String> polarities = new ArrayList<String>();
    StringBuffer buff = new StringBuffer();
    if ( qualifiers != null && !qualifiers.isEmpty() )
      for ( Annotation qualifier: qualifiers) {
        buff.append( qualifier.getCoveredText());
        buff.append(":");
        polarities.add(   ((BFQualifier)qualifier).getPolarity());
        ;
      }
    
    
    
    
  } // end Method assignPolarity () ---------------






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
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    this.evaluateQualifier        = Boolean.valueOf(U.getOption(pArgs,   "--evaluateQualifier=", "false" ));
    String evaluatePolarityPlusOne  = U.getOption(pArgs,   "--evaluatePolarityPlusOne", "false");
    String evaluatePolarityMinusOne = U.getOption(pArgs,   "--evaluatePolarityMinusOne", "false");
    String evaluatePolarityZero     = U.getOption(pArgs,   "--evaluatePolarityZero", "false");
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  boolean evaluateQualifier = false;
  
  
  
  
} // end Class LineAnnotator() ---------------

