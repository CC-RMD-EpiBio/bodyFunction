# The Body Function Project
The Body Function project is concerned wit body functions. A tangible component to this project is a Named Entity Recognition (NER) Application that extracts Strength, Range of Motion and Reflexes from clinical text.

# Introduction
Body functions are mentioned in clinical text when there is concern for, or documentation of, pathologies around body function or body function assessment. Body Function information is commonly collected during
physical exams to provide information on potential pathologies within underlying body systems or
structures. While there are many specific kinds of body function, this tool finds
mentions of strength, range of motion (ROM), and reflexes. 

This work was motivated by desire to aid the Social Security Administration in helping them identify body function with clinical text for the purpose of disability adjudication.  

Our organization adheres to the perspective/model/world view described by the WHO’s International Classification of Function and Disability that is there is utility in noting Body Function *as it relates to an individual’s activity and participation*. 

The application highlights body function mentions.  A fully formed body function mention includes
 
A fully formed mention includes: 

- Some kind or type of body function – strength, rom, reflex
- (Optionally) some indication of where.  I.e.,  body anatomy or location (including laterality), 
- A *qualifier* of that body function observation.  

The application classifies the *qualifier* as part of finding that mention.

# Useful Distribution Contents
-  **./bin/BodyFunctionApplication-jar-with-dependencies.jar**  This is the executable application jar 
-  ./project                                                Where the source code and dependencies for Body Function is

# Application Invocation

 Invoke/call/execute/kick off the application by running the command on a (windows/linux/mac) command line
 
    > java -jar BodyFunctionApplication-jar-with-dependencies.jar [Options]

 This is an all-encompassing java application jar.  

# Application Parameters/Options
  The following (command line) parameters are necessary to successfully run the application:


- `--inputDir=` This is the path/location of where text files are to be processed. The application will recurse through sub-directories to pick up files.
- `--outputDir=` if not filled out, defaults to the value of $inputDir_[dateStamp]

   The following parameters are optional:

- `--version`  When this option is used, the application will spew out the version.  No other processing happens.
- `--help`     when this option is used, the application will spew out the usage page.  No other processing happens.
- `--outputTypes=` Specifies the kinds of output annotations.  See [Annotation Labels](#Annotation Labels) below for more detail.
- `outputFormat=` Specifies what kind of output this application spews out.  See the [Output Formats](#Output Formats) below for ore details.
- `inputFormat=`  Specifies the kinds of files to work on.  By default ascii text files.  But the input can also be UIMA xmi files or GATE files.  see [Input Formats](#input formats) below for more details. 

# Annotation Labels
 The application will create annotations for the following annotation types.   These annotation types output are specified in a colon delimited list to the parameter --outputTypes=.  By default, all of the below annotation types are specified. 
   

  - **BodyFunctionMention** - these are mentions that include a body function type, one or more qualifiers, and an optional body location.  
  - **Strength**      - these are mentions of body strength
  - **RangeOfMotion** - these are mentions of body range of motion
  - **Reflex**        - these are mentions of body function reflex
  - **BodyLocation**  - these are indications of laterality or body part associated with the body function.
  - **BFQualifier**   - this is numeric or enumerated value indicating at or above normal functioning values (+1); below normal functioning values(-1), or the mention is ambiguous(0)
   
# Output Formats
   This program can return annotated files in the following formats.  These formats can be turned on by specifying them in a colon delimited list to the `--outputFormat=` parameter. This is the default output format.

 - *XMI_WRITER* - This is the standard Apache UIMA format. Apache UIMA 2.10 code was used. To interpret this format, the type descriptors will be needed. 
  >  The main type descriptor can be found 
                           at 60_03_type_descriptors/src/main/resources/gov/nih/cc/rmd/framework/bodyFunction/BodyFunctionModel.xml 
                           See https://uima.apache.org for documentation to use UIMA.   
                           
 - *GATE_WRITER*        - This is the GATE-NLP xml document format.  See https://gate.ac.uk for documentation to use GATE.  GATE 9 codebase was used for this functionality. 
 - GATE_CORPUS_WRITER - This is the GATE-NLP serial-datastore format.  All files read in will be put into a corpus named $CORPUS_NAME in this this format. 
 - *VTT_WRITER*         - This is a lightweight text format used by the VTT application. VTT (Visual Tagging Tool) is a simple, lightweight portable Java based annotation tool, created and distributed by the National Library of Medicine.  See https://lexsrv3.nlm.nih.gov/Specialist/Summary/vtt.html for more information about VTT. 
 - *TEXT_WRITER* - This is the text that was processed.  This format is sometimes useful to view as an output, when the input came from a non text format, such as UIMA's xmi, GATE's xml, input formats [or drawn from embedded database queries - the current version of framework-legacy does not have this capability] 
 - *BIO_WRITER*         - This is the Begin, Inside, Outside format useful to use with the Stanford Core NLP toolkit.
 - *CSV_WRITER*         - This is output in pipe delimited formatted text files.
 - *SNIPPET_WRITER*     - This is a VTT format, where each $focus annotation has been segmented into it's own "snippet", with 3 lines before and 3 lines after surrounding it.  Each $focus mention is labeled with a true|false annotation.


 
# Input Formats
   This program can read in data from the following formats.  The input format is specified by the --inputFormat= parameter.   

 - *TEXT_READER*        - This is the default.  This reader assumes input assumed to be UTF-8, ASCII-7 formatted files.  It is possible to feed in Windows page-code formatted files, but there is no translation is done to UTF-8, and any characters that are out of range will be passed along as-is.  Text tokens that include non-ascii range characters will fail to match dictionary based lookups.
 - *XMI_READER*         - This reader will read in Apache-UIMA formatted XMI files. 
 - *GATE_READER*        - This reader will read in GATE formatted xml files.
 - *GATE_CORPUS_READER* - This reader will read in files within a GATE serial datastore.  

#Example UIMA XMI Output

  Here is a screen shot of the UIMA Annotation Viewer viewing a file processed by the application.  
  <img src="https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/60_10_doc/BodyFunctionUIMAXMI_Output.png" width=900 />

