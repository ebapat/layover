#!/bin/bash
# Compile everything into out/ and run the program.
# Compiled files go in out/ so they stay out of the repo.
set -e
mkdir -p out
javac -d out src/layover/*.java
java -cp out layover.Main "$@"
