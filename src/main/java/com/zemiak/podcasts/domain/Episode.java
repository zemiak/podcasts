package com.zemiak.podcasts.domain;

import com.zemiak.podcasts.service.jsp.PodcastJSPService;
import java.io.*;
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.id3.ID3v1_1;

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

    @Transient @XmlTransient
    private File file;

    private ID3v1_1 tag;

    public Episode(Podcast podcast, Path path) {
        this.podcast = podcast;
        this.fileName = path.toString();

        file = new File(fileName);
        BasicFileAttributes attr;

        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(Episode.class.getName()).log(Level.SEVERE, "Cannot read file attributes " + fileName, ex);
            throw new IllegalStateException(ex);
        }

        this.fileSize = file.length();
        this.created = attr.creationTime();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            tag = new ID3v1_1(raf);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot read file {0}", fileName);
            throw new IllegalStateException(ex);
        } catch (TagNotFoundException ex) {
            LOG.log(Level.FINE, "Cannot read ID3 tag {0}", fileName);
            tag = new ID3v1_1();
        }
    }

    public void saveTag() {
        MP3File mp3file = new MP3File();
        mp3file.setID3v1Tag(tag);

        try {
            mp3file.save(file);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot write file {0}", fileName);
            throw new IllegalStateException(ex);
        } catch (TagException ex) {
            LOG.log(Level.FINE, "Cannot write ID3 tag {0}", fileName);
            throw new IllegalStateException(ex);
        }
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

    public ID3v1_1 getTag() {
        return tag;
    }

    public void setTag(ID3v1_1 tag) {
        this.tag = tag;
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
