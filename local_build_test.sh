#!/bin/bash
set -e # Parar se houver erro

echo "--- 1. Building Frontend ---"
cd frontend
npm install
npm run build
cd ..

echo "--- 2. Copying Frontend to Backend ---"
# Create destination if needed
mkdir -p backend/src/main/resources/static
# Clean target directory to prevent stale files locally
rm -rf backend/src/main/resources/static/*
# Copy build artifacts
cp -r frontend/dist/* backend/src/main/resources/static/

echo "--- 3. Building Backend ---"
cd backend
mvn clean package -DskipTests
cd ..

echo "--- Build Complete! ---"
echo "To run the application:"
echo "java -jar backend/target/backend-0.0.1-SNAPSHOT.jar"
echo " Go to http://localhost:8080 to see the application"
echo " Ctrl+C to stop the application"
