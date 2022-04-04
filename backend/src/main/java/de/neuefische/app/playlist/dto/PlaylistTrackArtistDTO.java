package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.data.PlaylistTrackArtist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackArtistDTO {

    private String name;

    public static PlaylistTrackArtistDTO of(PlaylistTrackArtist artist){
        return new PlaylistTrackArtistDTO(artist.getName());
    }
}
