package de.neuefische.app;

import de.neuefische.app.playlist.PlaylistService;
import de.neuefische.app.playlist.dto.PlaylistDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/playlists")
@RequiredArgsConstructor
@CrossOrigin
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public List<PlaylistDTO> getUserPlaylists(){
        return playlistService.getPlaylists().stream().map(playlist -> PlaylistDTO.of(playlist)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable String id){
        return ResponseEntity.of(playlistService.getPlaylistById(id)
                        .map(playlistData -> PlaylistDTO.of(playlistData)));
    }

}
