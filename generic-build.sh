#!/bin/bash
set -e
set -x

function move_back() {
  mv com.github.dnadolny.javatoscala/META-INF/MANIFEST.MF.bak com.github.dnadolny.javatoscala/META-INF/MANIFEST.MF
  mv com.github.dnadolny.javatoscala.tests/META-INF/MANIFEST.MF.bak com.github.dnadolny.javatoscala.tests/META-INF/MANIFEST.MF
}
trap move_back 0

change_version() {
  local manifestFile
  local scalaVersionExpression

  manifestFile="$1"
  shift
  scalaVersionExpression="$1"
  shift

  cp "$manifestFile" "$manifestFile.bak"
  sed -i -e "s/org\.scala-ide\.scala\.\(library\|compiler\)/org.scala-ide.scala.\1;bundle-version=\"$scalaVersionExpression\"/g" "$manifestFile"
}

#Build for Scala 2.9
change_version "com.github.dnadolny.javatoscala/META-INF/MANIFEST.MF" "[2.9,2.10)"
change_version "com.github.dnadolny.javatoscala.tests/META-INF/MANIFEST.MF" "[2.9,2.10)"
mvn clean package
rm -rf /tmp/javatoscala-build-ecosystem/generic-scala29
cp -r com.github.dnadolny.javatoscala.update-site/target/site/ /tmp/javatoscala-build-ecosystem/generic-scala29
move_back

#Build for Scala 2.10
change_version "com.github.dnadolny.javatoscala/META-INF/MANIFEST.MF" "[2.10,2.11)"
change_version "com.github.dnadolny.javatoscala.tests/META-INF/MANIFEST.MF" "[2.10,2.11)"
mvn -Drepo.scala-ide=http://download.scala-ide.org/sdk/next/e37/scala210/stable/site/ -Dscala.version=2.10.1 -Dversion.suffix=2_10 clean package
rm -rf /tmp/javatoscala-build-ecosystem/generic-scala210
cp -r com.github.dnadolny.javatoscala.update-site/target/site/ /tmp/javatoscala-build-ecosystem/generic-scala210
