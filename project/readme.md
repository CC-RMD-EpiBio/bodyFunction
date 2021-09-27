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

# General Comments About This Project

This project is built from framework-legacy, a suite of functionality, that, itself is built upon [UIMA](https://uima.apache.org/)  2.9.  Framework-legacy is an evolution of [V3-NLP Framework](https://pubmed.ncbi.nlm.nih.gov/27683667/); which was built specifically for use within the Veteran Administration's [VINCI environment](https://www.hsrd.research.va.gov/for_researchers/vinci/workspace.cfm) to process, at scale, VA clinical documents.  

Note:  framework-legacy relies upon [uimaFIT](https://uima.apache.org/uimafit.html), to define the pipelines in code rather than through the myriad of configuration files.  You will not find the UIMA configuration files in the above projects, except to retrofit and work with other UIMA based tools. 

This project is built using Maven and Java.  Maven reactors should kick off the cascade of sub-projects to be made.