package com.zemiak.podcasts.service.web;

import com.zemiak.podcasts.domain.Podcast;
import com.zemiak.podcasts.service.PodcastService;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named("podcastsForm")
@RequestScoped
public class PodcastsForm {
    @Inject
    PodcastService service;

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
}
