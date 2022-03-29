package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTracksArtists(
        @JsonProperty("name") String name
) {
}
