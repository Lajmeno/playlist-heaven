package de.neuefische.app.spotify.playlistsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifySearchPlaylistResult(
        @JsonProperty("items") List<SpotifySearchPlaylist> items
) {
}
