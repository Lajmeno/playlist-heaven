package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.PlaylistImage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistImageDTO {
    private String url;

    public static PlaylistImageDTO of(PlaylistImage image){
        return new PlaylistImageDTO(image.getUrl());

    }

}
