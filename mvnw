#!/usr/bin/env sh
set -e
BASEDIR=$(dirname "$0")
java -cp "$BASEDIR/.mvn/wrapper/maven-wrapper.jar" -Dmaven.multiModuleProjectDirectory="$BASEDIR" org.apache.maven.wrapper.MavenWrapperMain "$@"
