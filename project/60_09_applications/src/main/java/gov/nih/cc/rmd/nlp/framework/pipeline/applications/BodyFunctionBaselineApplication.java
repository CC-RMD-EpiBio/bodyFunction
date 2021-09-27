// ------------------------------------------------------------
/**
 * BodyFunctionApplication calls a BodyFunctionPipeline in such a way that 
 * it can be used within gui's.
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionPipeline;

// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class BodyFunctionBaselineApplication extends BaselineApplication {



  public BodyFunctionBaselineApplication() throws Exception {
    super();
   
  }




  //------------------------------------------
  /**
  * init
  *
  * @param pInputDir
  * @throws Exception
  */
  //------------------------------------------
  @Override
  public FrameworkBaselineApplication init(String[] pArgs) throws Exception {
    
    try {
  
     
     // --------------------
      // Read in the arguments needed for this application
      // --------------------
       this.setArgs(pArgs);
  
      // -------------------
      // Create a pipeline
      // -------------------
      BodyFunctionPipeline        bodyFunctionPipeline = new BodyFunctionPipeline( this.args );
   
      this.application = this.init( bodyFunctionPipeline);
      
  
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println("Issue initializing app " + e.toString());
      throw e;
      
    }
    
   
    return this.application;
  } // End Method init() ======================
  
  

// ------------------------------------------
/**
 * main
 * Options:
 * 
 * 
 * @param pArgs
 */
// ------------------------------------------
public static void main(String[] pArgs) {

  try {

    String[] args = BodyFunctionApplication.setArgs(pArgs);
    
    BodyFunctionBaselineApplication app = new BodyFunctionBaselineApplication();
    app.init(args);
    
  // -------------------
    // Create a reader <on vinci, this should be the multi-record reader>
    // -------------------
    app.createReader(FrameworkBaselineApplication.TEXT_READER, pArgs);

    // ------------------
    // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
    // ------------------
    app.addWriter(FrameworkBaselineApplication.VTT_WRITER, args);
    app.addWriter(FrameworkBaselineApplication.STATS_WRITER, args);
    app.addWriter(FrameworkBaselineApplication.XMI_WRITER, args);
    app.addWriter(FrameworkBaselineApplication.BIOC_WRITER, args);
  
    // ------------------
    // gather and process the cas's
    // -----------------
    app.run();
  
    // ----------------------
    // Finalize 
    app.finalize();

  } catch (Exception e2) {
    e2.printStackTrace();
    String msg = "Issue with the application " + e2.toString();
    System.err.println(msg);
  }
  System.exit(0);

} // End Method main() -----------------------------------



} // End BodyFunctionBaselineApplication Class -------------------------------
