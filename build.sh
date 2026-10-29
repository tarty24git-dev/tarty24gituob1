#!/bin/bash
# =========================================================
# DataMatch Studio - Linux Build Script
# Builds the Maven package and compiles all objects
# =========================================================
set -e
echo "[INFO] Starting DataMatch Studio Build Process..."
mvn clean package -DskipTests
echo "[SUCCESS] Build completed successfully! JAR generated in target/datamatch-1.0.0.jar"
