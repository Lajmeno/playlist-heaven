package de.neuefische.app.spotify.playlistsearch;


import de.neuefische.app.playlist.dto.PlaylistDTO;
import de.neuefische.app.spotify.SpotifyGetAccessTokenResponse;
import de.neuefische.app.spotify.playlistresponse.SpotifyRefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    @GetMapping("/search/{value}")
    public List<PlaylistDTO> searchPlaylists(@PathVariable String value){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken.getRefreshToken());
        HttpHeaders headers = createGetTokenHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<SpotifyGetAccessTokenResponse> accessTokenResponse = restTemplate.exchange(
                "https://accounts.spotify.com/api/token",
                HttpMethod.POST,
                request,
                SpotifyGetAccessTokenResponse.class
        );

        SpotifySearchPlaylistResponse result = getSpotifySearchResult(accessTokenResponse, value).getBody();
        return result.playlists().items().stream().map(playlist -> PlaylistDTO.of(playlist)).toList();
    }

    private ResponseEntity<SpotifySearchPlaylistResponse> getSpotifySearchResult(ResponseEntity<SpotifyGetAccessTokenResponse> accessTokenResponse, String searchValue){
        ResponseEntity<SpotifySearchPlaylistResponse> userPlaylistsResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/search?q=" + searchValue +"&type=playlist&limit=20",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifySearchPlaylistResponse.class
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
