package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyGetPlaylistsItems(
        @JsonProperty("added_at") String addedAt,
        @JsonProperty("track") SpotifyPlaylistTrack track
) {
}
