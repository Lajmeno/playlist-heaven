package de.neuefische.app.spotify.playlistsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifySearchPlaylistBody(
        @JsonProperty("playlists") SpotifySearchPlaylistItems playlists
) {
}
