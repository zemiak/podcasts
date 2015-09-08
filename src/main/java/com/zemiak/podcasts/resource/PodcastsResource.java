package com.zemiak.podcasts.resource;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.domain.Podcast;
import com.zemiak.podcasts.service.EpisodeService;
import com.zemiak.podcasts.service.PodcastService;
import com.zemiak.podcasts.service.RecordService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("podcasts")
@Produces("application/json")
@Consumes("application/json")
public class PodcastsResource {
    @Inject
    PodcastService podcasts;

    @Inject
    EpisodeService episodes;

    @Inject
    RecordService recorder;

    @GET
    public List<Podcast> getAllPodcasts() {
        return podcasts.getPodcasts();
    }

    @GET
    @Path("{name}/episodes")
    public Response getEpisodes(@PathParam("name") String name) {
        Podcast podcast = podcasts.find(name);
        if (null == podcast) {
            return Response.status(Response.Status.NOT_FOUND).entity("Cannot find podcast " + name).build();
        }

        return Response.ok().entity(episodes.getEpisodes(podcast)).build();
    }

    @POST
    @Path("{name}/record")
    public Response record(@PathParam("name") String name) throws URISyntaxException {
        Podcast podcast = podcasts.find(name);
        if (null == podcast) {
            return Response.status(Response.Status.NOT_FOUND).entity("Cannot find podcast " + name).build();
        }

        Episode episode = recorder.record(podcast);
        return Response.created(new URI("/podcasts/resources/episodes/" + episode.getBaseFileNameWithoutExtension())).build();
    }
}
