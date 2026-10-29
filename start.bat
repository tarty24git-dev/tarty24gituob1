@echo off
REM =========================================================
REM DataMatch Studio - Windows Start Script
REM Configured with maximum settings for 8GB RAM JVM allocation
REM =========================================================
echo [INFO] Starting DataMatch Studio with 8GB Maximum Memory Profile...
if not exist "target\datamatch-1.0.0.jar" (
    echo [ERROR] Application JAR target\datamatch-1.0.0.jar not found. Run build.bat first!
    exit /b 1
)

set JAVA_OPTS=-Xms4g -Xmx8g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+AlwaysPreTouch

java %JAVA_OPTS% -jar target\datamatch-1.0.0.jar %*
