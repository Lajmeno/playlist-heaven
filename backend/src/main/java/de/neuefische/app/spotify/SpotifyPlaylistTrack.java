package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyPlaylistTrack(
        @JsonProperty("name") String name,
        @JsonProperty("id") String id,
        @JsonProperty("album") SpotifyTracksAlbum album,
        @JsonProperty("artists") List<SpotifyTracksArtists> artist
) {
}
