package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyGetAllUserPlaylistsBody(
        @JsonProperty("total") int total,
        @JsonProperty("next") String next,
        @JsonProperty("items") List<SpotifyGetAllUserPlaylistsItem> items
) {
}
