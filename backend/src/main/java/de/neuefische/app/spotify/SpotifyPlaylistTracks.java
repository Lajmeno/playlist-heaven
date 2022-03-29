package de.neuefische.app.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyPlaylistTracks (
        @JsonProperty("name") String name,
        @JsonProperty("added_at") String addedAt,
        @JsonProperty("id") String id,
        @JsonProperty("album") List<SpotifyTracksAlbum> album,
        @JsonProperty("artists") List<SpotifyTracksArtists> artist

) {
}
