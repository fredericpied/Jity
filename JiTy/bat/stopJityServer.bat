@echo off

java -version

@echo.
cd ..

java -jar jityServer.jar -stop

@if errorlevel 1 pause