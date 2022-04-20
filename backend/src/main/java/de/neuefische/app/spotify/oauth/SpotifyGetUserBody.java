package de.neuefische.app.spotify.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyGetUserBody(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email,
        @JsonProperty("display_name") String name  )
{
}
