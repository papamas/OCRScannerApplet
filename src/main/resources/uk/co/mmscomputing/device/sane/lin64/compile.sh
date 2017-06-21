#!/bin/bash

JAVA_HOME="/usr/lib/jvm/java-6-sun"

#g++ -g -Wall -fPIC -pthread -D_REENTRANT -D_GNU_SOURCE -shared \
#	-o libjsane.so jsane.cpp jdesc.cpp jnu.cpp \
#	-I $JAVA_HOME/include -I $JAVA_HOME/include/linux

g++ -gdwarf-2 -shared -m64 -fPIC \
	-o libjsane.so jsane.cpp jdesc.cpp jnu.cpp \
	-I $JAVA_HOME/include -I $JAVA_HOME/include/linux
