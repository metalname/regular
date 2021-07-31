#!/bin/bash

# Check args

if [[ -z "$1" ]];
then
	echo "Usage: build.sh tag"
	exit 1
fi

tag=$1
PACKAGES=packages

# Create build directory

if [[ -d $PACKAGES/$tag ]];
then
	rm -rf $PACKAGES/$tag
fi

base=$PACKAGES/$tag
mkdir $base
mkdir $base/lib

# loop through subdirs, run ant in each

DIRS="Common Registry Regular"
LIBS="Common Registry"
MAIN="Regular"

for dir in $DIRS
do
	echo building $dir
	cd $dir
	ant
	if [[ $? -ne 0 ]];
	then
		exit 1
	fi
	cd ..
done

# copy jars

for dir in $LIBS
do
	cp $dir/dist/*.jar $base/lib
done

cp $MAIN/dist/*.jar $base

zip $PACKAGES/regular-0.0.5.zip -Z store $base -r

echo build complete
echo to launch: java -jar $base/$MAIN.jar
