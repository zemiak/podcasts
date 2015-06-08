package com.zemiak.podcasts.service;

import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v22Tag;
import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

public class Mp3Tagger {
    private static final String GENRE = "Speech";
    private static final String ARTIST = "Radio_FM";

    public Episode createId3Tag(String fileName, Podcast podcast, Date now) {
        Episode episode = new Episode(podcast, Paths.get(fileName));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        ID3v1Tag tag = new ID3v1Tag();
        tag.setAlbum(podcast.getTitle());
        tag.setArtist(ARTIST);
        tag.setGenre(ID3v1Genres.matchGenreDescription(GENRE));
        tag.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
        tag.setTrack(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        tag.setTitle(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)) + " " + podcast.getTitle());

        episode.getMp3File().setId3v1Tag(tag);

        ID3v22Tag tag2 = new ID3v22Tag();
        tag2.setAlbum(podcast.getTitle());
        tag2.setArtist(ARTIST);
        tag2.setGenre(ID3v1Genres.matchGenreDescription(GENRE));
        tag2.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
        tag2.setTrack(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        tag2.setTitle(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)) + " " + podcast.getTitle());

        episode.getMp3File().setId3v2Tag(tag2);
        
        return episode;
    }
}
