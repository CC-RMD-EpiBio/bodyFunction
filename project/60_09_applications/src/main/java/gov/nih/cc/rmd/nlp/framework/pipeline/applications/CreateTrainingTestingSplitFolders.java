// =================================================
/**
 * CreateTrainingTestingSpitFolders.java  creates the testing/training folders
 * for body function based on ....  
 *    random ?
 *    proportionally by annotation type ?
 *    proportionally by number of annotations?
 *    
 *
 * @author     Guy Divita
 * @created    Sep 3, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;



import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author divitag2
 *
 */
public class CreateTrainingTestingSplitFolders {

  // =================================================
  /**
   * main
   * 
   * @param args
  */
  // =================================================
  public static void main(String[] pArgs) {
   
    try {
      
      String inputFile = U.getOption(pArgs, "--inputFile=", "./bf_stats.txt");
      String outputDir = U.getOption(pArgs, "--outputDir=", "./softwareIteration_1");
      
      
      
      // read in the csv file with all the file names in it
      String[] rows = U.readFileIntoStringArray( inputFile );
      
      
      // for each row, copy the file to the train or test dir
      for ( String row : rows ) {
        processRow( row, outputDir);
      }
      
      
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  // =================================================
  /**
   * processRow 
   * 
   * @param pRow
   * @param pOutputDir
   * @throws Exception 
  */
  // =================================================
   private static void processRow(String pRow, String pOutputDir) throws Exception {
  
    
     if ( pRow!= null && pRow.trim().length() > 0 ) {
     System.err.println(pRow.trim());
     
     String row = pRow;
    
     if ( !row.startsWith("#")) {
     String cols[] = U.split(row.trim(), "|");
     
     if ( cols.length > 4 ) {
     String batch = cols[0];
     String file_name = cols[0];
     String annotator = cols[2];
     String path_ = cols[5];
     
     path_ = path_.replace("~", "," );
     path_ = path_.replace("\"", "");
     String split = cols[6];
     String train_outputDir = pOutputDir + "/train";
     String test_outputDir = pOutputDir + "/test";
     String orig_outputDir = pOutputDir + "/original";
     
     U.mkDir( train_outputDir);
     U.mkDir( test_outputDir);
     U.mkDir( orig_outputDir);
     
     
     String justName =  file_name.trim()  + ".xml";
   
     System.err.println("copying " + path_ + " to " + orig_outputDir + "/" + justName);
    copyFileFromTo( path_, orig_outputDir +"/" + justName );
    
     if ( split.contains("train"))
       copyFileFromTo( path_, train_outputDir +"/" + justName );
     else if ( split.contains("test"))
       copyFileFromTo( path_, test_outputDir +"/" + justName );
     
     
     
     } else {
       System.err.println( "what's wrong with this row?" + pRow);
     }
     }
     }
     
  } // end processRow

  // =================================================
  /**
   * copyFileFromTo
   * 
   * @param oldFileName
   * @param newFileName
  */
  // =================================================
  private static void copyFileFromTo(String oldFileName, String newFileName) {
    
    File copied = new File( newFileName  );
    File original = new File ( oldFileName );
    try {
      FileUtils.copyFile(original, copied);
    } catch (IOException e) {
      e.printStackTrace();
      
    }
    
  } // end Method copyFileFromTo() -----------------

} // end Class CreateTrainingTestingSpitFolders()----
