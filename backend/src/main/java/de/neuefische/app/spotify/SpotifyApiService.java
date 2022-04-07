package de.neuefische.app.spotify;

import de.neuefische.app.spotify.playlistresponse.SpotifyGetPlaylistBody;
import de.neuefische.app.spotify.playlistresponse.SpotifyRefreshToken;
import de.neuefische.app.spotify.playlistsearch.PlaylistTracksRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.expression.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        addTracksToNewPlaylist(accessTokenResponse.getBody(), newPlaylistId, uris);
    }

    private void addTracksToNewPlaylist(SpotifyGetAccessTokenBody accessTokenResponse, String playlistId, List<String> uris){
        List<PlaylistTracksRequest> uriPartitions = divideUris(uris);
        HttpHeaders headers = createHeaders(accessTokenResponse.accessToken());
        for(PlaylistTracksRequest uriChunks : uriPartitions){
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PlaylistTracksRequest> request = new HttpEntity<>(uriChunks, headers);
            ResponseEntity<SpotifyGetPlaylistBody> userPlaylistsResponse = restTemplate.exchange(
                    "https://api.spotify.com/v1/playlists/" + playlistId +"/tracks",
                    HttpMethod.POST,
                    request,
                    SpotifyGetPlaylistBody.class
            );
        }
    }

    private List<PlaylistTracksRequest> divideUris(List<String> uris){
        int n = (int)Math.ceil(uris.size()/100.0);
        int chunks = (int)Math.ceil(uris.size() / n);
        List<PlaylistTracksRequest> uriPartitions = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            uriPartitions.add(new PlaylistTracksRequest(uris.subList( i * chunks, Math.min(( i + 1 ) * chunks, uris.size()))));
        }
        return uriPartitions;
    }

    private String addNewPlaylist(SpotifyGetAccessTokenBody accessTokenResponse, String title, String userId){
        HttpHeaders headers = createHeaders(accessTokenResponse.accessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>("{\"name\":\"" + title + "\",\"description\":\"New playlist description\",\"public\":false}", headers);
        ResponseEntity<SpotifyGetPlaylistBody> userPlaylistsResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/users/" + userId +"/playlists",
                HttpMethod.POST,
                request,
                SpotifyGetPlaylistBody.class
        );
        return userPlaylistsResponse.getBody().id();
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

    private HttpHeaders createGetTokenHeaders(){
        HttpHeaders header = new HttpHeaders();
        header.setBasicAuth(spotifyClientId,spotifyAuthSecret);
        return header;
    }

    private HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }
}
