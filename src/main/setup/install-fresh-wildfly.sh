#!/bin/sh

WILDFLY=wildfly-10.0.0.Final
PROJECT=~/Documents/projects/podcasts
DOWNLOAD=~/Downloads/$WILDFLY.zip
INSTALL=~/bin
TARGET=wildfly

cd $INSTALL
test -d $TARGET && rm -rf $TARGET
cp $DOWNLOAD ./
unzip $WILDFLY.zip >/dev/null
rm $WILDFLY.zip
mv $WILDFLY $TARGET
cd $TARGET/standalone/configuration
rm standalone*.xml
cp $PROJECT/src/main/setup/standalone-dev.xml ./standalone.xml
ln -s standalone.xml standalone-full.xml
ln -s standalone.xml standalone-full-ha.xml
ln -s standalone.xml standalone-ha.xml
cd ../../..

./$TARGET/bin/add-user.sh admin admin --silent
