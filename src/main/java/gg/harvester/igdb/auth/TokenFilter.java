package gg.harvester.igdb.auth;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import java.io.IOException;

@Priority(Priorities.AUTHENTICATION)
public class TokenFilter implements ClientRequestFilter {

    private final AuthService service;

    TokenFilter(AuthService service) {
        this.service = service;
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        String token = service.getToken();
        clientRequestContext.getHeaders().putSingle("Client-ID", service.getClientId());
        clientRequestContext.getHeaders().putSingle("Authorization", "Bearer "+token);
    }
}
