package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.data.PlaylistData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaylistDTO {

    private String name;
    private String spotifyId;
    private List<PlaylistTrackDTO> tracks;
    private List<PlaylistImageDTO> images;

    public static PlaylistDTO of(PlaylistData playlistData){
        List<PlaylistTrackDTO> tracks = playlistData.getTracks().stream().map(track -> PlaylistTrackDTO.of(track)).toList();
        List<PlaylistImageDTO> images = playlistData.getImages().stream().map(image -> PlaylistImageDTO.of(image)).toList();
        return new PlaylistDTO(playlistData.getName(), playlistData.getSpotifyId(), tracks, images);
    }

}
