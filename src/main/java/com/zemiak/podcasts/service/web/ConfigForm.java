package com.zemiak.podcasts.service.web;

import com.zemiak.podcasts.domain.Podcast;
import com.zemiak.podcasts.service.PodcastService;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class ConfigForm {
    @Inject
    PodcastService service;

    public List<Podcast> getAll() {
        return service.getPodcasts();
    }

    
}
