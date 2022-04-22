package de.neuefische.app.playlist;


import de.neuefische.app.security.JwtService;
import de.neuefische.app.spotify.oauth.SpotifyGetAccessTokenBody;
import de.neuefische.app.spotify.oauth.SpotifyGetUserBody;
import de.neuefische.app.spotify.playlistresponse.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.List;

import org.hamcrest.MatcherAssert;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaylistControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RestTemplate mockTemplate;

    @Test
    void shouldAddNewUserAfterCallback(){
        PlaylistRepository playlistRepository = Mockito.mock(PlaylistRepository.class);
        String spotifyUserId = "spotifyUserId";
        String spotifyPlaylist1Id = "pl-id-1";
        String spotifyPlaylist2Id = "pl-id-2";


        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = ResponseEntity.ok(new SpotifyGetAccessTokenBody("","","",0, ""));

        when(mockTemplate.exchange(
                Mockito.eq("https://accounts.spotify.com/api/token"),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(SpotifyGetAccessTokenBody.class)))
                .thenReturn(accessTokenResponse);


        ResponseEntity<SpotifyGetUserBody> getUserResponse = ResponseEntity.ok(new SpotifyGetUserBody(spotifyUserId, null, null));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/me"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(SpotifyGetUserBody.class)))
                .thenReturn(getUserResponse);



        SpotifyGetAllUserPlaylistsItem playlistsItem1 = new SpotifyGetAllUserPlaylistsItem("pl1", spotifyPlaylist1Id, List.of(new SpotifyPlaylistImages("image.url")));
        SpotifyGetAllUserPlaylistsItem playlistsItem2 = new SpotifyGetAllUserPlaylistsItem("pl2", spotifyPlaylist2Id, List.of(new SpotifyPlaylistImages("image.url")));
        List<SpotifyGetAllUserPlaylistsItem> items = List.of(playlistsItem1, playlistsItem2);
        ResponseEntity<SpotifyGetAllUserPlaylistsBody> allUserPlaylistResponse = ResponseEntity.ok(new SpotifyGetAllUserPlaylistsBody(2, "nextUrl", items ));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/users/" + spotifyUserId +"/playlists?limit=50&offset=0"),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyGetAllUserPlaylistsBody.class)))
                .thenReturn(allUserPlaylistResponse);

        SpotifyPlaylistTrack pl1Track1 = new SpotifyPlaylistTrack("track1", "001", new SpotifyTracksAlbum("album1", "01032020"), List.of(new SpotifyTracksArtist("artist1")), "xxx");
        SpotifyPlaylistTracks pl1Tracks = new SpotifyPlaylistTracks(List.of(new SpotifyGetPlaylistsItems("01012001", pl1Track1)), 1, "next", 0);
        ResponseEntity<SpotifyGetPlaylistBody> playlist1Response = ResponseEntity.ok(new SpotifyGetPlaylistBody("pl1", spotifyPlaylist1Id, pl1Tracks, List.of(new SpotifyPlaylistImages("image.url")), new SpotifyPlaylistOwner(spotifyUserId)));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/playlists/" + spotifyPlaylist1Id),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyGetPlaylistBody.class)))
                .thenReturn(playlist1Response);

        SpotifyPlaylistTrack pl2Track1 = new SpotifyPlaylistTrack("track1", "001", new SpotifyTracksAlbum("album1", "01032020"), List.of(new SpotifyTracksArtist("artist1")), "xxx");
        SpotifyPlaylistTracks pl2Tracks = new SpotifyPlaylistTracks(List.of(new SpotifyGetPlaylistsItems("01012001", pl2Track1)), 1, "next", 0);
        ResponseEntity<SpotifyGetPlaylistBody> playlist2Response = ResponseEntity.ok(new SpotifyGetPlaylistBody("pl2", spotifyPlaylist2Id, pl2Tracks, List.of(new SpotifyPlaylistImages("image.url")), new SpotifyPlaylistOwner(spotifyUserId)));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/playlists/" + spotifyPlaylist2Id),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyGetPlaylistBody.class)))
                .thenReturn(playlist2Response);


        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> callbackUrlResonse= restTemplate.exchange("/api/callback?code={code}", HttpMethod.GET,  entity, String.class, "code");


        JwtService jwtService = new JwtService("my-super-duper-secret");
        String jwt = jwtService.createToken(new HashMap<>(), spotifyUserId);
        HttpHeaders authorizationHeader = new HttpHeaders();
        authorizationHeader.set("Authorization", "Bearer" + jwt);
        HttpEntity<String> httpEntityGetPlaylists = new HttpEntity<>(authorizationHeader);
        ResponseEntity<List> getUserPlaylistsResponse= restTemplate.exchange("/api/playlists", HttpMethod.GET,  httpEntityGetPlaylists, List.class);

        assertThat(getUserPlaylistsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        //MatcherAssert.assertThat(getUserPlaylistsResponse.getBody().get(0), contains(hasProperty("name", is("pl1"))));
        System.out.println(getUserPlaylistsResponse.getBody());


    }

}