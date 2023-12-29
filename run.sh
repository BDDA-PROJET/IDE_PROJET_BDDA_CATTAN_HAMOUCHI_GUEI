#!/bin/sh

# Navigate to the directory containing your Java files
cd ./CODE/src

# Find all Java files in the src directory and its subdirectories, and compile them
find . -name "*.java" -print | xargs javac -d ../bin

# Navigate back to the project root directory
cd ../..

# Check if the file exists
if [ -f "$1" ]; then
    # Run the main class with the first command-line argument
    java -cp ./CODE/bin up.mi.bdda.app.QueryManager $1 $2
else
    echo "File not found: $1"
fi