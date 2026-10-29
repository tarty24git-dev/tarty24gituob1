#!/bin/bash
# =========================================================
# DataMatch Studio - Linux Start Script
# Configured with maximum settings for 8GB RAM JVM allocation
# =========================================================
APP_JAR="target/datamatch-1.0.0.jar"
PID_FILE="datamatch.pid"

if [ ! -f "$APP_JAR" ]; then
    echo "[ERROR] Application JAR $APP_JAR not found. Running build script first..."
    ./build.sh
fi

if [ -f "$PID_FILE" ] && kill -0 $(cat "$PID_FILE") 2>/dev/null; then
    echo "[WARN] DataMatch Studio is already running under PID $(cat $PID_FILE)."
    exit 1
fi

JAVA_OPTS="-Xms4g -Xmx8g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+AlwaysPreTouch"

echo "[INFO] Starting DataMatch Studio with 8GB Maximum Memory Profile..."
nohup java $JAVA_OPTS -jar "$APP_JAR" "$@" > app.log 2>&1 &
echo $! > "$PID_FILE"
echo "[SUCCESS] Application started on PID $(cat $PID_FILE). Logs available in app.log."
