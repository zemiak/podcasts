#!/bin/sh

tag=podcasts:SNAPSHOT

cd src/main
docker build -t ${tag} .
