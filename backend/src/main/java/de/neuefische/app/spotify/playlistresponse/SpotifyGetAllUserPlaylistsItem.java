package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record SpotifyGetAllUserPlaylistsItem(
        @JsonProperty("name") String name,
        @JsonProperty("id") String id,
        @JsonProperty("images") List<SpotifyPlaylistImages> images
) {
}
