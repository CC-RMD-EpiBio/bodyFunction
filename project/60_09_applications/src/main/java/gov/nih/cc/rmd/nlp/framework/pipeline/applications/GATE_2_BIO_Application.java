// ------------------------------------------------------------
/**
 * This method reads in the gate files, converts the
 * body function type to Strength, ROM, Reflex types
 * Writes those out to a new GATE file.
 * 
 * From that, the python createBIOFormatFromGATEAnnotations.py
 * is call on them.
 * 
 * @author  G.o.d.
 * @created 2020.09.04
 * 
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.csv.CSVWriter;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionEvaluationPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.GATE_2_BIOPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.NoOpPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

import org.apache.uima.analysis_engine.AnalysisEngine;


public class GATE_2_BIO_Application {

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
      GATE_2_BIOPipeline   pipeline = new GATE_2_BIOPipeline( args );
    
      AnalysisEngine                ae = pipeline.getAnalysisEngine();
      application.setAnalsyisEngine(ae);

      // -------------------
      // Create a reader <on vinci, this should be the multi-record reader>
      // -------------------
      // application.createReader( FrameworkBaselineApplication.MULTI_RECORD_FILE_READER, args);
      //   application.createReader(FrameworkBaselineApplication.GATE_READER, args);
        application.createReader(FrameworkBaselineApplication.GATE_READER, args);

      // ------------------
      // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
      // ------------------
      application.addWriter(FrameworkBaselineApplication.BIO_WRITER, args);
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
    String   printToConsole = U.getOption(pArgs, "--printToConsole=", "true");
    String  outputTypes = U.getOption(pArgs, "--outputTypes=" ,"Body_location:Body_Function:Body_Function_Type:Qualifier:BFQualifierCategory_0:BFQualifierCategory_Minus_1:BFQualifierCategory_Plus_1:Strength:RangeOfMotion:Reflex:BFT_Ambiguous" );
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
   
   
    String gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
   
    
    String localTerminologyFiles = U.getOption(pArgs, "--localTerminologyFiles=",
        BodyFunctionAnnotator.BodyFunction_LRAGR_FILES  + ":" + 
        SyntaticPipeline.SyntaticMinimalTerminologyFiles + ":" + 
        AssertionAnnotator.EvidenceLRAGRFiles
         );                                       
                
       
   
    String addNewLines = U.getOption(pArgs,  "--addNewLines=", "false" );  
    String deIdentified = U.getOption(pArgs,  "--deIdentified=", "false" ); 
    
  
    String args[] = {
        
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--printToConsole=" + printToConsole,
        
        "--setMetaDataHeader=" + setMetaDataHeader,
        "--outputTypes=" + outputTypes,
        
 
        "--gateHome="     + gateHome,
   //     "--localTerminologyFiles=" + localTerminologyFiles,
       
       
        "--addNewLines=" + addNewLines,
        "--deIdentified=" + deIdentified
      
       
    };

     // need a help option here 
    // This method assumes that there is a resources/BodyFunctionApplication.txt
    Use.usageAndExitIfHelp( "GATE_2_BIO_Application", pArgs, args ) ;
                            

    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

} // End SyntaticApplication Class -------------------------------
