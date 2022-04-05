package de.neuefische.app.spotify;

import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.data.PlaylistImage;
import de.neuefische.app.playlist.PlaylistService;
import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.security.JwtService;
import de.neuefische.app.spotify.playlistresponse.SpotifyGetAllUserPlaylistsBody;
import de.neuefische.app.spotify.playlistresponse.SpotifyGetPlaylistBody;
import de.neuefische.app.spotify.playlistresponse.SpotifyGetAllUserPlaylistsItems;
import de.neuefische.app.spotify.playlistresponse.SpotifyRefreshToken;
import de.neuefische.app.user.UserDocument;
import de.neuefische.app.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/api/callback")
public class SpotifyCallbackController {

    private static final String ACCESS_TOKEN_URL = "https://accounts.spotify.com/api/token";

    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;
    private final UserService userService;
    private final PlaylistService playlistService;
    private final JwtService jwtService;
    private final SpotifyRefreshToken refreshToken;

    public SpotifyCallbackController(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId,
                                     @Value("${spotify.client.secret}") String spotifyAuthSecret, UserService userService,
                                     PlaylistService playlistService, JwtService jwtService, SpotifyRefreshToken refreshToken) {
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
        this.userService = userService;
        this.playlistService= playlistService;
        this.jwtService = jwtService;
        this.refreshToken = refreshToken;
    }

    @GetMapping
    public String callbackUrl(@RequestParam String code, Model model)  {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", "http://localhost:8080/api/callback");
        HttpHeaders headers = createGetTokenHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = restTemplate.exchange(
                ACCESS_TOKEN_URL,
                HttpMethod.POST,
                request,
                SpotifyGetAccessTokenBody.class
        );
        refreshToken.setRefreshToken(accessTokenResponse.getBody().refreshToken());
        UserDocument user= saveSpotifyUser(accessTokenResponse);
        getSpotifyUserPlaylists(accessTokenResponse, user);

        model.addAttribute("jwt", jwtService.createToken(new HashMap<>(), user.getSpotifyId()));

        return "oauth-landing";
    }

    private void getSpotifyUserPlaylists(ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse, UserDocument user) {
        ResponseEntity<SpotifyGetAllUserPlaylistsBody> userPlaylistsResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/me/playlists?limit=2&offset=20",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifyGetAllUserPlaylistsBody.class
        );

        for(SpotifyGetAllUserPlaylistsItems playlist : userPlaylistsResponse.getBody().items()){
            ResponseEntity<SpotifyGetPlaylistBody> userPlaylistsTracksResponse = restTemplate.exchange(
                    "https://api.spotify.com/v1/playlists/"+playlist.id(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                    SpotifyGetPlaylistBody.class
            );
            SpotifyGetPlaylistBody responseBody = userPlaylistsTracksResponse.getBody();
            List<PlaylistTrack> tracks = responseBody.tracks().items().stream().map(item -> PlaylistTrack.of(item.track())).toList();
            List<PlaylistImage> images = responseBody.images().stream().map(image -> PlaylistImage.of(image)).toList();
            PlaylistData playlistData = new PlaylistData(null, responseBody.name(), responseBody.id(), tracks, images, user.getSpotifyId());
            playlistService.savePlaylist(playlistData);
        }
    }

    HttpHeaders createGetTokenHeaders(){
        HttpHeaders header = new HttpHeaders();
            header.setBasicAuth(spotifyClientId,spotifyAuthSecret);
            return header;
    }

    private UserDocument saveSpotifyUser(ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse){
        ResponseEntity<SpotifyGetUserBody> userResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/me",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifyGetUserBody.class
        );

        return userService.saveUser(new UserDocument(userResponse.getBody().email(), userResponse.getBody().id(), userResponse.getBody().name(), null, null));
    }

    HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

}
