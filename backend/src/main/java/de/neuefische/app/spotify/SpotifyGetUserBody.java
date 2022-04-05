package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyGetUserBody(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email,
        @JsonProperty("display_name") String name  )
{
}
