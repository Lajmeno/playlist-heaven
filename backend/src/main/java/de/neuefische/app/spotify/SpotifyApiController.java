package de.neuefische.app.spotify;


import de.neuefische.app.playlist.csv.ImportStatus;
import de.neuefische.app.playlist.csv.PlaylistCSVService;
import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.data.PlaylistImage;
import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.playlist.dto.PlaylistDTO;
import de.neuefische.app.spotify.playlistresponse.SpotifyGetPlaylistBody;
import de.neuefische.app.spotify.playlistresponse.SpotifyRefreshToken;
import de.neuefische.app.spotify.playlistsearch.SpotifySearchPlaylistBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyApiController.class);

    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;
    private final SpotifyRefreshToken refreshToken;
    private final PlaylistCSVService playlistCSVService;

    public SpotifyApiController(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId,
                                @Value("${spotify.client.secret}") String spotifyAuthSecret,
                                SpotifyRefreshToken refreshToken, PlaylistCSVService playlistCSVService){
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
        this.refreshToken = refreshToken;
        this.playlistCSVService = playlistCSVService;
    }

    @PostMapping(value = "/{title}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> exportCSVToSpotify(@RequestParam("csv") MultipartFile file, @PathVariable String title) throws IOException {
        ImportStatus importStatus = playlistCSVService.readCSV(file.getInputStream());
        if (importStatus == ImportStatus.SUCCESS) {
            return ResponseEntity.ok().build();
        } else if (importStatus == ImportStatus.PARTIAL) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistFromSpotify(@PathVariable String id){
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();

        try {
            ResponseEntity<SpotifyGetPlaylistBody> userPlaylistsTracksResponse = restTemplate.exchange(
                    "https://api.spotify.com/v1/playlists/" + id,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                    SpotifyGetPlaylistBody.class
            );
            SpotifyGetPlaylistBody responseBody = userPlaylistsTracksResponse.getBody();
            List<PlaylistTrack> tracks = responseBody.tracks().items().stream().map(item -> PlaylistTrack.of(item.track())).toList();
            List<PlaylistImage> images = responseBody.images().stream().map(image -> PlaylistImage.of(image)).toList();
            PlaylistData playlistData = new PlaylistData(null, responseBody.name(), responseBody.id(), tracks, images, null);
            return ResponseEntity.ok().body(PlaylistDTO.of(playlistData));
        }catch (Exception e) {
            LOGGER.info("Playlist could not be found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/search/{value}")
    public List<PlaylistDTO> searchPlaylists(@PathVariable String value){
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();

        SpotifySearchPlaylistBody result = getSpotifySearchResult(accessTokenResponse, value).getBody();
        return result.playlists().items().stream().map(playlist -> PlaylistDTO.of(playlist)).toList();
    }

    private ResponseEntity<SpotifyGetAccessTokenBody> getRefreshTokenFromSpotify(){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken.getRefreshToken());
        HttpHeaders headers = createGetTokenHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.exchange(
                "https://accounts.spotify.com/api/token",
                HttpMethod.POST,
                request,
                SpotifyGetAccessTokenBody.class
        );
    }

    private ResponseEntity<SpotifySearchPlaylistBody> getSpotifySearchResult(ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse, String searchValue){
        ResponseEntity<SpotifySearchPlaylistBody> userPlaylistsResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/search?q=" + searchValue +"&type=playlist&limit=20",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifySearchPlaylistBody.class
        );
        return userPlaylistsResponse;

    }

    HttpHeaders createGetTokenHeaders(){
        HttpHeaders header = new HttpHeaders();
        header.setBasicAuth(spotifyClientId,spotifyAuthSecret);
        return header;
    }

    HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

}
