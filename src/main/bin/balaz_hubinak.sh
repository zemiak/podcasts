#!/bin/sh

podcast=balaz_hubinak
date="$(date +%Y%m%d)"
fileName="/mnt/media/inbox/${date}_${podcast}.mp3"
length="3h"

# record
eval "(curl http://live.slovakradio.sk:8000/FM_128.mp3 >${fileName} 2>/dev/null) &"
PID=$!
sleep "${length}"
kill $PID

# update feed
cd /opt/web
php podcast.php "${podcast}" >"/var/www/html/${podcast}.xml"
