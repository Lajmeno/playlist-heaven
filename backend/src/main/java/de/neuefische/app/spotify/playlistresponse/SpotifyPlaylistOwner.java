package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyPlaylistOwner(
        @JsonProperty("id") String id
) {
}
