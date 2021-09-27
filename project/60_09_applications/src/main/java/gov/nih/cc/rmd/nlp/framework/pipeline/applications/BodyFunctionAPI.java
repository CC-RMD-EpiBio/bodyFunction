// ------------------------------------------------------------
/**
 * An api annotating BodyFunction elements in documents
 * The input is a string that is at the document level
 * The output is pipe delimited string that is in VTT
 * style  
 * 
 * @author  G.o.d.
 * @created 2020.05.01
 * 
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject.FrameworkObject;
import gov.nih.cc.rmd.nlp.framework.pipeline.BodyFunctionPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;


// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class BodyFunctionAPI extends ApplicationAPI {

  
//=======================================================
 /**
  * Constructor TemplateAPI 
  *
  */
  // ====================================================
 public BodyFunctionAPI() throws Exception {
 
   String dummyArgs[] = null;
  
   init( dummyArgs);
   
  } // end Constructor() ----------------------------

 //=======================================================
 /**
  * Constructor TemplateAPI 
  * 
  * @param pArgs 
  *
  */
 // =====================================================
     public BodyFunctionAPI( String[] pArgs) throws Exception {
       
       init( pArgs);
       
      } // end Constructor() ----------------------------

 
  // =======================================================
  /**
   * init 
   *
   * @param pArgs
   */
  // =======================================================
  public void init(String[] pArgs) throws Exception {

    
    try {
      // --------------------
      // Read in any command line arguments, and add to them needed ones
      // The precedence is command line args come first, followed by those
      // set by the setArgs method, followed by the defaults set here.
      // --------------------

        
      String args[]  = BodyFunctionApplication.setArgs(pArgs );
      
      GLog.setLogDir( U.getOption(args,  "--logDir=", "./logs" ));
      
      // -------------------
      // Create an engine with a pipeline, attach it to the application
      // -------------------
      BodyFunctionPipeline BodyFunctionPipeline = new BodyFunctionPipeline(args );
    
      AnalysisEngine ae = BodyFunctionPipeline.getAnalysisEngine( );
      super.initializeApplication( pArgs, ae);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing Line " + e.toString();
      System.err.println( msg);
      throw new Exception( msg);
      
    }
      
  } // end Method initialize() -----------------------------
  
  
  
  // ------------------------------------------
  /**
   * main
   *    See the setArgs method to see what specific command line 
   *    arguments should be passed in here.
   *
   * @param pArgs
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {
    

    try {
    
      String[] args = BodyFunctionApplication.setArgs( pArgs);
      BodyFunctionAPI anApplication = new BodyFunctionAPI( args);

    
      // ------------------
      // gather and process the cas's
      // -----------------
   

   
     
      
      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ));
      String row = null;
      System.err.print("input : ");
      while ( (row = in.readLine() )!= null && row.trim().length() > 0  ) {
         FrameworkObject results = anApplication.processToFrameworkObject(  row + "\n" );  // <---------------- all the work is done here
         
         
        System.out.println();
        System.out.println(results.getWordTokens().toString() );
        System.err.println("--------------------");
        System.err.print("input : ");
      }
    
      System.err.println("Finished");
    
    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }
    
  } // end Method main() --------------------------------------
    
 

  // End TemplateAPI Class -------------------------------
}
