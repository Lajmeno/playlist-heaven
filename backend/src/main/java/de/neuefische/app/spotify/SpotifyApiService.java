package de.neuefische.app.spotify;

import de.neuefische.app.playlist.PlaylistService;
import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.data.PlaylistImage;
import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.spotify.playlistresponse.*;
import de.neuefische.app.spotify.playlistsearch.PlaylistTracksRequest;
import de.neuefische.app.spotify.playlistsearch.SpotifySearchPlaylist;
import de.neuefische.app.spotify.playlistsearch.SpotifySearchPlaylistBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Stream;


@Service

public class SpotifyApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyApiService.class);

    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;
    private final SpotifyRefreshToken refreshToken;
    private final PlaylistService playlistService;

    public SpotifyApiService(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId,
                                @Value("${spotify.client.secret}") String spotifyAuthSecret,
                                SpotifyRefreshToken refreshToken, PlaylistService playlistService){
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
        this.refreshToken = refreshToken;
        this.playlistService = playlistService;
    }

    public void createNewSpotifyPlaylist(String title, List<String> uris, String spotifyUserId) {
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();

        String newPlaylistId = addNewPlaylist(accessTokenResponse.getBody(), title, spotifyUserId);

        addTracksToNewPlaylist(accessTokenResponse.getBody(), newPlaylistId, uris);
    }

    public Optional<PlaylistData> getPlaylistFromSpotify(String id){
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();
        try {
            PlaylistData playlistData = getPlaylistWithTracks(accessTokenResponse.getBody(), id);
            return Optional.of(playlistData);
        }catch (Exception e) {
            LOGGER.info("Playlist could not be found", e);
            return Optional.empty();
        }
    }

    public List<SpotifySearchPlaylist> searchPlaylists(String searchValue){
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();

        SpotifySearchPlaylistBody result = getSpotifySearchResult(accessTokenResponse.getBody(), searchValue).getBody();
        return result.playlists().items();
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

    public void reloadSpotifyPlaylists(String spotifyUserId) throws Exception {
        try{
            ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = getRefreshTokenFromSpotify();
            getSpotifyUserPlaylists(accessTokenResponse, spotifyUserId);
        }catch (Exception e) {
            LOGGER.info("Could not reload Playlists from Spotify Api", e);
            throw new Exception(e.getMessage());
        }

    }

    public void getSpotifyUserPlaylists(ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse, String spotifyUserId) {
        List<SpotifyGetAllUserPlaylistsItems> playlists = new ArrayList<>();
        boolean hasPlaylistsLeftToGet = true;
        int i = 0;
        while(hasPlaylistsLeftToGet){
            ResponseEntity<SpotifyGetAllUserPlaylistsBody> userPlaylistsResponse = restTemplate.exchange(
                    "https://api.spotify.com/v1/users/"+ spotifyUserId + "/playlists?limit=50&offset=" + (i * 50),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                    SpotifyGetAllUserPlaylistsBody.class
            );
            playlists.addAll(userPlaylistsResponse.getBody().items());
            hasPlaylistsLeftToGet = userPlaylistsResponse.getBody().total() > (i * 50) && !Objects.equals(userPlaylistsResponse.getBody().next(), null);
            i += 1;
        }

        for(SpotifyGetAllUserPlaylistsItems playlist : playlists){
            PlaylistData playlistData =getPlaylistWithTracks(accessTokenResponse.getBody(), playlist.id());
            playlistData.setSpotifyUserId(spotifyUserId);
            playlistService.savePlaylist(playlistData);
        }
    }



    public PlaylistData getPlaylistWithTracks(SpotifyGetAccessTokenBody accessTokenBody, String playlistId){
        ResponseEntity<SpotifyGetPlaylistBody> userPlaylistsTracksResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/playlists/" + playlistId,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenBody.accessToken())),
                SpotifyGetPlaylistBody.class
        );
        SpotifyGetPlaylistBody responseBody = userPlaylistsTracksResponse.getBody();

        List<PlaylistTrack> tracks = responseBody.tracks().items().stream()
                .filter(item -> item.track() != null)
                .map(item -> PlaylistTrack.of(item.track()))
                .toList();

        List<PlaylistImage> images = responseBody.images().stream()
                .map(image -> PlaylistImage.of(image))
                .toList();

        String urlForNextTracks = responseBody.tracks().next();
        boolean hasMoreTracksToGet = !Objects.equals(urlForNextTracks, null) && responseBody.tracks().total() > 100;
        while(hasMoreTracksToGet){
            ResponseEntity<SpotifyPlaylistTracks> userPlaylistsNextTracksResponse = restTemplate.exchange(
                    urlForNextTracks,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(accessTokenBody.accessToken())),
                    SpotifyPlaylistTracks.class
            );
            tracks = Stream.concat(tracks.stream(), userPlaylistsNextTracksResponse.getBody().items().stream().map(item -> PlaylistTrack.of(item.track()))).toList();
            SpotifyPlaylistTracks tracksForInfo = userPlaylistsNextTracksResponse.getBody();
            urlForNextTracks = userPlaylistsNextTracksResponse.getBody().next();
            hasMoreTracksToGet = !Objects.equals(tracksForInfo.next(), null) && (( tracksForInfo.total() - tracksForInfo.offset() ) >= 100);
        }
        return new PlaylistData(null, responseBody.name(), responseBody.id(), tracks, images, null);
    }

    private List<PlaylistTracksRequest> divideUris(List<String> uris){
        double n = Math.ceil(uris.size() / 100.0);
        int chunks = (int) Math.ceil(uris.size() / n);
        List<PlaylistTracksRequest> uriPartitions = new ArrayList<>();
        for (int i = 0; i < (int) n; i++) {
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

    private ResponseEntity<SpotifySearchPlaylistBody> getSpotifySearchResult(SpotifyGetAccessTokenBody accessTokenResponse, String searchValue){
        ResponseEntity<SpotifySearchPlaylistBody> userPlaylistsResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/search?q=" + searchValue +"&type=playlist&limit=20",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.accessToken())),
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
