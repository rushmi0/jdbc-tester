@echo off
setlocal

set "JAR=%~dp0jdbc-tester-jvm.jar"

if not exist "%JAR%" (
    echo error: %JAR% not found ^(expected next to this script^) 1>&2
    exit /b 1
)


REM 3DPASS
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dpassport -p x3dpassport
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dpasstokens -p x3dpasstokens

REM 3DDASH
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3ddash -p x3ddash

REM 3DSPACE
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dspace -p x3dspace

REM 3DSWYM
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dswym_social -p x3dswym_social
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dswym_media -p x3dswym_media
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dswym_widget -p x3dswym_widget

REM 3DCOMMENT
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dcomment -p x3dcomment

REM 3DNOTICE
java -jar "%JAR%" -t oracle -H db.example.com -d PLATFORM -u x3dnotif -p x3dnotif

REM MSSQL example
REM java -jar "%JAR%" -t mssql -H db.example.com -d MyDatabase -u sa -p "S3cret!"
