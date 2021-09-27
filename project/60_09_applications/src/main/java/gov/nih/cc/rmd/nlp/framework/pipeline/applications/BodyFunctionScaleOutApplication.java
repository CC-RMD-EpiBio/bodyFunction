// ------------------------------------------------------------
/**
 * BodyFunction ScaleOut Application runs N threads of the BodyFunction pipeline
 * 
 * @author g.o.d.
 * @created 2020.05.01
 * 
 // ------------------------------------------------------------
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

import org.apache.uima.analysis_engine.AnalysisEngine;


public class BodyFunctionScaleOutApplication {

  
  

  // ------------------------------------------
  /**
   * main
   *   Options:
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
      System.err.println(" compiled on 2019_10_03_15"  );

      String               outputDir = U.getOption(args, "--outputDir=", "./"); 
      System.err.println("The output should now be in " + outputDir );
       
      // ---------------------------
      // Performance configuration Parameters
      // ---------------------------
      int                      metric = Integer.parseInt(U.getOption(args,  "--metric=",                    "100"));
      int             numberToProcess = Integer.parseInt(U.getOption(args,  "--numberToProcess=",             "1"));
      int initialNumberOfApplications = Integer.parseInt(U.getOption(args,  "--initialNumberOfApplications=", "2"));
      int     maxNumberOfApplications = Integer.parseInt(U.getOption(args,  "--maxNumberOfApplications=",     "1"));
      int               maxSystemLoad = Integer.parseInt(U.getOption(args,  "--maxSystemLoad=",              "80"));
      int                   recycleAt = Integer.parseInt(U.getOption(args,  "--recycleAt=",                  "10"));
      int                     seconds = Integer.parseInt(U.getOption(args,  "--seconds=",                    "30"));
      
      
      // --------------------------
      // Set up a performance meter
      // --------------------------
      PerformanceMeter meter = FrameworkBaselineApplication.createPerformanceMeter( args);
      meter.begin("Starting the application");
      
     // -------------------
      // Create the framework set of servers
      // -------------------
      ScaleOutApplication application = new ScaleOutApplication(initialNumberOfApplications, outputDir, metric, recycleAt, numberToProcess);
      
 
      // -------------------
      // set up a reader  relies upon argument "--inputFormat= FrameworkBaselineApplication.TEXT_READER_  );
      // -------------------
      application.createReader( args );

      
      // -------------------
      // set up writer(s)
      //  String outputFormat = U.getOption(args, "--outputFormat=", FrameworkBaselineApplication.STATS_WRITER_);
      // -------------------
      application.addWriters(args ); // <---- relies upon "--outputFormat=FrameworkBaselineApplication.STATS_WRITER_);
 
      // -------------------
      // Create an engine with a pipeline 
      // -------------------
      application.setPipelines(BodyFunctionPipeline.class, args);
     
 
      // --------------------
      // Run until done : if the number to process == 1, it processes them all
      // --------------------
  
      application.run(numberToProcess, 
                      initialNumberOfApplications, 
                      maxNumberOfApplications,
                      maxSystemLoad,
                      recycleAt,
                      seconds);
      
     
      meter.stop();

      // ----------------------
      System.err.println(" DOHN");
     
     
      System.err.println("The output should now be in " + outputDir );
      
     

    } catch (Exception e2) {
      e2.printStackTrace();
      String msg = "Issue with the application " + e2.toString();
      System.err.println(msg);
    }
    
    System.exit(0);

  }  // End Method main() ------------------------------------

  
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

    String    inputDir  = U.getOption(pArgs, "--inputDir=", "/100_data/examples");
    String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_BodyFunction_" + dateStamp);
    String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
    String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
    String  outputTypes = U.getOption(pArgs, "--outputTypes=" ,"SectionZone:ContentHeading" );
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    // String localTerminologyFiles = U.getOption(pArgs, "--localTerminologyFiles=", SyntaticPipeline.SyntaticMinimalTerminologyFiles ); 
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
    
    
 // ---------------------------
    // input and output format options
    // ---------------------------
    String inputFormat = U.getOption(pArgs, "--inputFormat=",  FrameworkBaselineApplication.TEXT_READER_  );
   
   
   String outputFormat = U.getOption(pArgs,"--outputFormat=", FrameworkBaselineApplication.SECTION_NAME_FREQ_WRITER_  ); // + ":" + 
                                                       //       FrameworkBaselineApplication._WRITER_  );//  + ":" +
                                                          //    FrameworkBaselineApplication.XMI_WRITER_ );
   
   
                                                            //  FrameworkBaselineApplication.XMI_WRITER_           + ":" +
                                                            //  FrameworkBaselineApplication.VTT_WRITER_           + ":" +
                                                            //  FrameworkBaselineApplication.STATS_WRITER_         + ":" +
                                                            //  FrameworkBaselineApplication.BIOC_WRITER_          + ":" +
                                                            //  FrameworkBaselineApplication.SIMPLE_FORMAT_WRITER_  );
        
   System.err.println("The outputFormat = " + outputFormat);
        
    
   // ---------------------------
   // Performance configuration Parameters
   // ---------------------------
   int                      metric = Integer.parseInt(U.getOption(pArgs, "--metric=",                      "50"));
   int             numberToProcess = Integer.parseInt(U.getOption(pArgs, "--numberToProcess=",            "100"));
   int        numberOfApplications = Integer.parseInt(U.getOption(pArgs,  "--numberOfApplications=",        "2"));
   int initialNumberOfApplications = Integer.parseInt(U.getOption(pArgs,  "--initialNumberOfApplications=", "1"));
   int     maxNumberOfApplications = Integer.parseInt(U.getOption(pArgs,  "--maxNumberOfApplications=",     "2"));
   int               maxSystemLoad = Integer.parseInt(U.getOption(pArgs,  "--maxSystemLoad=",              "80"));
   int                   recycleAt = Integer.parseInt(U.getOption(pArgs,  "--recycleAt=",               "1000000"));
   int                     seconds = Integer.parseInt(U.getOption(pArgs,  "--seconds=",                     "30"));
  
    String args[] = {
        
        "--inputDir=" + inputDir,
        "--inputFormat=" + inputFormat,
        "--outputDir=" + outputDir,
        "--outputFormat=" + outputFormat,
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--outputTypes=" + outputTypes,
   //     "--localTerminologyFiles=" + localTerminologyFiles,
        "--setMetaDataHeader=" + setMetaDataHeader,
        
        // -----------------------------------
        // Pipeline scale-out parameter
          "--numberOfApplications="        + numberOfApplications,
          "--metric="                      + metric,
          "--numberToProcess="             + numberToProcess,
          "--initialNumberOfApplications=" + initialNumberOfApplications,
          "--maxNumberOfApplications="     + maxNumberOfApplications,
          "--maxSystemLoad="               + maxSystemLoad,
          "--recycleAt="                   + recycleAt,
          "--seconds="                     + seconds,
        
       
    };

    // need a help option here 
    // This method assumes that there is a resources/BodyFunctionApplication.txt
    Use.usageAndExitIfHelp( "BodyFunctionScaleOutApplication", pArgs, args ) ;
     
    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

} // End FormatVariabilityApplication Class -------------------------------
