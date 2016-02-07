package com.zemiak.podcasts.service;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodcastService {
    private static final int MINUTE = 60;
    private static final int HOUR = MINUTE * 60;
    private static final Logger LOG = Logger.getLogger(PodcastService.class.getName());

    public List<Podcast> getPodcasts() {
        List<Podcast> podcasts = new ArrayList<>();
        podcasts.add(getBalazHubinakPodcast());
        podcasts.add(getOdVeciPodcast());
        podcasts.add(getTestPodcast());

        return podcasts;
    }

    public Podcast getBalazHubinakPodcast() {
        Podcast podcast = new Podcast();
        podcast.setName("balaz_hubinak");
        podcast.setTitle("Baláž a Hubinák");
        podcast.setDescription("Program s autorskou dvojicou Daniel Baláž a Pavol Hubinák. Zábava, recesia, glosovanie aktuálnych udalostí a atypická hudobná dramaturgia. Pravidelné rubriky a interakcia s poslucháčmi. Čokoľvek, čo nečakáte.");
        podcast.setPicture("http://static.etrend.sk/uploads/tx_media/2011/12/12/balaz_a_hubinak_radio_fm.jpg");
        podcast.setDurationSeconds(3 * HOUR);
        podcast.setDayOfWeek("fri");
        podcast.setHour(18);
        podcast.setMinute(3);

        return podcast;
    }

    public Podcast getOdVeciPodcast() {
        Podcast podcast = new Podcast();
        podcast.setName("od_veci");
        podcast.setTitle("Od Veci_FM");
        podcast.setDescription("Tomáš Hudák a Ludwig Bagin rozoberajú aktuálne témy a nadhadzujú tak tematickú korisť trojici Jurajovi „Šokovi“ Tabačkovi, Stanovi Staškovi a Lukášovi „Puchovi\" Puchovskému (známi z 3T). Nalaďte sa každý nepárny štvrtok od 20:00 do 22:00.");
        podcast.setPicture("http://static.hudba.zoznam.sk/media/obrazky/magazin/galeria/58972/od-veci_fm-nova-humoristicka-relacia.jpg");
        podcast.setDurationSeconds(2 * HOUR);
        podcast.setDayOfWeek("thu");
        podcast.setHour(19);
        podcast.setMinute(59);

        return podcast;
    }

    public Podcast getTestPodcast() {
        Podcast podcast = new Podcast();
        podcast.setName("test");
        podcast.setTitle("Test_FM");
        podcast.setDescription("Testing 10 minutes");
        podcast.setPicture("");
        podcast.setDurationSeconds(15);
        podcast.setDayOfWeek("*");
        podcast.setHour(1);
        podcast.setMinute(2);

        return podcast;
    }

    public Podcast find(String podcastName) {
        return getPodcasts().stream()
                .filter(p -> p.getName().equals(podcastName))
                .findFirst()
                .orElse(null);
    }

    public Episode findEpisode(String baseFileName) {
        String podcastName = baseFileName.substring(7);
        Podcast podcast = find(podcastName);
        if (null == podcast) {
            LOG.log(Level.SEVERE, "Cannot find podcast {0}", podcastName);
            return null;
        }

        return podcast.getEpisodes().stream()
                .filter(e -> e.getFileName().contains(baseFileName))
                .findFirst()
                .orElse(null);
    }
}
