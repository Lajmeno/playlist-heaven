package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyPlaylistItemsResponse(
        @JsonProperty("items") List<SpotifyPlaylistTracks> items
) {
}
