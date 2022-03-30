package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.PlaylistTrack;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
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
