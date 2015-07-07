package com.zemiak.podcasts.service;

import java.util.logging.Logger;
import javax.ejb.*;
import javax.inject.Inject;

@Stateless
public class Scheduler {
    private static final Logger LOG = Logger.getLogger(Scheduler.class.getName());

    @Inject
    RecordService recordService;

    @Inject
    PodcastService podcastService;

    @Inject
    PodcastRemovalService removalService;

    @Schedule(hour = "1", minute = "15", persistent = false)
    public void removeOldPodcasts() {
        removalService.removeOldPodcasts();
    }

    @Schedule(hour = "18", minute = "3", dayOfWeek = "fri", persistent = false)
    public void balazHubinak() {
        recordService.record(podcastService.getBalazHubinakPodcast());
    }

    @Schedule(hour = "18", minute = "3", dayOfWeek = "fri", persistent = false)
    public void odVeci() {
        recordService.record(podcastService.getOdVeciPodcast());
    }
}
