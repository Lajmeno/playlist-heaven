package de.neuefische.app.playlist;

import de.neuefische.app.spotify.playlistresponse.SpotifyPlaylistTrack;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaylistTrack {

    private String title;
    private List<PlaylistTrackArtist> artists;
    private String album;
    private String albumReleaseDate;


    public static PlaylistTrack of(SpotifyPlaylistTrack track){
        List<PlaylistTrackArtist> artists = track.artists().stream().map(t -> PlaylistTrackArtist.of(t)).toList();
        return new PlaylistTrack(track.name(), artists, track.album().name(), track.album().date());
    }

}
