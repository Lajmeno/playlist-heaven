package de.neuefische.app.spotify;

import de.neuefische.app.spotify.playlistresponse.SpotifyGetPlaylistBody;
import de.neuefische.app.spotify.playlistresponse.SpotifyRefreshToken;
import de.neuefische.app.spotify.playlistsearch.SpotifySearchPlaylistBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service

public class SpotifyApiService {
    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;
    private final SpotifyRefreshToken refreshToken;

    public SpotifyApiService(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId,
                                @Value("${spotify.client.secret}") String spotifyAuthSecret,
                                SpotifyRefreshToken refreshToken){
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
        this.refreshToken = refreshToken;
    }

    public void createNewSpotifyPlaylist(String title, List<String> uris, String spotifyUserId) {
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();

        String newPlaylistId = addNewPlaylist(accessTokenResponse.getBody(), title, spotifyUserId);

    }

    private String addNewPlaylist(SpotifyGetAccessTokenBody accessTokenResponse, String title, String userId){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", title);
        map.add("description", "New playlist description");
        //map.add("public", "false");
        HttpHeaders headers = createHeaders(accessTokenResponse.accessToken());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<SpotifyGetPlaylistBody> userPlaylistsResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/users/" + userId +"/playlists",
                HttpMethod.POST,
                request,
                SpotifyGetPlaylistBody.class
        );
        return userPlaylistsResponse.getBody().id();
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
