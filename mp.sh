#! /bin/sh
BASE_DIR=`dirname $0`
mvn exec:java -Dexec.args="$*"


