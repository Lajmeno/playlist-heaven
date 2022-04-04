package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.data.PlaylistImage;
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

}
