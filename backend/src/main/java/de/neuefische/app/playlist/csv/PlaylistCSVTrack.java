package de.neuefische.app.playlist.csv;

import de.neuefische.app.playlist.dto.PlaylistTrackArtistDTO;
import de.neuefische.app.playlist.dto.PlaylistTrackDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistCSVTrack {

    private String title;
    private String artists;
    private String album;
    private String albumReleaseDate;
    private String spotifyUri;


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
        return new PlaylistCSVTrack(track.getTitle(), artistNames, track.getAlbum(), track.getAlbumReleaseDate(), track.getSpotifyUri());
    }

}
