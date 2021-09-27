// ------------------------------------------------------------
/**
 * This main reads in the gate corpus, converts the
 * body function type to Strength, ROM, Reflex types
 * Writes those out to uima xml files.
 * 
 *
 * 
 * @author  G.o.d.
 * @created 2021.08.09
 * 
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.pipeline.FromGATEPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.NoOpPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;


public class GATE_Corpus2_GATE_UIMA_Application {

  // ------------------------------------------
  /**
   * main
   * 
   * 
   * @param pArgs
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {

    try {
      
      // --------------------
      // Read in any command line arguments, and add to them needed ones
      // The precedence is command line args come first, followed by those
      // set by the setArgs method, followed by the defaults set here.
      // --------------------
      String args[] = setArgs(pArgs);

      GLog.set( args );
      
      
      PerformanceMeter meter = new PerformanceMeter(System.err);
      meter.begin("Starting the application");
     
       // -------------------
      // Create a BaselineFrameworkApplication instance
      // -------------------
      FrameworkBaselineApplication application = new FrameworkBaselineApplication();

      // -------------------
      // Add a performance meter to the application (This is optional)
      application.addPerformanceMeter(meter);

      // -------------------
      // Create a pipeline, retrieve an analysis engine
      // that uses the pipeline, attach it to the application
      // -------------------
      FromGATEPipeline   pipeline = new FromGATEPipeline( args );
    
      AnalysisEngine                ae = pipeline.getAnalysisEngine();
      application.setAnalsyisEngine(ae);

      // -------------------
      // Create a reader <on vinci, this should be the multi-record reader>
      // -------------------
      // application.createReader( FrameworkBaselineApplication.MULTI_RECORD_FILE_READER, args);
      //   application.createReader(FrameworkBaselineApplication.GATE_READER, args);
        application.createReader(FrameworkBaselineApplication.GATE_CORPUS_READER, args);

      // ------------------
      // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
      // ------------------
      application.addWriter(FrameworkBaselineApplication.GATE_WRITER, args);
      application.addWriter(FrameworkBaselineApplication.XMI_WRITER, args);
     
      meter.mark("Finished Initialization");

      // ------------------
      // gather and process the cas's
      // -----------------

      try {
        application.process();
      } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue processing the files " + e.toString();
        System.err.println(msg);
      }

      meter.stop();

      // ----------------------
      System.err.println(" DOHN");

    } catch (Exception e2) {
      e2.printStackTrace();
      String msg = "Issue with the application " + e2.toString();
      System.err.println(msg);
    }
    System.exit(0);

  } // End Method main() -----------------------------------

  // ------------------------------------------
  /**
   * setArgs
   * 
   * 
   * @return
   */
  // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {

    // -------------------------------------
    // dateStamp
    String dateStamp = U.getDateStampSimple();

    // -------------------------------------
    // Input and Output

    String    inputDir  = U.getOption(pArgs, "--inputDir=", "600_BodyFunction/60_01_data/examples");
    String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_BodyFunction_" + dateStamp);
    String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
    String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
    String   outputTypes= U.getOption(pArgs, "--outputTypes =", "all" );
    String   printToConsole = U.getOption(pArgs, "--printToConsole=", "true");
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
   
   
    String gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
   
    String addNewLines = U.getOption(pArgs,  "--addNewLines=", "false" );  
    String deIdentified = U.getOption(pArgs,  "--deIdentified=", "true" ); 
    
  
    String args[] = {
        
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--printToConsole=" + printToConsole,
        
        "--setMetaDataHeader=" + setMetaDataHeader,
        
 
        "--gateHome="     + gateHome,
   //     "--localTerminologyFiles=" + localTerminologyFiles,
       
       
        "--addNewLines=" + addNewLines,
        "--deIdentified=" + deIdentified,
        "--outputTypes=" + outputTypes
      
       
    };

    Use.usageAndExitIfHelp( "GATE_Corpus2_GATE_UIMAApplicationUsage", pArgs, args ) ;
                            

    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

} // End SyntaticApplication Class -------------------------------
