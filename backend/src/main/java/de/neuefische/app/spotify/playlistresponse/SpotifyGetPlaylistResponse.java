package de.neuefische.app.spotify.playlistresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyGetPlaylistResponse(
        @JsonProperty("name") String name,
        @JsonProperty("id") String id,
        @JsonProperty("tracks") SpotifyPlaylistTracks tracks,
        @JsonProperty("images") List<SpotifyPlaylistImages> images,
        @JsonProperty("owner") SpotifyPlaylistOwner owner
) {
}
