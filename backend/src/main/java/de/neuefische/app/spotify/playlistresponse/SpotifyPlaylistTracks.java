package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyPlaylistTracks (
        @JsonProperty("items") List<SpotifyGetPlaylistsItems> items,
        @JsonProperty("total") int total,
        @JsonProperty("next") String next,
        @JsonProperty("offset") int offset
) {
}
