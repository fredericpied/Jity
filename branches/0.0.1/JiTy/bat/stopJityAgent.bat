@echo off

java -version

@echo.
cd ..

java -jar jityAgent.jar -stop

@if errorlevel 1 pause