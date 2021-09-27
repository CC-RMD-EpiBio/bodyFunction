// =================================================
/**
 * PostEvalOrganizeStep takes the eval_main, and
 * merges it with the csv file that has the name of 
 * the batches, who annotated it, and where the batch 
 * file is.
 * 
 * 
 *
 * @author     Guy Divita
 * @created    Sep 23, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author divitag2
 *
 */
public class PostEvalOrganizeStep {

  // =================================================
  /**
   * main 
   * 
   * @param pArgs
  */
  // =================================================
   public static void main(String[] pArgs) {
   
     try {
       
       String[] args = setArgs( pArgs);
       String      inputFile = U.getOption(args,  "--inputFile=",      "./eval_main.txt");
       String inputStatsFile = U.getOption(args, "--inputStatsFile=",  "./bf_stats.csv" );
       String     outputFile = U.getOption(args, "--outputFile=",      "./eval_merged.txt");
     
       
       String[] evalFileRows = U.readFileIntoStringArray(inputFile);
       
       String   summaryStats = getSummaryStats( evalFileRows);
       String[] fns          = getPs( evalFileRows, "FN:");
       String[] fps          = getPs( evalFileRows, "FP:");
       String[] tps          = getPs( evalFileRows, "TP:");
       
       
       String[] stats_rows  = U.readFileIntoStringArray( inputStatsFile );
       HashMap<String,String> pageIdIndex = indexPageIds( stats_rows);
       
       String[] fnsUpdated = mergeInfo( fns, pageIdIndex );
       String[] fpsUpdated = mergeInfo( fps, pageIdIndex );
       String[] tpsUpdated = mergeInfo( tps, pageIdIndex );
       
       PrintWriter out = new PrintWriter( outputFile);
       
       out.print( summaryStats);
       
       out.print("\n\n");
       
       out.print("#Type|batch|Annotator|FileName|BeginOffset|EndOfset|Left Context|Mention|Right Context|Section|AssertionStatus|FullPath\n");;
       out.print("#----------------------------------\n"
               + "#False Negatives                   \n"
               + "#----------------------------------\n" );
       for ( String row: fnsUpdated) {
         out.print( row );
         
       }
       
       out.print("\n\n");
       out.print("#Type|batch|Annotator|FileName|BeginOffset|EndOfset|Left Context|Mention|Right Context|Section|AssertionStatus|FullPath\n");;
       
       out.print("#----------------------------------\n"
               + "#False Positives                   \n"
               + "#----------------------------------\n" );
       for ( String row: fpsUpdated) {
         out.print( row );
        
       }
       
       out.print("\n\n");
       out.print("#Type|batch|Annotator|FileName|BeginOffset|EndOfset|Left Context|Mention|Right Context|Section|AssertionStatus|FullPath\n");;
       
       out.print("#----------------------------------\n"
               + "#True Positives                    \n"
               + "#----------------------------------\n" );
       
       for ( String row: tpsUpdated) {
         out.print( row );
      
       }
       
       out.close();
       
       
       
       
     } catch (Exception e) {
       e.printStackTrace();
       GLog.error_println("Issue with this " + e.toString());
     }

  }
   
   
 // =================================================
  /**
   * getSummaryStats 
   * 
   * @param evalFileRows
   * @return String
  */
  // =================================================
   private static String getSummaryStats(String[] evalFileRows) {
     
     StringBuffer buff = new StringBuffer();
     for ( int i = 0; i < 30; i++ )
       buff.append( "## " + evalFileRows [i]+ "\n");
       
          
     return buff.toString();
       
       
  } // end Method getSummaryStats() ------------------


  // =================================================
  /**
   * getPs 
   * 
   * @param pRows
   * @param pStartPattern
   * @return String[] 
  */
  // =================================================
  private static String[] getPs(String[] pRows, String pStartPattern) {
   String returnVal[] = null;
   
   List<String> newRows = new ArrayList<String>( );
   
   for ( String row : pRows ) 
     if ( row.trim().startsWith( pStartPattern ))
       newRows.add( row );
   
   
   returnVal = newRows.toArray(new String[ newRows.size()]);
   
   
    return returnVal;
  } // end Method getPs() ---------------------------


  // =================================================
  /**
   * indexPageIds 
   *   #PageName|Batch Name|Annotator|Num Annotations|Duplicate|FullPathOfXML_file|test:train:original
   *   
   * 
   * @param pStatsRows
   * @return HashMap<String, String>
  */
  // =================================================
   private static HashMap<String, String> indexPageIds(String[] pStatsRows) {
    
     HashMap<String, String> returnVal = new HashMap<String, String>();
     
     for ( String row : pStatsRows ) {
       String[] cols = U.split(row );
       String key = cols[0];
       returnVal.put( key, row);
     }
     
     return returnVal;
  } // end Method indexPageIds() ---------------------


  // =================================================
  /**
   * mergeInfo 
   * 
   * @param pPs
   * @param pPageIdIndexHash
   * @return String[] 
  */
  // =================================================
  private static String[] mergeInfo(String[] pPs, HashMap<String, String> pPageIdIndexHash) {
    
    List<String> buff = new ArrayList<String>( pPs.length);
    for ( String row : pPs )
      buff.add( mergeInfo ( row, pPageIdIndexHash ) + "\n");
    
    return buff.toArray( new String[ buff.size()]);
  } // end Method mergeInfo() --------------


// =================================================
  /**
   * mergeInfo 
   *   0      1         2          3            4      5     6            7                8     9
   *  #type|fileName|leftContext|mention|rightContext|Empty|Section|AssertionStatus|beginOffset|endOffset
   * 
   *  index 
   *    0           1          2           3            4           5               6
   *  #PageName|Batch Name|Annotator|Num Annotations|Duplicate|FullPathOfXML_file|test:train:original
   *  
   *  Merged =
   *  
   *  #type|batch|annotator|pageName|beginOffset|endOffset|leftContext|mention|rightContext|Section|status|fullPath
   *  
   * @param pRow
   * @param pPageIdIndexHash
   * @return
  */
  // =================================================
  private static String mergeInfo(String pRow, HashMap<String, String> pPageIdIndexHash) {
   
    String[] cols = U.split(pRow);
    String aType = cols[0];
    String key = cols[1];
    int x = key.lastIndexOf(".xml");
    if ( x > 0)
      key = key.substring(0, x);
    
    key = key.trim();
    
   String leftContext = cols[2];
    String mention = cols[3];
    String rightContext = cols[4];
    String empty = cols[5];
    String section = cols[6];
    String assertionStatus = cols[7];
    
    String beginOffset = cols[8];
    String endOffset = cols[9];
    if ( cols.length == 11 ) {
       beginOffset = cols[9];
       endOffset = cols[10];
     
    }
    String infoz = pPageIdIndexHash.get( key );
    String infoCols[] = { "PageName","Batch","Annotator","#Annotations","Duplicate","Path","testingTraining"};
    if ( infoz != null ) 
     infoCols = U.split(infoz);
    
    String pageName = infoCols[0];
    String batch = infoCols[1];
    String annotator = infoCols[2];
    String numAnnotations = infoCols[3];
    String duplicate = infoCols[4];
    String aPath = infoCols[5];
    String testingTraining = infoCols[6];
    
    StringBuffer buff = new StringBuffer();
    buff.append(aType);
    buff.append("|");
    buff.append(U.spacePadRight(31, batch));
    buff.append("|");
    buff.append( U.spacePadLeft(3, annotator));
    buff.append("|");
    buff.append( U.spacePadRight(19,pageName));
    buff.append("|");
    buff.append(U.spacePadRight(5, beginOffset));
    buff.append("|");
    buff.append(U.spacePadRight(5, endOffset));
    buff.append("|");
    buff.append(leftContext);
    buff.append("|");
    buff.append(mention);
    buff.append("|");
    buff.append(rightContext);
    buff.append("|");
    buff.append(U.spacePadRight(30, section));
    buff.append("|");
    buff.append(U.spacePadLeft(10,assertionStatus));
    buff.append("|");
    buff.append(aPath);
    
    return buff.toString();
     
    
    
  } // end Method mergeInfo() -------------


//------------------------------------------
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
     String dateStamp = "v2021-09-09.06";

     // -------------------------------------
     // Input and Output

     String    inputDir       = U.getOption(pArgs, "--inputDir=", "/some/dir/eval/");
     String    inputStatsFile = U.getOption(pArgs, "--inputStatsFile=", inputDir + "/bf_stats.csv" );
     String    outputFile     = U.getOption(pArgs, "--outputFile=", inputDir + "/eval_merged.txt");
   
   
     String inputFile = U.getOption(pArgs,  "--inputFile=", inputDir + "/eval_main.txt");
     String      version = "2021-09-23.01";
     
     String args[] = {
         
         "--inputDir=" + inputDir,
         "--inputFile=" + inputFile,
         "--inputStatsFile=" + inputStatsFile,
         "--inputDir=" + inputDir,
         "--outputFile=" + outputFile,
         "--version=" + version
        
     };

      // need a help option here 
     // This method assumes that there is a resources/BodyFunctionApplication.txt
     if ( Use.version(pArgs, args ) || Use.usageAndExitIfHelp( "PostEvalOrganizeStep", pArgs, args ) )
         Runtime.getRuntime().exit(0);
                           

     return args;

   }  // End Method setArgs() -----------------------


   // ------------------------------------------------
   // Global Variables
   // ------------------------------------------------

} // end class PostEvalOrganizeStep() --------------
