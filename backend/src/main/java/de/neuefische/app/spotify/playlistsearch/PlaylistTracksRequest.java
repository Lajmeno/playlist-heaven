package de.neuefische.app.spotify.playlistsearch;

import java.util.List;

public record PlaylistTracksRequest(
        List<String> uris
) {

}
