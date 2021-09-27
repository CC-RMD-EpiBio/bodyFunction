// ------------------------------------------------------------
/**
 * BodyFunctionApplication kicks off the BodyFunction pipeline on a corpus
 * This application runs from annotated gate files and creates vtt and xmi output files.
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 * 
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.csv.CSVWriter;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

import org.apache.uima.analysis_engine.AnalysisEngine;


public class BodyFunctionApplication {

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
      BodyFunctionPipeline   pipeline = new BodyFunctionPipeline( args );
      AnalysisEngine                ae = pipeline.getAnalysisEngine();
      application.setAnalsyisEngine(ae);

      // -------------------
      // Create a reader <on vinci, this should be the multi-record reader>
      // -------------------
    application.createReader( args);

      // ------------------
      // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
      // ------------------
      application.addWriters(args);
    
   
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
    // Input Reader
    String inputFormat = U.getOption(pArgs, "--inputFormat=", FrameworkBaselineApplication.TEXT_READER_ );
    
   
    // ------------------------------------
    // output writers
    // ---------------------------------------
    String outputFormat = U.getOption(pArgs, "--outputFormat=", 
        FrameworkBaselineApplication.XMI_WRITER_ + ":" +
        FrameworkBaselineApplication.GATE_WRITER_ + ":" +
        FrameworkBaselineApplication.GATE_CORPUS_WRITER + ":" +
        FrameworkBaselineApplication.VTT_WRITER_ + ":" +
        FrameworkBaselineApplication.TEXT_WRITER_ + ":" +
        FrameworkBaselineApplication.BIO_WRITER_ + ":" +
        FrameworkBaselineApplication.CSV_WRITER_ + ":" +
        FrameworkBaselineApplication.SNIPPET_WRITER_ );
    
    
    // -------------------------------------
    // Input and Output
    
    String    inputDir  = U.getOption(pArgs, "--inputDir=", "600_BodyFunction/60_01_data/examples");
    String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_BodyFunction_" + dateStamp);
    String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
    String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
    String   printToConsole = U.getOption(pArgs, "--printToConsole=", "true");
    String  outputTypes = U.getOption(pArgs, "--outputTypes=" ,"BodyFunctionMention:Qualifier:Strength:RangeOfMotion:Reflex:BodyLocation" );
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
    String csvFormatType = U.getOption(pArgs,"--csvFormatType=", CSVWriter.formatType_Tokenized ); 
    
    // ------------------------------------
    // VTT Snippet elements
    // ------------------------------------
    String  focusLabel = U.getOption(pArgs, "--focusLabel=", "BodyFunctionMention");
    String  sampleRate = U.getOption(pArgs, "--sampleRate=",  "1");
    String  snippetsPerFile = U.getOption(pArgs, "--snippetsPerFile=", "200");
    String snippetWriterType = U.getOption(pArgs, "--snippetWriterType=", "VTT").toLowerCase();
    
    String gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
   
    
    String localTerminologyFiles = U.getOption(pArgs, "--localTerminologyFiles=",
        "resources/vinciNLPFramework/term/2011/SPECIALIST_PART1.LRAGR" + ":"  + 
        "resources/vinciNLPFramework/term/2011/SPECIALIST_PART2.LRAGR" + ":"  +
        BodyFunctionAnnotator.BodyFunction_LRAGR_FILES  + ":" + 
        SyntaticPipeline.SyntaticMinimalTerminologyFiles + ":" + 
        AssertionAnnotator.EvidenceLRAGRFiles
         );                                            
  
    String addNewLines = U.getOption(pArgs,  "--addNewLines=", "false" );  
    String deIdentified = U.getOption(pArgs,  "--deIdentified=", "false" ); 
    String version = "2021-09-22.0";
    
    String args[] = {
        
        "--inputFormat=" + inputFormat,
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        "--outputFormat=" + outputFormat,
        
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--printToConsole=" + printToConsole,
        
        "--setMetaDataHeader=" + setMetaDataHeader,
        "--outputTypes=" + outputTypes,
        "--focusLabel=" + focusLabel,
        "--sampleRate=" + sampleRate, 
        "--snippetsPerFile=" + snippetsPerFile,
        "--snippetWriterType=" + snippetWriterType,
        "--csvFormatType="  + csvFormatType,
 
        "--gateHome="     + gateHome,
        "--localTerminologyFiles=" + localTerminologyFiles,
        "--addNewLines=" + addNewLines,
        "--deIdentified=" + deIdentified,
        "--version=" + version
        
      
       
    };

  //  if ( Use.version(pArgs, args ) ||
     if (   Use.usageAndExitIfHelp( "BodyFunctionApplication", pArgs, args ) )
      Runtime.getRuntime().exit(0);
                          

    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

} // End SyntaticApplication Class -------------------------------
