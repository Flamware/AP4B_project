#!/bin/bash

# Définir les chemins des fichiers source et du fichier JAR
SOURCE_DIR="src"
BIN_DIR="bin"
MANIFEST_FILE="Manifest.txt"
JAR_FILE="utmunchkin.jar"
JAR_FILENAME_FILE="jar.txt"

# 1. Compilation des fichiers source
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/Interface/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/cards/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/gameplay/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/gameplay/img/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/gameplay/img/game/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/gameplay/img/img_menu/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/gameplay/img/main/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/gameplay/img/stats/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/players/*.java
javac -d "$BIN_DIR" "$SOURCE_DIR"/main/java/com/utmunchkin/utils/*.java

# Vérification de la réussite de la compilation
if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation. Arrêt du script."
    exit 1
fi

# 2. Création du fichier manifeste
echo "Main-Class: Main" > "$MANIFEST_FILE"

# 3. Création du fichier JAR
jar cfm "$JAR_FILE" "$MANIFEST_FILE" -C "$BIN_DIR" .

# Vérification de la réussite de la création du JAR
if [ $? -ne 0 ]; then
    echo "Erreur lors de la création du fichier JAR. Arrêt du script."
    exit 1
fi

echo "$JAR_FILE" > "$JAR_FILENAME_FILE"

# 4. Exécution du fichier JAR
java -jar "$JAR_FILE"


