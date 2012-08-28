@echo off

java -version

@echo.
cd ..

java -jar jityServer.jar -start

@if errorlevel 1 pause