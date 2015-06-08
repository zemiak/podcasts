package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class RecordService {
    @Inject
    String radioFmUrl;

    @Inject
    Mp3Tagger tagger;

    Date now;

    @PostConstruct
    public void init() {
        now = new Date();
    }

    public Episode record(Podcast podcast) {
        String outputFileName = getOutputFileName(podcast);

        Recorder recorder = new Recorder(radioFmUrl, outputFileName, podcast.getDurationSeconds());
        recorder.run();

        return tagger.createId3Tag(outputFileName, podcast, now);
    }

    private String getOutputFileName(Podcast podcast) {
        String date = new SimpleDateFormat("yyMMdd").format(now);

        return date + "_" + podcast.getName() + ".mp3";
    }
}
