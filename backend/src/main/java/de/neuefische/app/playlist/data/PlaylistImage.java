package de.neuefische.app.playlist.data;

import de.neuefische.app.playlist.dto.PlaylistDTO;
import de.neuefische.app.playlist.dto.PlaylistImageDTO;
import de.neuefische.app.spotify.playlistresponse.SpotifyPlaylistImages;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PlaylistImage {

    private String url;

    public static PlaylistImage of(SpotifyPlaylistImages image){
        return new PlaylistImage(image.url());

    }

    public static PlaylistImage of(PlaylistImageDTO image){
        return new PlaylistImage(image.getUrl());
    }
}
