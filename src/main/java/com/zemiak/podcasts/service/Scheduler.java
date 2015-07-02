package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Podcast;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;

@Startup
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

    private Map<Timer, Podcast> schedule;

    @PostConstruct
    public void init() {
        podcastService.getPodcasts().forEach(p -> schedule.put(schedulePodcast(p), p));
        schedule.keySet().stream().forEach(key -> {
            LOG.info(String.format("Podcast %s: timer %s", schedule.get(key).getName(), key.toString()));
        });
    }

    @Schedule(hour = "1", minute = "15", persistent = false)
    public void removeOldPodcasts() {
        removalService.removeOldPodcasts();
    }

    private Timer schedulePodcast(Podcast podcast) {
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.dayOfWeek(podcast.getDayOfWeek());
        schedule.hour(podcast.getHour());
        schedule.minute(podcast.getMinute());

        return timerService.createCalendarTimer(schedule);
    }

    @Timeout
    public void scheduleRun(Timer timer) {
        if (! schedule.containsKey(timer)) {
            LOG.severe(String.format("Unknown timer %s fired", timer.toString()));
            return;
        }

        recordService.record(schedule.get(timer));
    }
}
