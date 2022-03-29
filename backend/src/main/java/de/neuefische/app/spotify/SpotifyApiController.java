package de.neuefische.app.spotify;

import de.neuefische.app.user.UserDocument;
import de.neuefische.app.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/callback")
public class SpotifyApiController {

    private static final String ACCESS_TOKEN_URL = "https://accounts.spotify.com/api/token";

    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;
    private final UserService userService;

    public SpotifyApiController(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId, @Value("${spotify.client.secret}") String spotifyAuthSecret, UserService userService) {
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
        this.userService = userService;
    }

    @GetMapping
    public void callbackUrl(@RequestParam String code)  {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", "http://localhost:8080/api/callback");
        HttpHeaders headers = createGetTokenHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<SpotifyResponse> accessTokenResponse = restTemplate.exchange(
                ACCESS_TOKEN_URL,
                HttpMethod.POST,
                request,
                SpotifyResponse.class
        );

        //saveSpotifyUser(accessTokenResponse);
        getSpotifyUserPlaylists(accessTokenResponse);
    }

    private void getSpotifyUserPlaylists(ResponseEntity<SpotifyResponse> accessTokenResponse) {
        ResponseEntity<SpotifyGetPlaylistResponse> userPlaylistsResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/me/playlists",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifyGetPlaylistResponse.class
        );

        for( SpotifyPlaylistItems playlist : userPlaylistsResponse.getBody().items()){
            ResponseEntity<SpotifyPlaylistItemsResponse> userPlaylistsTracksResponse = restTemplate.exchange(
                    "https://api.spotify.com/v1/playlists/"+playlist.id()+"/tracks",
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                    SpotifyPlaylistItemsResponse.class
            );
            SpotifyPlaylistItemsResponse reponseBody = userPlaylistsTracksResponse.getBody();
            List<SpotifyPlaylistTracks> tracks = reponseBody.tracks();
        }
    }

    HttpHeaders createGetTokenHeaders(){
        HttpHeaders header = new HttpHeaders();
            header.setBasicAuth(spotifyClientId,spotifyAuthSecret);
            return header;
    }

    private void saveSpotifyUser(ResponseEntity<SpotifyResponse> accessTokenResponse){
        ResponseEntity<SpotifyUser> userResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/me",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifyUser.class
        );

        userService.saveUser(new UserDocument(userResponse.getBody().email(), userResponse.getBody().id(), userResponse.getBody().name(), null, null));
    }

    HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

}
