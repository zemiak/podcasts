#!/bin/sh

docker rm -f podcasts
docker run -d -p 8082:80 --name podcasts podcasts:SNAPSHOT
