#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$SCRIPT_DIR/jdbc-tester-jvm.jar"

if [[ ! -f "$JAR" ]]; then
    echo "error: $JAR not found (expected next to this script)" >&2
    exit 1
fi


# 3DPASS
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dpassport -p x3dpassport
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dpasstokens -p x3dpasstokens

# 3DDASH
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3ddash -p x3ddash

# 3DSPACE
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dspace -p x3dspace

# 3DSWYM
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dswym_social -p x3dswym_social
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dswym_media -p x3dswym_media
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dswym_widget -p x3dswym_widget

# 3DCOMMENT
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dcomment -p x3dcomment

# 3DNOTICE
java -jar "$JAR" -t oracle -H db.example.com -d PLATFORM -u x3dnotif -p x3dnotif

# MSSQL example
#java -jar "$JAR" -t mssql -H db.example.com -d MyDatabase -u sa -p "S3cret!"
