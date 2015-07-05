package com.zemiak.podcasts.resource;

import com.zemiak.podcasts.domain.Episode;
import com.zemiak.podcasts.service.PodcastService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("episodes")
@Produces("application/json")
@Consumes("application/json")
public class EpisodesResource {
    @Inject
    PodcastService podcasts;

    @GET
    @Path("{name}")
    public Response find(@PathParam("name") String name) {
        Episode episode = podcasts.findEpisode(name);
        if (null == episode) {
            return Response.status(Response.Status.NOT_FOUND).entity("Cannot find episode " + name).build();
        }

        return Response.ok().entity(episode).build();
    }
}
