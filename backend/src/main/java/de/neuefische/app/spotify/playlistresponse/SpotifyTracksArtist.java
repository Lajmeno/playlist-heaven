package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTracksArtist(
        @JsonProperty("name") String name
) {
}
