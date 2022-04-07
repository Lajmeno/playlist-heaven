package de.neuefische.app.playlist.csv;

import com.opencsv.bean.CsvBindByName;
import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.playlist.data.PlaylistTrackArtist;
import de.neuefische.app.playlist.dto.PlaylistTrackArtistDTO;
import de.neuefische.app.playlist.dto.PlaylistTrackDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PlaylistCSVTrack {


    @CsvBindByName(column = "1 Title")
    private String title;


    @CsvBindByName(column = "2 Artists")
    private String artists;


    @CsvBindByName(column = "3 Album")
    private String album;


    @CsvBindByName(column = "4 Release Date")
    private String albumReleaseDate;


    @CsvBindByName(column = "5 SpotifyUri")
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

    public PlaylistTrack toPlaylistTrack(){
        List<PlaylistTrackArtist> artistsList;
        if(!Objects.equals(artists, null)){
            List<String> names = Arrays.asList(artists.split(","));
            artistsList = names.stream().map(name -> new PlaylistTrackArtist(name)).toList();
        }else{
            artistsList = List.of(new PlaylistTrackArtist(null));
        }
        return new PlaylistTrack(title, artistsList, album, albumReleaseDate, spotifyUri);
    }

}
