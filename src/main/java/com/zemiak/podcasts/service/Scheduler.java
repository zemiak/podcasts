package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Podcast;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;

@Startup
@Singleton
public class Scheduler {
    @Inject
    RecordService recordService;

    @Inject
    PodcastService podcastService;

    @Inject
    PodcastRemovalService removalService;

    @Resource
    TimerService timerService;

    @PostConstruct
    public void init() {
        podcastService.getPodcasts().forEach(p -> schedule(p));
    }

    @Schedule(hour = "1", minute = "15", persistent = false)
    public void removeOldPodcasts() {
        removalService.removeOldPodcasts();
    }

    private Timer schedule(Podcast podcast) {
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.dayOfWeek(podcast.getDayOfWeek());
        schedule.hour(podcast.getHour());
        schedule.minute(podcast.getMinute());

        return timerService.createCalendarTimer(schedule);
    }
}
