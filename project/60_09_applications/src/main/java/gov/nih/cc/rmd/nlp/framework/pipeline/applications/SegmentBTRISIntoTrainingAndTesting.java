// =================================================
/**
 * SegmentBTRISIntoTrainingAndTesting
 * 
 * segments the files in the --inputDir= into a --testingDir  and a --trainingDir=
 * 
 * based on if the files are in the list by --testingFiles=
 *
 * @author     Guy Divita
 * @created    Aug 9, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.bodyFunction.BodyFunctionAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.csv.CSVWriter;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author divitag2
 *
 */
public class SegmentBTRISIntoTrainingAndTesting {

  // =================================================
  /**
   * main 
   * 
   * @param pArgs
  */
  // =================================================
public static void main(String[] pArgs) {
   
  try {
    String args[] = setArgs( pArgs);
    String    inputDir  = U.getOption(pArgs, "--inputDir=", "./combinedDir");
    String    testingDir = U.getOption(pArgs, "--testingDir=", "./testingDir");
    String   trainingDir = U.getOption(pArgs, "--trainingDir=", "./trainingDir");
    String   testFilesName = U.getOption(pArgs,  "--testFilesName=", "./testFiles" ); 
    
    
    String[] fileNames = U.readFileIntoStringArray(testFilesName);
    
    HashSet<String> fileNameHash = new HashSet<String>( );
    
    // these are going to be devoid of file extensions
    for ( String fileName : fileNames ) 
      fileNameHash.add(  fileName );
    
    // read in the inputDir files
    File aDir = new File ( inputDir);
    
    File[] someFiles = aDir.listFiles();
    
    for ( File aFile : someFiles ) {
      
      String buff1 = aFile.getName();
      int  ptr = buff1.indexOf('.');
      String justName = buff1.substring(0, ptr);
      
      if ( fileNameHash.contains(justName )) 
        copyFileTo( aFile, testingDir );
       else 
        copyFileTo( aFile, trainingDir);
      
    }
    
   
  } catch (Exception e) {
    e.printStackTrace();
  }

  } // end Method main() ----------------------------


// =================================================
  /**
   * copyFileTo copies file to this dir
   * 
   * @param aFile
   * @param aDir
   * @throws IOException 
  */
  // =================================================
  private static void copyFileTo(File aFile, String pDir) throws IOException {
   
    String aFileName = aFile.getName();
    String destFileName = pDir + "/" + aFileName;
    
    Path src = Paths.get(aFile.getAbsolutePath() );
    Path dest = Paths.get(destFileName);
    Files.copy( src,  dest);
    
    
    
    
  } // end Method copyFileTo() --------------


// ------------------------------------------
/**
 * setArgs
 * 
 * 
 * @return
 */
// ------------------------------------------
public static String[] setArgs(String pArgs[]) {



  String    inputDir  = U.getOption(pArgs, "--inputDir=", "./combinedDir");
  String    testingDir = U.getOption(pArgs, "--testingDir=", "./testingDir");
  String   trainingDir = U.getOption(pArgs, "--trainingDir=", "./trainingDir");
  String   testFilesName = U.getOption(pArgs,  "--testFilesName=", "./testFiles" ); 
  
  
  

  String args[] = {
      
      "--inputDir=" + inputDir,
      "--testingDir=" + testingDir,
      "--trainingDir=" + trainingDir,
      "--testFilesName=" + testFilesName
     
  };

   // need a help option here 
  // This method assumes that there is a resources/BodyFunctionApplication.txt
  Use.usageAndExitIfHelp( "SegmentBTRISIntoTrainingAndTesting", pArgs, args ) ;
                          

  return args;

}  // End Method setArgs() -----------------------


// ------------------------------------------------
// Global Variables
// ------------------------------------------------
}
