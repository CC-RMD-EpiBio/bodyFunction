# Project Directory Description

This project directory includes all the modules which make up the Body Function Project specifically.

You may notice that there is an ordering to the directories.  The numbers that preface each directory name enforces that logical ordering. 

Those directories that start with 00 are direct copies (a white lie) of modules from the framework-legacy code.  They are included here because we currently have no nexus hosting mechanism to stuff these modules into to reference as a dependency to be pulled by maven.  

(the white lie - I renamed the directory name of these modules to enforce the ordering for this project.  The directory names are slightly differently prefaced in framework-legacy, but the namespaces are not changed.)

# Directory Contents:

 | Directory Name     | Description |
 | ------------------ | ----------- |
 |00_0_parent         | This is framework-legacy's parent pom                                                    |
 |00_1_ThirdParty-jars| These are framework legacy dependencies that were not published in a public nexus server |
 |00_2_framework-jars | These are the framework legacy jars, that would have been built if it were cloned out of the framework-legacy repo.  These are the guts of the underlying framework that the projects are built upon. |
 |                    |             |
 | 60_01_data         | A directory filled with example data |
 | 60_01_libs         | This is where the executable jars are placed when made by maven (this will get made when building) |
 | 60_01_parent       | This is this project's parent pom.  It does also reference the framework-legacy's parent pom |
 | 60_01_resources    | This is where the lexica (dictionary) resources are for this project |
 | 60_03_type0descriptors | This is where the definitions for the UIMA and GATE based labels reside |
 | 60_05_annotators   | This is where the source code for the UIMA annotators for this project reside |
 | 60_08_pipelines    | This is where the source code for the UIMA pipeline definitions reside |
 | 60_09_applications | This is where the source code for the applications/mains for the project reside |
 | 60_10_doc          | Some useful documentation |

# General Comments About This Project

This project is built from java-nlp-framework, a suite of functionality, that, itself is built upon [UIMA](https://uima.apache.org/)  2.9.  Framework-legacy is an evolution of [V3-NLP Framework](https://pubmed.ncbi.nlm.nih.gov/27683667/); which was built specifically for use within the Veteran Administration's [VINCI environment](https://www.hsrd.research.va.gov/for_researchers/vinci/workspace.cfm) to process, at scale, VA clinical documents.  

Note:  java-framework relies upon [uimaFIT](https://uima.apache.org/uimafit.html), to define the pipelines in code rather than through the myriad of configuration files.  You will not find the UIMA configuration files in the above projects, except to retrofit and work with other UIMA based tools. 

This project is built using Maven and Java.  Maven reactors should kick off the cascade of sub-projects to be made.

Note: See [Java-NLP-Framework/readme.md](https://github.com/CC-RMD-EpiBio/java-nlp-framework#readme) for details.


## The Jars Compiled from Java-NLP-Frameowrk distributed within this Repo
The following jars are distributed here (we do not have a nexus service to host these directly).  As part of the build, these jars get locally installed into your *.m2* directory as part of the 00_2_framework-jars project called from the reactor pom. 

- 01-nlp-resources-2022.09.0.jar
- 03-nlp-type-descriptors-2022.09.0.jar
- 04.0-nlp-util-2022.09.0.jar
- 04.1-nlp-vUtil-2022.09.0.jar
- 05-nlp-annotators-2022.09.0.jar
- 06-nlp-marshallers-2022.09.0.jar
- 07.0-nlp-pUtils-2022.09.0.jar
- 08-nlp-pipelines-2022.09.0.jar 

## Third Party Software and Jars Also Not Distributed via a Nexus Service
This project, when built from this repo, relies on a few jars that cannot be found via a nexus service.  They are found within the 00_1_ThirdParty-Jars project and are built from the reactor pom. 

# Building
For reference sake, $BF_HOME equates to where this repo got cloned out then changed to the ./project directory.
  
Install the parent,parent pom first.  This is the pom in $BF_HOME/00_0_parent.  This parent pom is a copy of the Java-NLP-Framework's parent pom. All is built upon the Java-NLP-Framework, so referencing that project's parent pom is a must.
(Skip this if you've prior installed the Java-NLP-Framework project).
<pre> 
cd $BF_HOME
cd 00_0_parent
mvn install
cd ..
</pre> 
 
Once Java-NLP-Framework's parent pom has been installed, there is a Body Function parent pom to install.
<pre>
cd $BF_HOME
cd 60_01_parent
mvn install
cd ..
</pre> 

Once the  Body Function parent pom has been installed, go back and build the set of projects.  There is a reactor pom in the $BF_HOME directory that refers to each project to be built.

 
### Example Build from the command line:

> cd $BF_HOME
> mvn install

</pre>
When the process is complete, the output from the process should look something like this:
<pre>
[INFO]
[INFO] ----------< gov.nih.cc.rmd.framework:60_00-bodyFunction-Top >-----------
[INFO] Building 60_00-bodyFunction-Top 2022.09.0                        [10/10]
[INFO] --------------------------------[ pom ]---------------------------------
[INFO]
[INFO] --- maven-install-plugin:2.4:install (default-install) @ 60_00-bodyFunction-Top ---
[INFO] Installing [somePath]\bodyFunction\project\pom.xml to [somePath]\[M2_Repository]\gov\nih\cc\rmd\framework\60_00-bodyFunction-Top\2022.09.0\60_00-bodyFunction-Top-2022.09.0.pom
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for 60_00-bodyFunction-Top 2022.09.0:
[INFO]
[INFO] unmavenedJars ...................................... SUCCESS [  3.650 s]
[INFO] 02-thirdParty ...................................... SUCCESS [  0.025 s]
[INFO] 00_framework-jars .................................. SUCCESS [  0.932 s]
[INFO] gov.nih.cc.rmd.framework: 60_01-bodyFunction-parent  SUCCESS [  0.050 s]
[INFO] 60_01_bodyFunction_resources ....................... SUCCESS [  0.763 s]
[INFO] 60_03-bodyFunction-type-descriptors ................ SUCCESS [ 59.392 s]
[INFO] 60_05-bodyFunction-annotators ...................... SUCCESS [  6.332 s]
[INFO] 60_08-bodyFunction-pipelines ....................... SUCCESS [  7.337 s]
[INFO] 60_09-bodyFunction-Application ..................... SUCCESS [02:58 min]
[INFO] 60_00-bodyFunction-Top ............................. SUCCESS [  0.045 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  04:17 min
[INFO] Finished at: 2022-08-18T18:19:28-04:00
[INFO] ------------------------------------------------------------------------
</pre>
