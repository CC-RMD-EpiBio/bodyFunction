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
 * BodyFunctionApplication kicks off the BodyFunction pipeline on a corpus
 * This application runs from annotated gate files and creates vtt and xmi output files.
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 * 
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.csv.CSVWriter;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionAddNewlinesPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionEvaluationPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

import org.apache.uima.analysis_engine.AnalysisEngine;


public class BodyFunctionAddNewlinesApplication {

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
      BodyFunctionAddNewlinesPipeline   pipeline = new BodyFunctionAddNewlinesPipeline( args );
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
     
      application.addWriter(FrameworkBaselineApplication.XMI_WRITER, args);
      application.addWriter(FrameworkBaselineApplication.GATE_WRITER, args);
     
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
    String  outputTypes = U.getOption(pArgs, "--outputTypes=" ,"BodyLocation:BodyFunction:Strength:RangeOfMotion:Quantifier:LexicalElement:Gold:Copper:TruePositive:FalsePositive:FalseNegative:FalsePostitive:TP:TN:FP:FN" );
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
   
  
    String gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
    String addNewLines = U.getOption(pArgs,  "--addNewLines=", "false" );  
    
   
   
  
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
        "--addNewLines=" + addNewLines
      
      
       
    };

     // need a help option here 
    // This method assumes that there is a resources/BodyFunctionApplication.txt
    Use.usageAndExitIfHelp( "BodyFunctionEvaluationApplication", pArgs, args ) ;
                            

    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

} // End SyntaticApplication Class -------------------------------
