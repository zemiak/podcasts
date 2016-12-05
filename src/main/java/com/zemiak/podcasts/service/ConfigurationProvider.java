package com.zemiak.podcasts.service;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Needed ENV keys are listed below.
 *
 * MEDIA_PATH
 * MAIL_TO
 * RADIO_FM_URL
 */
public final class ConfigurationProvider {
    private static String get(String key) {
        String value = System.getenv(key);
        if (null == value || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing configuration " + key);
        }

        return value;
    }

    private static Path getBasePath() {
        return Paths.get(get("MEDIA_PATH"));
    }

    public static String getPath() {
        return getBasePath().toString();
    }

    public static String getRadioFmUrl() {
        return get("RADIO_FM_URL");
    }

    public static String getMailTo() {
        return get("MAIL_TO");
    }
}
