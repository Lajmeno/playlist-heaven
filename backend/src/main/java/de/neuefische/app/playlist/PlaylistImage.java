package de.neuefische.app.playlist;

import de.neuefische.app.spotify.playlistresponse.SpotifyPlaylistImages;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PlaylistImage {

    private String url;

    public static PlaylistImage of(SpotifyPlaylistImages images){
        return new PlaylistImage(images.url());

    }
}
