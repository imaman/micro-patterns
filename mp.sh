#! /bin/bash
BASE_DIR=`dirname $0`
mvn compile && mvn exec:java -Dexec.args="$*"


