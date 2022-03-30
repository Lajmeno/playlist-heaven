package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTracksAlbum(
        @JsonProperty("name") String name,
        @JsonProperty("release_date") String date
) {
}
