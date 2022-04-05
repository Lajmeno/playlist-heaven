package de.neuefische.app.playlist.data;

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

}
