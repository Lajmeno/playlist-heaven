package de.neuefische.app.spotify.playlistsearch;


import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.data.PlaylistImage;
import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.playlist.dto.PlaylistDTO;
import de.neuefische.app.spotify.SpotifyGetAccessTokenBody;
import de.neuefische.app.spotify.playlistresponse.SpotifyGetPlaylistBody;
import de.neuefische.app.spotify.playlistresponse.SpotifyRefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyApiController {

    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;
    private final SpotifyRefreshToken refreshToken;

    public SpotifyApiController(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId,
                                     @Value("${spotify.client.secret}") String spotifyAuthSecret,
                                SpotifyRefreshToken refreshToken){
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
        this.refreshToken = refreshToken;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistFromSpotify(@PathVariable String id){
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();

        ResponseEntity<SpotifyGetPlaylistBody> userPlaylistsTracksResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/playlists/"+id,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifyGetPlaylistBody.class
        );

        if(userPlaylistsTracksResponse.getStatusCode().value() == 200){
            SpotifyGetPlaylistBody responseBody = userPlaylistsTracksResponse.getBody();
            List<PlaylistTrack> tracks = responseBody.tracks().items().stream().map(item -> PlaylistTrack.of(item.track())).toList();
            List<PlaylistImage> images = responseBody.images().stream().map(image -> PlaylistImage.of(image)).toList();
            PlaylistData playlistData = new PlaylistData(null, responseBody.name(), responseBody.id(), tracks, images, null);
            return ResponseEntity.of(Optional.of(PlaylistDTO.of(playlistData)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
