package com.zemiak.podcasts.domain;

import com.zemiak.podcasts.service.jsp.PodcastJSPService;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Objects;
import java.util.logging.Logger;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Episode {
    private static final Logger LOG = Logger.getLogger(Episode.class.getName());

    @Transient @XmlTransient
    private Podcast podcast;

    private String fileName;
    private Long fileSize;

    @Transient @XmlTransient
    private FileTime created;

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
        calendar.setTimeInMillis(podcast.getDurationSeconds() * 1000);

        return String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}
