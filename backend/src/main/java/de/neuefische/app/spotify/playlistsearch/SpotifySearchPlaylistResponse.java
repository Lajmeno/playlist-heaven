package de.neuefische.app.spotify.playlistsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifySearchPlaylistResponse(
        @JsonProperty("playlists") SpotifySeachPlaylistResult playlists
) {
}
