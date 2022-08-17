# Directory Description: Parent

The parent pom in this directory is a duplicate of the one in the java-nlp-framework\00_parent\pom.xml.
It is here to be able to bootstrap build from just the body_function/project project, as the body_function
parent pom references the parent pom from the java-nlp-framework. 

This (java-nlp-framework) mave parent pom includes project setup information in it, and dependency version
definitions to reference so that dependency versions are consistent internally across modules.

This parent pom is the java-framework parent pom, which is referenced by all of the other modules.
