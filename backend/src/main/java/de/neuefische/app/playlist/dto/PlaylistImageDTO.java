package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.data.PlaylistImage;
import de.neuefische.app.spotify.playlistsearch.SpotifySearchPlaylistImages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistImageDTO {
    private String url;

    public static PlaylistImageDTO of(PlaylistImage image){
        return new PlaylistImageDTO(image.getUrl());

    }

    public static PlaylistImageDTO of(SpotifySearchPlaylistImages image){
        return new PlaylistImageDTO(image.url());

    }

}
