package de.neuefische.app.playlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaylistDTO {

    private String name;
    private String spotifyId;
    private List<PlaylistTrack> tracks;
    private List<PlaylistImage> images;

    public static PlaylistDTO of(PlaylistData playlistData){
        return new PlaylistDTO(playlistData.getName(), playlistData.getSpotifyId(), playlistData.getTracks(), playlistData.getImages());
    }

}
