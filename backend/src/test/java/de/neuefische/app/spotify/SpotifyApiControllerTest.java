package de.neuefische.app.spotify;

import de.neuefische.app.playlist.PlaylistRepository;
import de.neuefische.app.playlist.PlaylistService;
import de.neuefische.app.playlist.csv.PlaylistCSVService;
import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.data.PlaylistImage;
import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.security.JwtService;
import de.neuefische.app.spotify.oauth.SpotifyGetAccessTokenBody;
import de.neuefische.app.spotify.playlistresponse.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;


import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpotifyApiControllerTest {

    @MockBean
    private RestTemplate mockTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReloadPlaylistsFromSpotify(){
        PlaylistRepository playlistRepository = Mockito.mock(PlaylistRepository.class);
        String spotifyUserId = "00a";
        String spotifyPlaylistId = "00b";

        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = ResponseEntity.ok(new SpotifyGetAccessTokenBody("","","",0, ""));

        when(mockTemplate.exchange(
                Mockito.eq("https://accounts.spotify.com/api/token"),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(SpotifyGetAccessTokenBody.class)))
                .thenReturn(accessTokenResponse);


        List<SpotifyGetAllUserPlaylistsItem> items = List.of(new SpotifyGetAllUserPlaylistsItem("pl1", spotifyPlaylistId, List.of(new SpotifyPlaylistImages("image.url"))));
        ResponseEntity<SpotifyGetAllUserPlaylistsBody> allUserPlaylistResponse = ResponseEntity.ok(new SpotifyGetAllUserPlaylistsBody(2, "nextUrl", items ));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/users/" + spotifyUserId +"/playlists?limit=50&offset=0"),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyGetAllUserPlaylistsBody.class)))
                .thenReturn(allUserPlaylistResponse);

        SpotifyPlaylistTrack track = new SpotifyPlaylistTrack("track1", "001", new SpotifyTracksAlbum("album1", "01032020"), List.of(new SpotifyTracksArtist("artist1")), "xxx");
        SpotifyPlaylistTracks tracks = new SpotifyPlaylistTracks(List.of(new SpotifyGetPlaylistsItems("01012001", track)), 1, "next", 0);
        ResponseEntity<SpotifyGetPlaylistBody> playlistResponse = ResponseEntity.ok(new SpotifyGetPlaylistBody("pl1", spotifyPlaylistId, tracks, List.of(new SpotifyPlaylistImages("image.url")), new SpotifyPlaylistOwner(spotifyUserId)));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/playlists/" + spotifyPlaylistId),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyGetPlaylistBody.class)))
                .thenReturn(playlistResponse);

        when(playlistRepository.findBySpotifyId(spotifyPlaylistId)).thenReturn(Optional.empty());
        List<PlaylistTrack> playlistTracks = playlistResponse.getBody().tracks().items().stream()
                .map(item -> PlaylistTrack.of(item.track()))
                .toList();

        List<PlaylistImage> images = playlistResponse.getBody().images().stream()
                .map(image -> PlaylistImage.of(image))
                .toList();

        PlaylistData playlistData = new PlaylistData(null, playlistResponse.getBody().name(), playlistResponse.getBody().id(), playlistTracks, images, spotifyUserId, null);

        when(playlistRepository.save(playlistData)).thenReturn(playlistData);

        JwtService jwtService = new JwtService("my-super-duper-secret");
        String jwt = jwtService.createToken(new HashMap<>(), spotifyUserId);
        HttpHeaders authorizationHeader = new HttpHeaders();
        authorizationHeader.set("Authorization", "Bearer" + jwt);
        HttpEntity<String> httpEntityUser1Get = new HttpEntity<>(authorizationHeader);
        ResponseEntity<String> reloadPlaylistsResponse= restTemplate.exchange("/api/spotify", HttpMethod.GET, httpEntityUser1Get, String.class);

        assertThat(reloadPlaylistsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }



    @Autowired
    MockMvc mockMvc;



    @Test
    void shouldRestorePlaylistFromCSVUpload() throws Exception {

        PlaylistRepository playlistRepository = Mockito.mock(PlaylistRepository.class);
        PlaylistService playlistService = new PlaylistService(playlistRepository);
        SpotifyApiService spotifyApiService = new SpotifyApiService(mockTemplate, "", "", new SpotifyRefreshToken(), playlistService);
        PlaylistCSVService playlistCSVService = Mockito.mock(PlaylistCSVService.class);
        String spotifyUserId = "userID";
        String spotifyPlaylistId = "playlistID";

        //MultipartFile file = new MockMultipartFile("test.csv", new FileInputStream(new File("/...")));

        MockMultipartFile file = new MockMultipartFile("csv", "filename.csv", "text/csv", "text_csv".getBytes());
        //generate csv file for
        when(playlistCSVService.readCSV(file.getInputStream())).thenReturn(List.of("1"));


        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = ResponseEntity.ok(new SpotifyGetAccessTokenBody("","","",0, ""));

        when(mockTemplate.exchange(
                Mockito.eq("https://accounts.spotify.com/api/token"),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(SpotifyGetAccessTokenBody.class)))
                .thenReturn(accessTokenResponse);

        int[] intList = IntStream.rangeClosed(1,100).toArray();
        String list = Arrays.toString(intList);
        String[] uris = list.substring(1,list.length()-1).split(", ");
        List<SpotifyGetPlaylistsItems> items = new ArrayList<>();
        for(String uri: uris){
            SpotifyPlaylistTrack track = new SpotifyPlaylistTrack(uri, uri, new SpotifyTracksAlbum("album1", "01012001"), List.of(new SpotifyTracksArtist("artist1")), uri);
            SpotifyGetPlaylistsItems item = new SpotifyGetPlaylistsItems("0101", track);
            items.add(item);
        }

        SpotifyPlaylistTracks tracks = new SpotifyPlaylistTracks(items, 200, "https://api.spotify.com/v1/playlists/" + spotifyUserId +"/playlists?limit=100&offset=100", 0);
        ResponseEntity<SpotifyGetPlaylistBody> playlistResponse = ResponseEntity.ok(new SpotifyGetPlaylistBody("pl1", spotifyPlaylistId, tracks, List.of(new SpotifyPlaylistImages("image.url")), new SpotifyPlaylistOwner(spotifyUserId)));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/playlists/" + spotifyPlaylistId),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyGetPlaylistBody.class)))
                .thenReturn(playlistResponse);

        List<PlaylistTrack> playlistTracks = playlistResponse.getBody().tracks().items().stream()
                .map(item -> PlaylistTrack.of(item.track()))
                .toList();

        List<PlaylistImage> images = playlistResponse.getBody().images().stream()
                .map(image -> PlaylistImage.of(image))
                .toList();

        int[] intList2 = IntStream.rangeClosed(101,200).toArray();
        String list2 = Arrays.toString(intList2);
        String[] uris2 = list2.substring(1,list.length()-1).split(", ");
        List<SpotifyGetPlaylistsItems> items2 = new ArrayList<>();
        for(String uri: uris2){
            SpotifyPlaylistTrack track = new SpotifyPlaylistTrack(uri, uri, new SpotifyTracksAlbum("album1", "01012001"), List.of(new SpotifyTracksArtist("artist1")), uri);
            SpotifyGetPlaylistsItems item = new SpotifyGetPlaylistsItems("0101", track);
            items.add(item);
        }

        SpotifyPlaylistTracks tracks2 = new SpotifyPlaylistTracks(items2, 200, "https://api.spotify.com/v1/playlists/" + spotifyUserId +"/playlists?limit=100&offset=100", 0);
        ResponseEntity<SpotifyPlaylistTracks> playlistTracksResponseEntity = ResponseEntity.ok(tracks2);

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/playlists/" + spotifyUserId +"/playlists?limit=100&offset=100"),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyPlaylistTracks.class)))
                .thenReturn(playlistTracksResponseEntity);

        playlistTracks = Stream.concat(playlistTracks.stream(), playlistTracksResponseEntity.getBody().items().stream()
                        .filter(item -> item.track() != null)
                        .map(item -> PlaylistTrack.of(item.track())))
                .toList();



        PlaylistData playlistData = new PlaylistData(null, playlistResponse.getBody().name(), playlistResponse.getBody().id(), playlistTracks, images, spotifyUserId, null);


        when(playlistRepository.findBySpotifyIdAndSpotifyUserId(playlistData.getSpotifyId(), playlistData.getSpotifyUserId())).thenReturn(Optional.of(playlistData));
        when(playlistRepository.save(playlistData)).thenReturn(playlistData);

        /*
        Map<String, ?> params = new HashMap<>();
        params.put("msisdn", file);

        ResponseEntity<PlaylistDTO> restorePlaylistResponse= restTemplate.exchange(
                "/api/spotify/" + spotifyUserId + "/" + spotifyUserId,
                HttpMethod.PATCH,
                null,
                PlaylistDTO.class);

        */
        MockMultipartHttpServletRequestBuilder multipartRequest =
                MockMvcRequestBuilders.multipart("/api/spotify/" + spotifyUserId + "/" + spotifyUserId);

        mockMvc.perform(multipartRequest.file(file))
                .andExpect(status().isOk());



    }

}