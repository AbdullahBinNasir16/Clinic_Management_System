#!/bin/bash
echo "Compiling Clinic Management System..."
mkdir -p bin
javac -d bin src/cms/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi
echo "Compilation successful. Launching..."
java -cp bin cms.MainFrame
