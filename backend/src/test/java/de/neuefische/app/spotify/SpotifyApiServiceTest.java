package de.neuefische.app.spotify;

import de.neuefische.app.playlist.PlaylistRepository;
import de.neuefische.app.playlist.PlaylistService;
import de.neuefische.app.playlist.data.PlaylistData;
import de.neuefische.app.playlist.data.PlaylistImage;
import de.neuefische.app.playlist.data.PlaylistTrack;
import de.neuefische.app.spotify.oauth.SpotifyGetAccessTokenBody;
import de.neuefische.app.spotify.playlistresponse.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpotifyApiServiceTest {

    @MockBean
    private RestTemplate mockTemplate;

    @Test
    void shouldCreateNewSpotifyPlaylist() throws Exception {
        PlaylistRepository playlistRepository = Mockito.mock(PlaylistRepository.class);
        PlaylistService playlistService = new PlaylistService(playlistRepository);
        SpotifyApiService spotifyApiService = new SpotifyApiService(mockTemplate, "", "", new SpotifyRefreshToken(), playlistService);
        String spotifyUserId = "00a";
        String spotifyPlaylistId = "00b";

        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = ResponseEntity.ok(new SpotifyGetAccessTokenBody("","","",0, ""));

        when(mockTemplate.exchange(
                Mockito.eq("https://accounts.spotify.com/api/token"),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(SpotifyGetAccessTokenBody.class)))
        .thenReturn(accessTokenResponse);


        SpotifyPlaylistTrack track = new SpotifyPlaylistTrack("track1", "001", new SpotifyTracksAlbum("album1", "01032020"), List.of(new SpotifyTracksArtist("artist1")), "xxx");
        SpotifyPlaylistTracks tracks = new SpotifyPlaylistTracks(List.of(new SpotifyGetPlaylistsItems("01012001", track)), 1, "next", 0);
        ResponseEntity<SpotifyGetPlaylistBody> playlistResponse = ResponseEntity.ok(new SpotifyGetPlaylistBody("pl1", spotifyPlaylistId, tracks, List.of(new SpotifyPlaylistImages("image.url")), new SpotifyPlaylistOwner(spotifyUserId)));

        when(mockTemplate.exchange(
                Mockito.eq("https://api.spotify.com/v1/users/" + spotifyUserId +"/playlists"),
                Mockito.<HttpMethod> any(),
                Mockito.<HttpEntity<Object>> any(),
                Mockito.eq(SpotifyGetPlaylistBody.class)))
        .thenReturn(playlistResponse);

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

        PlaylistData playlistData = new PlaylistData(null, playlistResponse.getBody().name(), playlistResponse.getBody().id(), playlistTracks, images, spotifyUserId, spotifyUserId);

        when(playlistRepository.save(playlistData)).thenReturn(playlistData);

        assertThat(spotifyApiService.createNewSpotifyPlaylist("pl 1", List.of("1", "2"), spotifyUserId)).isEqualTo(Optional.of(playlistData));

    }


}