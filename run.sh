#!/bin/bash

# Lire le nom du fichier JAR à partir du fichier texte
JAR_FILENAME_FILE="jar.txt"
JAR_FILE=$(cat "$JAR_FILENAME_FILE")

# Exécution du fichier JAR
java -jar "$JAR_FILE"
