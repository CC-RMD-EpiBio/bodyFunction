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
