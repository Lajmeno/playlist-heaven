package de.neuefische.app.playlist.csv;

import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.playlist.data.PlaylistTrackArtist;
import de.neuefische.app.playlist.dto.PlaylistTrackArtistDTO;
import de.neuefische.app.playlist.dto.PlaylistTrackDTO;
import de.neuefische.app.spotify.playlistresponse.SpotifyPlaylistTrack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistCSVTrack {

    private String title;
    private String artists;
    private String album;
    private String albumReleaseDate;


    public static PlaylistCSVTrack of(PlaylistTrackDTO track){
        int num = track.getArtists().size();
        String artistNames = "";
        for(PlaylistTrackArtistDTO artist : track.getArtists()){
            if(num > 1){
                num--;
                artistNames += artist.getName() + ", ";
            }else {
                artistNames += artist.getName();
            }
        }
        return new PlaylistCSVTrack(track.getTitle(), artistNames, track.getAlbum(), track.getAlbumReleaseDate());
    }

}
