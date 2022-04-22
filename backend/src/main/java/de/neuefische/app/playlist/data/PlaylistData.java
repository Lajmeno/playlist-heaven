package de.neuefische.app.playlist.data;

import de.neuefische.app.playlist.dto.PlaylistDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("playlists")
@Data
@AllArgsConstructor
@Builder
public class PlaylistData {

    @Id
    private String id;
    private String name;
    private String spotifyId;
    private List<PlaylistTrack> tracks;
    private List<PlaylistImage> images;
    private String spotifyUserId;
    private String spotifyOwnerId;

    public static PlaylistData of(PlaylistDTO playlistDTO){
        List<PlaylistTrack> tracks = playlistDTO.getTracks().stream().map(track -> PlaylistTrack.of(track)).toList();
        List<PlaylistImage> images = playlistDTO.getImages().stream().map(image -> PlaylistImage.of(image)).toList();
        return new PlaylistData(null, playlistDTO.getName(), playlistDTO.getSpotifyId(), tracks, images, null, null);

    }

}
