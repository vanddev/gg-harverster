package gg.harvester.igdb.auth;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;

@ApplicationScoped
public class AuthService {

    private final AuthClient authClient;
    private final String clientId;
    private final String clientSecret;
    private final String grantType;
    private String token;
    private Instant expiresAt;

    AuthService(@RestClient AuthClient authClient) {
        this.authClient = authClient;

        var dotenv = Dotenv.configure().load();

        this.clientId = dotenv.get("IGDB_CLIENT_ID", System.getenv("IGDB_CLIENT_ID"));
        this.clientSecret = dotenv.get("IGDB_CLIENT_SECRET", System.getenv("IGDB_CLIENT_SECRET"));
        this.grantType = "client_credentials";
    }

    public synchronized String getToken() {
        if (token == null || isExpired()) {
            System.out.println("Token expired or missing - requesting new one");
            refreshToken();

        }

        return token;
    }

    public String getClientId() {
        return this.clientId;
    }

    private void refreshToken() {
        var response = authClient.authenticate(clientId, clientSecret, grantType);
        token = response.token();
        expiresAt = Instant.now().plusSeconds(response.expiresIn());
        System.out.printf("New token received, expires at %s \n", expiresAt);
    }

    private boolean isExpired() {
        return expiresAt == null || Instant.now().isAfter(expiresAt.minusSeconds(30));
    }




}
