echo off
chcp 65001

set DIRECTORY=%~dp0
echo %DIRECTORY%
#cd /d "d:\ImageRecognition"

if "%1" equ "" goto prompt_path
set SOURCE_PATH=%1

:run_programm
java -classpath %CD%"\image_recognition\bin" Main %SOURCE_PATH%
pause
goto exit

:prompt_path
set /p SOURCE_PATH="Specify the path to the files: "
goto run_programm

:exit