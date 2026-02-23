package gg.harvester.igdb.game;

import gg.harvester.igdb.CountDTO;
import gg.harvester.igdb.auth.TokenFilter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey="igdb")
@RegisterProvider(TokenFilter.class)
@Path("/games")
public interface GameClient {

    @POST
    @Path("/count")
    @Consumes(MediaType.TEXT_PLAIN)
    CountDTO fetchGamesCount(String body);

    @POST
    List<GamesDTO> fetchGames(String format);
}
