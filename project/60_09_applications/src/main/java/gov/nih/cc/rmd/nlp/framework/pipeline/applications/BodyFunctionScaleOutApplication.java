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
