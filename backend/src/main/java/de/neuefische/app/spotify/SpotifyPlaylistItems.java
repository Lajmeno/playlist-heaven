package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record SpotifyPlaylistItems(
        @JsonProperty("name") String name,
        @JsonProperty("id") String id,
        @JsonProperty("images") List<SpotifyPlaylistImages> images
) {
}
