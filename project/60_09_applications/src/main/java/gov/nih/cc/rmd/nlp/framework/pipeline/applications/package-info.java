

/**
 * Body Function Applications Package
 * <p>
 * The following classes are available that wrap up Body Function functionalities
 * 
 * <ul>
 * <li><b> BodyFucntionApplication </b> A stand-alone application that reads in files from an input directory and writes out processed files to an output directory </li>
 * <li><b> BodyFunctionAPI         </b> A class that has an api function to call to embed body function annotations into other code.  This class has an example main within it. </li>
 * <li><b> BodyFunctionScaleOutApplication</b> A stand-alone application, similar to the BodyFunctionApplication, but has more options that can be set to multi-thread to run the application faster </li>
 * <li><b> BodyFunctionAddNewlinesApplication</b> A stand-alone application to guess where newlines should go based on SOAP note like section headings.  This application was needed from notes extracted from an EMR where the sql report did not carry along newlines </li>  
 * <li><b> GATE_2_BIO_Application </b> A stand alone program to convert the text along with body function annotations into BIO format.  This was needed to convert our manual annotations into BIO format to feed into Standford's CRF software, which takes BIO formatted files </li>
 * 
 * </p>
 *
 * @since 2022-09-0
 * @author Guy Divita
 * @version 2022.09.0
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;
