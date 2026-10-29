#!/bin/bash
# =========================================================
# DataMatch Studio - Linux Stop Script
# Gracefully stops the running Java process using PID tracking
# =========================================================
PID_FILE="datamatch.pid"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if kill -0 "$PID" 2>/dev/null; then
        echo "[INFO] Stopping DataMatch Studio PID $PID..."
        kill -15 "$PID"
        rm -f "$PID_FILE"
        echo "[SUCCESS] Application stopped successfully."
    else
        echo "[WARN] PID file exists but process $PID is not running. Removing stale PID file."
        rm -f "$PID_FILE"
    fi
else
    echo "[WARN] No datamatch.pid found. Attempting to kill process by name..."
    pkill -f datamatch-1.0.0.jar || true
fi
