package de.neuefische.app.playlist;

import de.neuefische.app.spotify.playlistresponse.SpotifyTracksArtists;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistTrackArtist {
    private String name;

    public static PlaylistTrackArtist of(SpotifyTracksArtists artists){
        return new PlaylistTrackArtist(artists.name());
    }
}
