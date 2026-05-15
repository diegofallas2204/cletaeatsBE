@echo off
setlocal
set MVNW_DIR=%~dp0
if "%MVNW_DIR:~-1%"=="\" set MVNW_DIR=%MVNW_DIR:~0,-1%
java -cp "%MVNW_DIR%\.mvn\wrapper\maven-wrapper.jar" -Dmaven.multiModuleProjectDirectory="%MVNW_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
