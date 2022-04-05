package de.neuefische.app.spotify.playlistsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifySearchPlaylistImages(
        @JsonProperty("url") String url
) {
}
