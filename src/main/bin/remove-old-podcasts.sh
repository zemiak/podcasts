#!/bin/bash

cd /mnt/media/inbox || exit 1
find . -type f -mtime +31 -delete
