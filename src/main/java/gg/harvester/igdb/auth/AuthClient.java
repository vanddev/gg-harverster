package gg.harvester.igdb.auth;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "twitch-oauth")
public interface AuthClient {

    @POST
    AuthResponse authenticate(
            @QueryParam("client_id") String clientId,
            @QueryParam("client_secret") String clientSecret,
            @QueryParam("grant_type") String grantType
    );
}
