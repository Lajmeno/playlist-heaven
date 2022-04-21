package de.neuefische.app.playlist.dto;

import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.spotify.playlistsearch.SpotifySearchPlaylist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDTO {

    private String name;
    private String spotifyId;
    private List<PlaylistTrackDTO> tracks;
    private List<PlaylistImageDTO> images;
    private String spotifyOwnerId;

    public static PlaylistDTO of(PlaylistData playlistData){
        List<PlaylistTrackDTO> tracks = playlistData.getTracks().stream().map(track -> PlaylistTrackDTO.of(track)).toList();
        List<PlaylistImageDTO> images = playlistData.getImages().stream().map(image -> PlaylistImageDTO.of(image)).toList();
        return new PlaylistDTO(playlistData.getName(), playlistData.getSpotifyId(), tracks, images, playlistData.getSpotifyOwnerId());
    }

    public static PlaylistDTO of(SpotifySearchPlaylist spotifySearchPlaylist){
        List<PlaylistImageDTO> images = spotifySearchPlaylist.images().stream().map(image -> PlaylistImageDTO.of(image)).toList();
        return new PlaylistDTO(spotifySearchPlaylist.name(), spotifySearchPlaylist.id(), null, images, spotifySearchPlaylist.owner().id());

    }

}
