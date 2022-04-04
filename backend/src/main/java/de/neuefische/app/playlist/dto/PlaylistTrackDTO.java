package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.data.PlaylistTrack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackDTO {
    private String title;
    private List<PlaylistTrackArtistDTO> artists;
    private String album;
    private String albumReleaseDate;

    public static PlaylistTrackDTO of(PlaylistTrack track){
        List<PlaylistTrackArtistDTO> artists = track.getArtists().stream().map(a -> PlaylistTrackArtistDTO.of(a)).toList();
        return new PlaylistTrackDTO(track.getTitle(), artists, track.getAlbum(), track.getAlbumReleaseDate());
    }
}
