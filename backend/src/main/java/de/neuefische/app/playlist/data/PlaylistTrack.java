package de.neuefische.app.playlist.data;

import de.neuefische.app.playlist.dto.PlaylistTrackDTO;
import de.neuefische.app.spotify.playlistresponse.SpotifyPlaylistTrack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrack {

    private String title;
    private List<PlaylistTrackArtist> artists;
    private String album;
    private String albumReleaseDate;
    private String spotifyUri;


    public static PlaylistTrack of(SpotifyPlaylistTrack track){
        List<PlaylistTrackArtist> artists = track.artists().stream().map(t -> PlaylistTrackArtist.of(t)).toList();
        return new PlaylistTrack(track.name(), artists, track.album().name(), track.album().date(), track.uri());

    }

    public static PlaylistTrack of(PlaylistTrackDTO track){
        List<PlaylistTrackArtist> artists = track.getArtists().stream().map(t -> PlaylistTrackArtist.of(t)).toList();
        return new PlaylistTrack(track.getTitle(), artists, track.getAlbum(), track.getAlbumReleaseDate(), track.getSpotifyUri() );
    }

}
