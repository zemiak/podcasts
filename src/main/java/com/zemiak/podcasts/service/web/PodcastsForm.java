package com.zemiak.podcasts.service.web;

import com.zemiak.podcasts.domain.Podcast;
import com.zemiak.podcasts.service.CronExpression;
import com.zemiak.podcasts.service.PodcastService;
import com.zemiak.podcasts.service.Scheduler;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named("podcastsForm")
@RequestScoped
public class PodcastsForm {
    @Inject PodcastService service;
    @Inject Scheduler scheduler;

    FacesContext faces;

    @PostConstruct
    public void init() {
        faces = FacesContext.getCurrentInstance();
    }

    public void save(String name) {
        Podcast podcast = service.find(name);
        String expression = getParam(podcast, "cron");
        String duration = getParam(podcast, "duration");
        podcast.setCronExpression(expression);
        podcast.setDurationSeconds(Integer.valueOf(duration));

        writeMessage(podcast.getTitle() + " has been updated.");
    }

    public void disable(String name) {
        Podcast podcast = service.find(name);
        podcast.setEnabled(false);

        writeMessage(podcast.getTitle() + " has been disabled.");
    }

    public void enable(String name) {
        Podcast podcast = service.find(name);
        podcast.setEnabled(true);

        writeMessage(podcast.getTitle() + " has been enabled.");
    }

    public List<Podcast> getAll() {
        return service.getPodcasts();
    }

    private String getParam(Podcast podcast, String fieldName) {
        Map<String, String> params = faces.getExternalContext().getRequestParameterMap();
        String key = "podcasts:j_idt10:" + podcast.getId() + ":" + fieldName;
        String value = params.get(key);
        return value;
    }

    private void writeMessage(String message) {
        faces.addMessage(null, new FacesMessage(message));
    }

    public Podcast getNearest() {
        long remaining = Long.MAX_VALUE;
        Podcast podcast = null;

        for (Podcast p: service.getPodcasts()) {
            if ("test".equals(p.getName())) {
                continue;
            }

            Long waitingTime = getWaitingTime(p);
            if (waitingTime < remaining) {
                podcast = p;
                remaining = waitingTime;
            }
        }

        return null == podcast ? noPodcast() : podcast;
    }

    public String getNearestTime() {
        long remaining = service.getPodcasts().stream().filter(p -> !"test".equals(p.getName())).map(this::getWaitingTime).min(Long::compare).get();
        long hours = remaining / 3600;
        remaining = remaining - (hours * 3600);
        long minutes = remaining / 60;

        return String.format("%02d hours, %02d minutes", hours, minutes);
    }

    private Long getWaitingTime(Podcast podcast) {
        CronExpression cron = new CronExpression(podcast.getCronExpression(), false);
        ZonedDateTime nextTimeAfter = cron.nextTimeAfter(ZonedDateTime.now());
        ZonedDateTime now = ZonedDateTime.now();
        return nextTimeAfter.toEpochSecond() - now.toEpochSecond();
    }

    public Podcast getRecording() {
        Set<Podcast> recordings = scheduler.getRecording();
        Podcast podcast = (null == recordings || recordings.isEmpty()) ? null : recordings.stream().findFirst().get();
        return null == podcast ? noPodcast() : podcast;
    }

    private Podcast noPodcast() {
        Podcast no = new Podcast();
        no.setName("(None)");

        return no;
    }
}
