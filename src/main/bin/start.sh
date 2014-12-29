#!/bin/bash

cd $(dirname $0)/..
JERRY_HOME=$(pwd)
java -cp "lib/*:conf/" -Dgaia.base=${JERRY_HOME} -Dgaia.home=${JERRY_HOME} org.omg.gaia.startup.Bootstrap
