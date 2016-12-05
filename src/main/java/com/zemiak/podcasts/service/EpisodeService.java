package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.id3.ID3v1_1;

@Stateless
public class EpisodeService {
    private static final Logger LOG = Logger.getLogger(EpisodeService.class.getName());

    private final String path = ConfigurationProvider.getPath();

    private static final byte GENRE = (byte) 101;
    private static final String ARTIST = "Radio_FM";

    public List<Episode> getEpisodes(Podcast podcast) {
        List<Episode> episodes = new ArrayList<>();

        try {
            String podcastName = podcast.getName();
            for (Path file: Files.walk(Paths.get(path)).collect(Collectors.toList())) {
                String fileName = file.getFileName().toString();

                if (fileName.endsWith(".mp3") && fileName.contains(podcastName)) {
                    Episode episode = create(podcast, file);
                    episodes.add(episode);
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot walk the path {0}", path);
            throw new IllegalStateException(ex);
        }

        return episodes;
    }

    public Episode create(Podcast podcast, Path episodePath) {
        Episode episode = new Episode();
        String fileName = episodePath.toString();

        File file = new File(fileName);
        BasicFileAttributes attr;

        try {
            attr = Files.readAttributes(episodePath, BasicFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(Episode.class.getName()).log(Level.SEVERE, "Cannot read file attributes " + fileName, ex);
            throw new IllegalStateException(ex);
        }

        episode.setFileSize(file.length());
        episode.setCreated(attr.creationTime());


        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            ID3v1_1 tag = new ID3v1_1(raf);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot read file {0}", fileName);
            throw new IllegalStateException(ex);
        } catch (TagNotFoundException ex) {
            LOG.log(Level.FINE, "Cannot read ID3 tag {0}", fileName);
            try {
                save(fileName, podcast, attr.creationTime());
            } catch (IOException ex1) {
                LOG.log(Level.SEVERE, "Cannot save a new ID3 Tag (IOException)", ex1);
                throw new IllegalStateException(ex1);
            } catch (TagException ex1) {
                LOG.log(Level.SEVERE, "Cannot save a new ID3 Tag (TagException)", ex1);
                throw new IllegalStateException(ex1);
            }
        }

        return episode;
    }

    private void save(String fileName, Podcast podcast, FileTime creationTime) throws IOException, TagException {
        ID3v1_1 tag = new ID3v1_1();

        tag.setAlbum(podcast.getName());
        tag.setArtist(ARTIST);
        tag.setGenre(GENRE);
        tag.setTitle(podcast.getTitle());

        Date created = Date.from(creationTime.toInstant());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(created);
        tag.setTrack((byte) calendar.get(Calendar.WEEK_OF_YEAR));

        MP3File file = new MP3File(fileName);
        file.setID3v1Tag(tag);
        file.save();
    }
}
