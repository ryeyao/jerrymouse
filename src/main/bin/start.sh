#!/bin/bash

cd $(dirname $0)/..
JERRY_HOME=$(pwd)
java -Dlog4j.configurationFile=conf/log4j2.xml -Dgaia.base=${JERRY_HOME} -Dgaia.home=${JERRY_HOME} -jar lib/Jerrymouse-1.5.jar
