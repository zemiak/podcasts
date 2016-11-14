package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RecordService {
    private static final Logger LOG = Logger.getLogger(RecordService.class.getName());

    @Inject
    String radioFmUrl;

    @Inject
    EpisodeService service;

    @Resource(name = "mail/podcasts")
    private Session mailSession;

    @Inject
    String mailFrom;

    @Inject
    String mailTo;

    @Inject
    String mailSubject;

    Date now;

    @PostConstruct
    public void init() {
        now = new Date();
    }

    public Episode record(Podcast podcast) {
        String outputFileName = getOutputFileName(podcast);

        Recorder recorder = new Recorder(radioFmUrl, outputFileName, podcast.getDurationSeconds());
        recorder.run();

        Path outputPath = Paths.get(outputFileName);
        Episode episode = service.create(podcast, outputPath);

        try {
            sendInfoMail(episode);
        } catch (MessagingException ex) {
            LOG.log(Level.SEVERE, "Cannot send info mail", ex);
        }

        return episode;
    }

    private String getOutputFileName(Podcast podcast) {
        String date = new SimpleDateFormat("yyMMdd").format(now);

        return date + "_" + podcast.getName() + ".mp3";
    }

    private void sendInfoMail(Episode episode) throws MessagingException {
        final Message message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(mailFrom));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
        message.setSubject(String.format(mailSubject, episode.getPodcast().getName()));

        message.setText("Recording has ended on  " + episode.getCreatedString() + ", duration " + episode.getDuration());

        Transport.send(message);

        LOG.log(Level.INFO, "Sent info message to {0}", mailTo);
    }
}
