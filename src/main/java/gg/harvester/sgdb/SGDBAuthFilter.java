package gg.harvester.sgdb;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import java.io.IOException;

@Priority(Priorities.AUTHENTICATION)
public class SGDBAuthFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        var dotenv = Dotenv.configure().load();
        var appKey = dotenv.get("SGDB_APP_KEY", System.getenv("SGDB_APP_KEY"));
        clientRequestContext.getHeaders().putSingle("Authorization", "Bearer "+appKey);
    }
}
