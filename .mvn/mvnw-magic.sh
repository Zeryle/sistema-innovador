#!/bin/bash
# Workaround: mvn shell script fails on JAVA_HOME with spaces.
# Invoke plexus-classworlds directly with -Dmaven.multiModuleProjectDirectory.
JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.11.9-hotspot"
M2_HOME="/c/Users/Zeryle/.m2/wrapper/dists/apache-maven-3.9.9/8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698"
export JAVA_HOME
exec "$JAVA_HOME/bin/java" \
  -cp "$M2_HOME/boot/plexus-classworlds-2.8.0.jar" \
  "-Dclassworlds.conf=$M2_HOME/bin/m2.conf" \
  "-Dmaven.home=$M2_HOME" \
  "-Dmaven.multiModuleProjectDirectory=$(pwd)" \
  org.codehaus.plexus.classworlds.launcher.Launcher "$@"