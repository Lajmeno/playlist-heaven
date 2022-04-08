package de.neuefische.app.spotify;

import de.neuefische.app.playlist.csv.PlaylistCSVService;
import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.dto.PlaylistDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<Void> exportCSVToSpotify(@RequestParam("csv") MultipartFile file, @PathVariable String title, Principal principal) throws IOException {
        Optional<List<String>> uris = playlistCSVService.readCSV(file.getInputStream());
        if(uris.isPresent()){
            spotifyApiService.createNewSpotifyPlaylist(title, uris.get(), principal.getName());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistFromSpotify(@PathVariable String id){
        Optional<PlaylistData> playlistData = spotifyApiService.getPlaylistFromSpotify(id);
        return playlistData.map(data -> ResponseEntity.ok().body(PlaylistDTO.of(data))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search/{value}")
    public List<PlaylistDTO> searchPlaylists(@PathVariable String value){
        return spotifyApiService.searchPlaylists(value).stream().map(playlist -> PlaylistDTO.of(playlist)).toList();
    }

}
