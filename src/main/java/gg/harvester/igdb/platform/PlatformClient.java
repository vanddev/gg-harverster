package gg.harvester.igdb.platform;

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
@Path("/platforms")
public interface PlatformClient {

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    List<PlatformDTO> fetchPlatforms(String body);

}
