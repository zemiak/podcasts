package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class PodcastService {
    private static final Logger LOG = Logger.getLogger(PodcastService.class.getName());
    private final List<Podcast> podcasts = new ArrayList<>();

    @PostConstruct
    public void readConfiguration() {
	ResourceBundle props = ResourceBundle.getBundle("podcasts");

        String podcastsString = props.getString("podcasts");
        String[] podcastsArray = podcastsString.split(",");

        int i = 0;
        for (String podcastName : podcastsArray) {
            Podcast podcast = readPodcast(props, podcastName);
            podcast.setId(i);
            podcasts.add(podcast);
            i++;
        }
    }

    public List<Podcast> getPodcasts() {
        return podcasts;
    }

    public Podcast find(String podcastName) {
        return getPodcasts().stream()
                .filter(p -> p.getName().equals(podcastName))
                .findFirst()
                .orElse(null);
    }

    public Podcast find(int id) {
        return getPodcasts().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Episode findEpisode(String baseFileName) {
        String podcastName = baseFileName.substring(7);
        Podcast podcast = find(podcastName);
        if (null == podcast) {
            LOG.log(Level.SEVERE, "Cannot find podcast {0}", podcastName);
            return null;
        }

        return podcast.getEpisodes().stream()
                .filter(e -> e.getFileName().contains(baseFileName))
                .findFirst()
                .orElse(null);
    }

    private Podcast readPodcast(ResourceBundle props, String name) {
        Podcast podcast = new Podcast();
        podcast.setTitle(props.getString(name + ".title"));
        podcast.setDescription(props.getString(name + ".description"));
        podcast.setPicture(props.getString(name + ".picture"));
        podcast.setDurationSeconds(Integer.valueOf(props.getString(name + ".duration")));
        podcast.setCronExpression(props.getString(name + ".cron"));
        podcast.setEnabled(props.getString(name + ".enabled").equals("true"));
        podcast.setName(name);

        return podcast;
    }
}
