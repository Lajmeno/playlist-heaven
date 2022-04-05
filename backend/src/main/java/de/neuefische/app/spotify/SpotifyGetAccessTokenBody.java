package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyGetAccessTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("scope") String scope,
        @JsonProperty("expires_in") int expirationTime,
        @JsonProperty("refresh_token") String refreshToken
) {
}
