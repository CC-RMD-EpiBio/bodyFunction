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
// =================================================
/**
 * BodyFunctionAPITest.java  
 *
 * @author     Guy Divita
 * @created    Mar 4, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import org.apache.uima.jcas.JCas;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FrameworkTestUtilities;

/**
 * @author divitag2
 *
 */
public class BodyFunctionAPITest {
  
  
  // -------------------------------------------------
  // Setup Global test fields
  static ApplicationAPI applicationAPI = null;
  
  
  

  // =================================================
  /**
   * Constructor
   *
   * 
  **/
  // =================================================
  public BodyFunctionAPITest() {
   
  String  outputTypes = "BodyLocation:BodyFunction" ;
    
    String args1[] = {"--outputTypes=" + outputTypes , "--printToLog=false" };
    String[] args = BodyFunctionApplication.setArgs(args1);
    try {
      applicationAPI = new BodyFunctionAPI(args);
    } catch (Exception e) {
      e.printStackTrace();
      
      
    }
  }

  // =================================================
  /**
   * setUpBeforeClass [TBD] summary
   * 
   * @throws java.lang.Exception
  */
  // =================================================
  @Before
   public void setUpBeforeClass() throws Exception {
    
  
  }

  // =================================================
  /**
   * tearDownAfterClass [TBD] summary
   * 
   * @throws java.lang.Exception
  */
  // =================================================
  @After
   public void tearDownAfterClass() throws Exception {
    
    // ----------------
    // clean up
    // ----------------
    applicationAPI.destroy();

  }


  @Test
  public void testBodyLocation() {
    // -------------------
    // Create an API instance
    // -------------------

    try {

    

      // ---------------------------------------
      // Test if you can pass something in, and get something out
      // ---------------------------------------

      // ------------------
      // Magic happens here ----+
      // ------------------ \|/

      String inputText = 
      "STRENGTH:\n" +
      "RIGHT LOWER EXTREMITY:\n" + 
      "Knee extension 5/5, hip flexion 5/5, knee flexion 4/5, internal and external hip rotation was 4/5.\n" +
      "With manual muscle testing of knee flexion, hip, internal and external rotation, the patient reports\n" +
      "an increase in right SI joint pain to 8/10.\n";
      
      JCas testResults = applicationAPI.processToCas(inputText  );
        

      // --------------- /|\
      // Magic happens here --+
      // ---------------

      // ---------------
      // find the correct annotation
      // ---------------
         
         
      String pAnnotationType = "gov.nih.cc.rmd.framework.BodyLocation";   // <------ without namespace 
      String strengthType = "Strength";
      String romType = "RangeOfMotion";
      
      String pSpanString = "Knee";
      String strengthMention = "flexion";
      
      boolean found = FrameworkTestUtilities.findAnnotation( testResults, strengthType, strengthMention );

      
      String msg = "Strength :" + found ;
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "testStrength", msg );
      Assert.assertTrue(msg, found);
      
      
    

    } catch (Exception e) {
      e.printStackTrace();
      // where do test hook exceptions go?
      Assert.fail("Missed Rom ");
    }

  } // end testBodyLocation() --------------------
 
  
  

  // =================================================
  /**
   * main 
   * 
   * @param pArgs
  */
  // =================================================
  public static void main(String[] pArgs) {
    
    try {
      
      BodyFunctionAPITest tests = new BodyFunctionAPITest();
      tests.setUpBeforeClass();
      tests.testBodyLocation();
    
      tests.tearDownAfterClass();
      
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println("Issue with tests: " + e.toString());
    }
    
  } // end Method main() -----------------------------
  
  
  

} // end Class BodyFunctionAPITest() ---------------
