package gg.harvester.igdb.gamestatus;

import gg.harvester.igdb.auth.TokenFilter;
import gg.harvester.igdb.gametype.GameTypesDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey="igdb")
@RegisterProvider(TokenFilter.class)
@Path("/game_statuses")
public interface GamestatusClient {
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    List<GameStatusDTO> fetchGameStatus(String body);
}
