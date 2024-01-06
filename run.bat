@echo off
REM Lire le nom du fichier JAR à partir du fichier texte
set JAR_FILENAME_FILE=jar.txt
set /p JAR_FILE=<"%JAR_FILENAME_FILE%"

REM Exécution du fichier JAR
java -jar "%JAR_FILE%"
