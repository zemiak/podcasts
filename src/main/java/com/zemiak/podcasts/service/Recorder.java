package com.zemiak.podcasts.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Recorder {
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final Logger LOG = Logger.getLogger(Recorder.class.getName());

    private final String radioFmUrl;
    private final String outputFileName;
    private final int seconds;

    public Recorder(String radioFmUrl, String outputFileName, int seconds) {
        this.radioFmUrl = radioFmUrl;
        this.outputFileName = outputFileName;
        this.seconds = seconds;
    }

    public void run() {
        URL url;

        try {
            url = new URL(radioFmUrl);
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, "Malformed URL {0}", radioFmUrl);
            throw new IllegalStateException(ex);
        }

        final URLConnection connection;
        try {
            connection = url.openConnection();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot open connection {0}", radioFmUrl);
            throw new IllegalStateException(ex);
        }

        connection.setConnectTimeout(5 * MINUTE);
        connection.setReadTimeout(seconds * SECOND + 10 * MINUTE); // 10 minutes lead-out, for sure

        final InputStream input;
        try {
            input = connection.getInputStream();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot get input stream {0}", radioFmUrl);
            throw new IllegalStateException(ex);
        }

        try {
            Files.copy(input, Paths.get(outputFileName));
        } catch (java.net.SocketTimeoutException ex) {

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot write to file {0}", outputFileName);
            throw new IllegalStateException(ex);
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Cannot close the recording socket");
            }
        }
    }
}
