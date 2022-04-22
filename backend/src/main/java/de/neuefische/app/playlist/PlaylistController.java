package de.neuefische.app.playlist;

import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.dto.PlaylistDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
@CrossOrigin
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public List<PlaylistDTO> getUserPlaylists(Principal principal){
        return playlistService.getUserPlaylists(principal.getName())
                .stream()
                .map(playlist -> PlaylistDTO.of(playlist))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable String id){
        return ResponseEntity.of(playlistService.getPlaylistById(id)
                        .map(playlistData -> PlaylistDTO.of(playlistData)));
    }

    @PostMapping
    public ResponseEntity<Void> savePlaylistForUser(@RequestBody PlaylistDTO playlist, Principal principal){
        PlaylistData playlistData = PlaylistData.of(playlist);
        playlistData.setSpotifyUserId(principal.getName());
        if(playlistService.savePlaylist(playlistData).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spotifyId}")
    public ResponseEntity<Void> deletePlaylistFromDB(@PathVariable String spotifyId, Principal principal){
        if(playlistService.deletePlaylist(spotifyId, principal.getName()).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

}
