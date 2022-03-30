package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.PlaylistTrackArtist;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistTrackArtistDTO {

    private String name;

    public static PlaylistTrackArtistDTO of(PlaylistTrackArtist artist){
        return new PlaylistTrackArtistDTO(artist.getName());
    }
}
