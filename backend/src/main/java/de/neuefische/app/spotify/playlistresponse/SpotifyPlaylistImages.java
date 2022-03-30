package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyPlaylistImages(
        @JsonProperty("url") String url
) {
}
