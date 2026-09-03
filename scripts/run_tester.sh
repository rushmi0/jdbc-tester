#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$SCRIPT_DIR/jdbc-tester-jvm.jar"

if [[ ! -f "$JAR" ]]; then
    echo "error: $JAR not found (expected next to this script)" >&2
    exit 1
fi

exec java -jar "$JAR" -t oracle -H db.example.com -d ORCLPDB -u scott -p tiger
#exec java -jar "$JAR" -t -t mssql  -H db.example.com -d MyDatabase -u sa -p "S3cret!"
