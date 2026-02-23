package gg.harvester.igdb.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token") String token,
        @JsonProperty("expires_in") long expiresIn
) {
}
