package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyGetPlaylistResponse(
        @JsonProperty("total") int total,
        @JsonProperty("next") String next,
        @JsonProperty("items") List<SpotifyPlaylistItems> items
) {
}
