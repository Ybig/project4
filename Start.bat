@echo off
set CLASSPATH=.\mysql-connector-java-5.1.15-bin.jar;.\
javac *.java
echo Starting Program...
java XMLParser
pause