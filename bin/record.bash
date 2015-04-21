#!/bin/bash

if [ $# != 2 ]
then
    echo Usage: $0 file-name length
    exit 1
fi

FNAME=$1
LENGTH=$2

eval "(curl http://live.slovakradio.sk:8000/FM_256.mp3 >${FNAME} 2>/dev/null) &"
PID=$!
sleep "${LENGTH}"
kill $PID
