package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTracksArtists(
        @JsonProperty("name") String name
) {
}
