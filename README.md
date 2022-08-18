# The Body Function Project
The Body Function project seeks to identify information about certain types of body function, as defined by the WHO's International Classification of Functioning, Disability and Health (ICF). A component of this project is a Named Entity Recognition (NER) application that extracts Strength, Range of Motion (ROM), and Reflexes from clinical text.

# Introduction
This work was motivated by interest from the Social Security Administration in identifying body function information from clinical text for the purpose of disability adjudication. In particular, they were interested in the type of body function that is documented as part of physical exams. While there are many types of body function, we chose to focus on mentions of strength, range of motion (ROM), and reflexes. 

The application highlights body function mentions in clinical text. A fully formed body function mention includes:

- The type of body function – strength, ROM, reflex
- The body location, including laterality, of the body function type (optional)
- A *qualifier* of the body function type  

The application classifies the *qualifier* as part of finding the mention.

# Useful Distribution Contents
-  **[./bin/BodyFunctionApplication-jar-with-dependencies.jar](https://github.com/CC-RMD-EpiBio/bodyFunction/raw/main/bin/BodyFunctionApplication-jar-with-dependencies.jar)**  This is the executable application jar 
-  ./project                                                Where the source code and dependencies for Body Function is
-  [Publications and Presentations](https://github.com/CC-RMD-EpiBio/bodyFunction#publications-and-presentations)

# Example Application Invocation
  > java -jar BodyFunctionApplication-jar-with-dependencies.jar --inputDir=..\project\60_01_data\examples --outputDir=.\bf_example_outputDir

# Application Usage
The application is run from the command line: 
 
    > java -jar BodyFunctionApplication-jar-with-dependencies.jar [Options]

This is an all-encompassing java application jar.  

# Application Parameters/Options
The following command line parameters are necessary to run the application:

- `--inputDir=` This is the path to the text files for the application to process. The application will recurse through sub-directories to pick up files.
- `--outputDir=` If not filled out, defaults to the value of $inputDir_[dateStamp]

The following parameters are optional:

- `--version`  When this option is used, the application will print the version.  No other processing happens.
- `--help`     When this option is used, the application will print the usage page.  No other processing happens.
- `--outputTypes=` Specifies the kinds of output annotations.  See [Annotation Labels](#Annotation Labels) below for more details.
- `outputFormat=` Specifies the kind of output this application prints.  See the [Output Formats](#Output Formats) below for more details.
- `inputFormat=`  Specifies the kind of files to work on. The default is ascii text files, but the input can also be UIMA xmi or GATE files. See [Input Formats](#input formats) below for more details. 

# Annotation Labels
The application will create annotations for the following annotation types. These annotation types output are specified in a colon delimited list to the parameter --outputTypes=.  By default, all of the below annotation types are specified. 
   

  - **BodyFunctionMention** - these are mentions that include a body function type, one or more qualifiers, and an optional body location.  
  - **Strength**      - these are mentions of body function type strength
  - **RangeOfMotion** - these are mentions of body function type range of motion
  - **Reflex**        - these are mentions of body function type reflex
  - **BodyLocation**  - these are indications of body part and/or laterality associated with the body function.
  - **BFQualifier**   - this is a numeric or enumerated value indicating at or above normal functioning values (+1); below normal functioning values(-1), or if the measurement is unclear or ambiguous (0)
   
# Output Formats
This application returns annotated files in the following formats. These formats can be turned on by specifying them in a colon delimited list to the --outputFormat= parameter. This is the default output format.

 - *XMI_WRITER* - This is the standard Apache UIMA format. Apache UIMA 2.10 code was used. To interpret this format, the type descriptors will be needed. 
  >  The main type descriptor can be found 
                           at 60_03_type_descriptors/src/main/resources/gov/nih/cc/rmd/framework/bodyFunction/BodyFunctionModel.xml 
                           See https://uima.apache.org for documentation to use UIMA.   
                           
 - *GATE_WRITER*        - This is the GATE Developer xml document format.  See https://gate.ac.uk for documentation to use GATE.  GATE 9 codebase was used for this functionality. 
 - GATE_CORPUS_WRITER - This is the GATE Developer serial-datastore format.  All files read in will be put into a corpus named $CORPUS_NAME in this this format. 
 - *VTT_WRITER*         - This is a lightweight text format used by the VTT application. VTT (Visual Tagging Tool) is a simple, lightweight portable Java based annotation tool, created and distributed by the National Library of Medicine.  See https://lexsrv3.nlm.nih.gov/Specialist/Summary/vtt.html for more information about VTT. 
 - *TEXT_WRITER* - This is the text that was processed.  This format is sometimes useful to view as an output when the input came from a non text format, such as UIMA's xmi, GATE's xml, input formats [or drawn from embedded database queries - the current version of framework-legacy does not have this capability] 
 - *BIO_WRITER*         - This is the Begin, Inside, Outside format to use with the Stanford Core NLP toolkit.
 - *CSV_WRITER*         - This is output in pipe delimited formatted text files.
 - *SNIPPET_WRITER*     - [Not really applicable for this application] This is a VTT format, where each $focus annotation has been segmented into its own "snippet", with 3 lines before and 3 lines after surrounding it.  Each $focus mention is labeled with a true|false annotation.

 
# Input Formats
This application reads in data from the following formats.  The input format is specified by the --inputFormat= parameter.   

- *TEXT_READER*        - This is the default.  This reader assumes input to be UTF-8, ASCII-7 formatted files.  It is possible to feed in Windows page-code formatted files, but there is no translation done to UTF-8, and any characters that are out of range will be passed along as-is.  Text tokens that include non-ascii range characters will fail to match dictionary based lookups.
 - *XMI_READER*         - This reader will read in Apache-UIMA formatted XMI files. 
 - *GATE_READER*        - This reader will read in GATE formatted xml files.
 - *GATE_CORPUS_READER* - This reader will read in files within a GATE serial datastore.  

# Example UIMA XMI Output

Here is a screen shot of the UIMA Annotation Viewer viewing a file processed by the application.
  
  <img src="https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/project/60_10_doc/BodyFunctionUIMAXMI_Output.png" width=1000 />

# Example of GATE Output

Here is a screen shot of the GATE Viewer viewing a file processed by the application.

<img src="https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/project/60_10_doc/BodyFunctionGATE_ExampleOutput.png" width=1000 />

# Example of VTT Output

Here is a screen shot of the VTT Viewer viewing a file processed by the application.

<img src="https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/project/60_10_doc/BodyFunctionVTT_Example.png" width=1000 />


# Example Text Data
Two exemplar files are distributed that include body function mentions in them. 
  
-  [BodyFunctionExamle00.txt](https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/project/60_01_data/examples/BodyFunctionExample001.txt)
-  [bf_ssa_examples01.txt](https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/project/60_01_data/examples/bf_ssa_examples_01.txt)

# Publications and Presentations

|Title     | Citation | Description |
| -------- | -------- | ------------| 
|**Extracting Body Function from Clinical Text** |*G Divita, J Lo, C Zhou, K Coale, E Rasch*. **Extracting Body Function from Clinical Text**  Proceedings of the Second Workshop on Artificial Intelligence for Function, Disability, and Health (AI4Function 2021), Online, August 20-21, 2021. Edited by: Denis Newman-Griffis, Bart Desmet, Ayah Zirikly, Suzanne Tamang, Hongfang Liu Submitted by: Denis Newman-Griffis Published on CEUR-WS: 15-Aug-2021ONLINE: [Vol-2926 paper 3](http://ceur-ws.org/Vol-2926/paper3.pdf) |This paper describes finding Body Function (BF) mentions within clinical text. Body Function is noted in clinical documents to provide information on potential pathologies within underlying body systems or structures. BF mentions are embedded in highly formatted structures where the formats include implied scoping boundaries that confound existing NLP segmentation and document decomposition techniques. We have created two extraction systems: a dictionary lookup rule-based version, and a conditional random field (CRF)|
|**Extracting Body Function from Clinical Text** |[Powerpoint Presentation to SSA Aug 26, 2021](/project/60_10_doc/ExtractingBodyFunctionFromClinicalText_SSAPresentation.pptx)|Presentation of this work to SSA.

# License
All source code and documentation contained in this package are distributed under the terms in the [LICENSE](LICENSE) file (modified BSD). 

# Acknowlegements
See the [acknowledgments](acknowldgements.md) file for what software we use and attribute. 


