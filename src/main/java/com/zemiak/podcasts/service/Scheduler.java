package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Podcast;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.*;
import javax.inject.Inject;

@Singleton
public class Scheduler {
    private static final Logger LOG = Logger.getLogger(Scheduler.class.getName());

    @Inject
    RecordService recordService;

    @Inject
    PodcastService podcastService;

    @Inject
    PodcastRemovalService removalService;

    @Inject
    TimerService timerService;

    final Map<Podcast, ScheduleEntry> planned = new HashMap<>();

    @Schedule(hour = "1", minute = "15", persistent = false)
    public void removeOldPodcasts() {
        removalService.removeOldPodcasts();
    }

    @Schedule(minute = "*", second = "59", persistent = false)
    public void scheduleNextRecordings() {
        ZonedDateTime now = ZonedDateTime.now(TimeZone.getDefault().toZoneId());
        for (Podcast podcast: podcastService.getPodcasts()) {
            CronExpression cron = new CronExpression(podcast.getCronExpression());
            ZonedDateTime when = cron.nextTimeAfter(now);

            if (planned.containsKey(podcast)) {
                ScheduleEntry plannedTime = planned.get(podcast);
                if (!plannedTime.getDate().equals(when)) {
                    planned.remove(podcast);
                    unplan(podcast, plannedTime);
                    plan(podcast, when);
                }
            } else {
                plan(podcast, when);
            }
        }
    }

    @Timeout
    public void timeout(Timer timer) {
        Podcast podcast = (Podcast) timer.getInfo();

        try {
            recordService.record(podcast);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Recording exception", ex);
        }

        planned.remove(podcast);
    }


    private void plan(Podcast podcast, ZonedDateTime when) {
        Date date = Date.from(when.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        TimerConfig config = new TimerConfig();
        config.setInfo(podcast);
        Timer timer = timerService.createSingleActionTimer(date, config);
        planned.put(podcast, new ScheduleEntry(when, timer));
    }

    private void unplan(Podcast podcast, ScheduleEntry entry) {
        entry.timer.cancel();
        planned.remove(podcast);
    }

    public Map<Podcast, ScheduleEntry> getPlanned() {
        return planned;
    }

    public class ScheduleEntry {
        private final ZonedDateTime date;
        private final Timer timer;

        public ScheduleEntry(ZonedDateTime date, Timer timer) {
            this.date = date;
            this.timer = timer;
        }

        public ZonedDateTime getDate() {
            return date;
        }

        public Timer getTimer() {
            return timer;
        }
    }
}
