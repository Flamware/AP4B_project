#!/bin/bash

# Définir les chemins des fichiers source et du fichier JAR
SOURCE_DIR="src"
BIN_DIR="bin"
MANIFEST_FILE="Manifest.txt"
JAR_FILE="utmunchkin.jar"

# 1. Compilation des fichiers source
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/Interface/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/cards/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/gameplay/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/gameplay/img/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/gameplay/img/game/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/gameplay/img/img_menu/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/gameplay/img/main/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/gameplay/img/stats/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/players/*.java
javac -d $BIN_DIR $SOURCE_DIR/main/java/com/utmunchkin/utils/*.java

# 2. Création du fichier manifeste
echo "Main-Class: Main" > $MANIFEST_FILE

# 3. Création du fichier JAR
jar cfm $JAR_FILE $MANIFEST_FILE -C $BIN_DIR .

# 4. Exécution du fichier JAR
java -jar $JAR_FILE

