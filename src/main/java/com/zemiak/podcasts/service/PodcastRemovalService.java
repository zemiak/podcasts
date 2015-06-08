package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class PodcastRemovalService {
    private static final long DAY = 1000 * 3600 * 24;
    private static final Logger LOG = Logger.getLogger(PodcastRemovalService.class.getName());

    @Inject
    private String path;

    public void removeOldPodcasts() {
        // find . -type f -mtime +31 -delete
        long now = new Date().getTime();

        try {
            Files.walk(Paths.get(path))
                    .filter(path -> path.getFileName().endsWith(".mp3"))
                    .filter(path -> (now - getCreated(path).toMillis()) > (DAY * 31))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            LOG.log(Level.INFO, "Deleted old podcast {0}", path.toString());
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, "Cannot remove the file {0}", path.toString());
                        }
            });
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot walk the path {0}", path);
            throw new IllegalStateException(ex);
        }
    }

    private FileTime getCreated(Path path) {
        BasicFileAttributes attr;

        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(Episode.class.getName()).log(Level.SEVERE, "Cannot read file attributes " + path.toString(), ex);
            throw new IllegalStateException(ex);
        }

        return attr.creationTime();
    }
}
