package de.neuefische.app.spotify.playlistsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifySearchPlaylist(
        @JsonProperty("name") String name,
        @JsonProperty("id") String id,
        @JsonProperty("images") List<SpotifySearchPlaylistImages> images
) {
}
