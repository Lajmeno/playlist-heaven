package de.neuefische.app.spotify;

import de.neuefische.app.playlist.csv.PlaylistCSVService;
import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.dto.PlaylistDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyApiController {


    private final PlaylistCSVService playlistCSVService;
    private final SpotifyApiService spotifyApiService;

    public SpotifyApiController(PlaylistCSVService playlistCSVService, SpotifyApiService spotifyApiService){
        this.playlistCSVService = playlistCSVService;
        this.spotifyApiService = spotifyApiService;
    }

    @PostMapping(value = "/{title}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaylistDTO> exportCSVToSpotify(@RequestParam("csv") MultipartFile file, @PathVariable String title, Principal principal) throws Exception {
        List<String> uris = playlistCSVService.readCSV(file.getInputStream());
        if(uris.size() > 0) {
            try {
                return ResponseEntity
                        .of(spotifyApiService.createNewSpotifyPlaylist(title, uris, principal.getName())
                        .map(playlistData -> PlaylistDTO.of(playlistData)));
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping(value = "/{id}/{ownerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaylistDTO> restorePlaylistFromCSV(@RequestParam("csv") MultipartFile file, @PathVariable String id, @PathVariable String ownerId, Principal principal) throws Exception {
        if(ownerId.equals(principal.getName())){
            List<String> uris = playlistCSVService.readCSV(file.getInputStream());
            if(uris.size()>0) {
                try {
                    return ResponseEntity
                            .of(spotifyApiService.restorePlaylist(id, uris, principal.getName())
                                    .map(playlistData -> PlaylistDTO.of(playlistData)));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistFromSpotify(@PathVariable String id){
        Optional<PlaylistData> playlistData = spotifyApiService.getPlaylistFromSpotify(id);
        return playlistData
                .map(data -> ResponseEntity.ok().body(PlaylistDTO.of(data)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build());
    }

    @GetMapping("/search/{value}")
    public List<PlaylistDTO> searchPlaylists(@PathVariable String value){
        return spotifyApiService.searchPlaylists(value).stream()
                .map(playlist -> PlaylistDTO.of(playlist)).toList();
    }

    @GetMapping
    public ResponseEntity<String> reloadSpotifyPlaylists(Principal principal){
        try{
            spotifyApiService.reloadSpotifyPlaylists(principal.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDTO> reloadPlaylistById(@PathVariable String id, Principal principal){
        try{
            return ResponseEntity
                    .ok()
                    .body(PlaylistDTO.of(spotifyApiService.reloadPlaylistFromSpotify(id, principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
