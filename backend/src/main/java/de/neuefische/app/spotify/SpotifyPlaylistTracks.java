package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyPlaylistTracks (
        @JsonProperty("track") SpotifyPlaylistTrack track,
        @JsonProperty("added_at") String addedAt

) {
}
