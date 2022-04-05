package de.neuefische.app.playlist.data;

import de.neuefische.app.playlist.dto.PlaylistTrackArtistDTO;
import de.neuefische.app.spotify.playlistresponse.SpotifyTracksArtist;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistTrackArtist {
    private String name;

    public static PlaylistTrackArtist of(SpotifyTracksArtist artist){
        return new PlaylistTrackArtist(artist.name());
    }

    public static PlaylistTrackArtist of(PlaylistTrackArtistDTO artist){
        return new PlaylistTrackArtist(artist.getName());
    }
}
