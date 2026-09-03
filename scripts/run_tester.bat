@echo off
setlocal

set "JAR=%~dp0jdbc-tester-jvm.jar"

if not exist "%JAR%" (
    echo error: %JAR% not found ^(expected next to this script^) 1>&2
    exit /b 1
)

java -jar "%JAR%" -t oracle -H db.example.com -d ORCLPDB -u scott -p tiger
REM java -jar "%JAR%" -t -t mssql  -H db.example.com -d MyDatabase -u sa -p "S3cret!"
