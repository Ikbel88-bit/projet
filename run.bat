@echo off
echo Compilation du projet...
mvn clean compile

echo Execution du projet...
mvn javafx:run

pause 