@echo off
REM =========================================================
REM DataMatch Studio - Windows Build Script
REM Builds the Maven package and compiles all objects
REM =========================================================
echo [INFO] Starting DataMatch Studio Build Process...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed! Check Maven output.
    exit /b %ERRORLEVEL%
)
echo [SUCCESS] Build completed successfully! JAR generated in target/datamatch-1.0.0.jar
