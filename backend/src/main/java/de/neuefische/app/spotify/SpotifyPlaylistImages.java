package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyPlaylistImages(
        @JsonProperty("url") String url
) {
}
