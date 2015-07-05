package com.zemiak.podcasts.service.jsp;

import com.zemiak.podcasts.domain.Podcast;
import com.zemiak.podcasts.service.CDILookup;
import com.zemiak.podcasts.service.PodcastService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@RequestScoped
public class PodcastJSPService {
    private PodcastService podcasts;
    private Podcast podcast;

    public PodcastJSPService() {
        podcasts = new CDILookup().lookup(PodcastService.class);
    }

    public List<Podcast> getPodcasts() {
        return podcasts.getPodcasts();
    }

    public void setPodcastName(HttpServletRequest request) {
        String podcastName = request.getParameter("name");
        podcast = podcasts.find(podcastName);
        if (null == podcast) {
            throw new IllegalStateException("Unknown podcast " + podcastName);
        }
    }

    public Podcast getPodcast() {
        return podcast;
    }

    public String getNow() {
        return getNow(new Date().getTime());
    }

    public static String getNow(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(calendar.getTime());
    }
}
