package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Podcast;
import com.zemiak.podcasts.domain.ScheduleEntry;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Timer;
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

    @Resource
    TimerService timerService;

    final Map<Podcast, ScheduleEntry> planned = new HashMap<>();
    final Set<Podcast> recording = new HashSet<>();

    @Schedule(hour = "1", minute = "15", persistent = false)
    public void removeOldPodcasts() {
        removalService.removeOldPodcasts();
    }

    @Schedule(minute = "*", second = "59", persistent = false)
    public void scheduleNextRecordings() {
        ZonedDateTime now = ZonedDateTime.now(TimeZone.getDefault().toZoneId());
        podcastService.getPodcasts().forEach((podcast) -> {
            CronExpression cron = new CronExpression(podcast.getCronExpression(), false);
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
        });
    }

    @Timeout
    public void timeout(Timer timer) {
        Podcast podcast = (Podcast) timer.getInfo();

        try {
            recording.add(podcast);
            recordService.record(podcast);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Recording exception", ex);
        } finally {
            recording.remove(podcast);
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
        entry.getTimer().cancel();
        planned.remove(podcast);
    }

    public Map<Podcast, ScheduleEntry> getPlanned() {
        return planned;
    }

    public Set<Podcast> getRecording() {
        return recording;
    }
}
