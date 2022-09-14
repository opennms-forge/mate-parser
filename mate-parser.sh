#!/bin/bash
cd `dirname $0`
java -jar target/mate-parser-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
