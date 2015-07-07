package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import org.farng.mp3.id3.ID3v1_1;

public class Mp3Tagger {
    private static final String GENRE = "Speech";
    private static final String ARTIST = "Radio_FM";

    public Episode createId3Tag(String fileName, Podcast podcast, Date now) {
        Episode episode = new Episode(podcast, Paths.get(fileName));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        ID3v1_1 tag = episode.getTag();
        tag.setAlbum(podcast.getTitle());
        tag.setArtist(ARTIST);
        tag.setSongGenre(GENRE);
        tag.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
        tag.setTrackNumberOnAlbum(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        tag.setTitle(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)) + " " + podcast.getTitle());

        episode.saveTag();

        return episode;
    }
}
