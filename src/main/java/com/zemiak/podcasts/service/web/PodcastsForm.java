package com.zemiak.podcasts.service.web;

import com.zemiak.podcasts.domain.Podcast;
import com.zemiak.podcasts.service.PodcastService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("podcastsForm")
@RequestScoped
public class PodcastsForm {
    @Inject
    PodcastService service;

    List<Podcast> selected = new ArrayList<>();

    @PostConstruct
    public void init() {
        selected = new ArrayList<>(service.getPodcasts().stream().filter(p -> p.isEnabled()).collect(Collectors.toList()));
    }

    public List<Podcast> getSelected() {
        return selected;
    }

    public void setSelected(List<Podcast> selected) {
        this.selected = new ArrayList<>(selected);
    }

    public void save() {
        service.getPodcasts().stream().forEach(p -> p.setEnabled(false));
        this.selected.stream().forEach(this::setEnabled);
    }

    private void setEnabled(Podcast p) {
        service.find(p.getName()).setEnabled(true);
    }

    public List<Podcast> getAllEnabled() {
        return service.getPodcasts().stream().filter(Podcast::isEnabled).collect(Collectors.toList());
    }

    public List<Podcast> getAllDisabled() {
        return service.getPodcasts().stream().filter(Podcast::isDisabled).collect(Collectors.toList());
    }
}
