package com.zemiak.podcasts.domain;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.zemiak.podcasts.service.jsp.PodcastJSPService;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Episode {
    private Podcast podcast;
    private String fileName;
    private Long fileSize;
    private Long durationSeconds;
    private FileTime created;
    private Mp3File mp3File;

    public Episode(Podcast podcast, Path path) {
        this.podcast = podcast;
        this.fileName = path.toString();

        File file = new File(fileName);
        BasicFileAttributes attr;

        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(Episode.class.getName()).log(Level.SEVERE, "Cannot read file attributes " + fileName, ex);
            throw new IllegalStateException(ex);
        }

        this.fileSize = file.length();
        this.created = attr.creationTime();

        try {
            mp3File = new Mp3File(file);
        } catch (IOException ex) {
            Logger.getLogger(Episode.class.getName()).log(Level.SEVERE, "Cannot read ID3 tag for " + fileName, ex);
            throw new IllegalStateException(ex);
        } catch (UnsupportedTagException ex) {
            Logger.getLogger(Episode.class.getName()).log(Level.SEVERE, "Unsupported tag in " + fileName, ex);
            throw new IllegalStateException(ex);
        } catch (InvalidDataException ex) {
            Logger.getLogger(Episode.class.getName()).log(Level.SEVERE, "Invalid tag data in " + fileName, ex);
            throw new IllegalStateException(ex);
        }

        this.durationSeconds = mp3File.getLengthInSeconds();
    }

    public FileTime getCreated() {
        return created;
    }

    public void setCreated(FileTime created) {
        this.created = created;
    }

    public Podcast getPodcast() {
        return podcast;
    }

    public void setPodcast(Podcast podcast) {
        this.podcast = podcast;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    public void setMp3File(Mp3File mp3File) {
        this.mp3File = mp3File;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.fileName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Episode other = (Episode) obj;
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        return true;
    }

    public String getBaseFileName() {
        return new File(fileName).getName();
    }

    public String getBaseFileNameWithoutExtension() {
        String baseFileName = getBaseFileName();
        return baseFileName.substring(0, baseFileName.length() - 4); // cut also the extension
    }

    public int getCreatedWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(created.toMillis());

        return calendar.getWeekYear();
    }

    public String getGuid() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest instance = MessageDigest.getInstance("MD5");
        instance.update(fileName.getBytes("UTF-8"));

        return instance.toString();
    }

    public String getCreatedString() {
        return PodcastJSPService.getNow(created.toMillis());
    }

    public String getDuration() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(created.toMillis());

        return String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}
