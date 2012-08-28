@echo off

java -version

@echo.
cd ..

java -jar jityAgent.jar -start

@if errorlevel 1 pause