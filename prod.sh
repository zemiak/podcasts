#!/bin/sh

docker rm -f podcasts
docker run -d -p 8082:80 --name podcasts -v /mnt/media/inbox:/mnt/media/inbox podcasts:SNAPSHOT
