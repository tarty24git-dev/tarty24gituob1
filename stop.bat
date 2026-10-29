@echo off
REM =========================================================
REM DataMatch Studio - Windows Stop Script
REM Gracefully stops the running Java process
REM =========================================================
echo [INFO] Stopping DataMatch Studio...
for /f "tokens=1" %%i in ('jcmd ^| findstr "datamatch-1.0.0.jar"') do (
    echo Stopping PID %%i
    taskkill /PID %%i /F
)
echo [SUCCESS] Application stopped.
