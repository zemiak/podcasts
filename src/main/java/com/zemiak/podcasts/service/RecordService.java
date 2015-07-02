package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Session;

public class RecordService {
    @Inject
    String radioFmUrl;

    @Inject
    Mp3Tagger tagger;

    @Resource(name = "java:/podcasts/mail/default")
    private Session mailSession;

    Date now;

    @PostConstruct
    public void init() {
        now = new Date();
    }

    public Episode record(Podcast podcast) {
        String outputFileName = getOutputFileName(podcast);

        Recorder recorder = new Recorder(radioFmUrl, outputFileName, podcast.getDurationSeconds());
        recorder.run();

        Episode episode = tagger.createId3Tag(outputFileName, podcast, now);
        sendInfoMail(episode);

        return episode;
    }

    private String getOutputFileName(Podcast podcast) {
        String date = new SimpleDateFormat("yyMMdd").format(now);

        return date + "_" + podcast.getName() + ".mp3";
    }
}
