package de.neuefische.app.playlist;

import de.neuefische.app.playlist.data.PlaylistData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PlaylistServiceTest {

    @Test
    void shouldNotSavePlaylistBecauseItAlreadyExists(){
        PlaylistData playlist1 = PlaylistData.builder().spotifyId("33").build();

        PlaylistRepository repository = Mockito.mock(PlaylistRepository.class);
        when(repository.findBySpotifyId("33")).thenReturn(Optional.of(playlist1));
        PlaylistService service = new PlaylistService(repository);

        Optional<PlaylistData> savedPlaylist = service.savePlaylist(playlist1);
        assertThat(savedPlaylist).isEqualTo(Optional.empty());
    }

    @Test
    void shouldDeletePlaylistBecauseFoundByRepo(){
        PlaylistData playlist1 = PlaylistData.builder().spotifyId("1").build();
        PlaylistRepository repository = Mockito.mock(PlaylistRepository.class);

        when(repository.findBySpotifyIdAndSpotifyUserId("1", "name")).thenReturn(Optional.of(playlist1));
        PlaylistService service = new PlaylistService(repository);

        Optional<PlaylistData> actual = service.deletePlaylist("1", "name");

        verify(repository, times(1)).delete(playlist1);
        assertThat(actual).isEqualTo(Optional.of(playlist1));
    }

    @Test
    void shouldNotOverrideNonExistingPlaylist(){
        PlaylistData playlist1 = PlaylistData.builder().spotifyId("1").spotifyUserId("2").build();
        PlaylistRepository repository = Mockito.mock(PlaylistRepository.class);
        PlaylistService service = new PlaylistService(repository);

        when(repository.findBySpotifyIdAndSpotifyUserId("1", "2")).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NoSuchFieldException.class, () -> service.overridePlaylist(playlist1));
        assertEquals("Could not find requested playlist to override.", exception.getMessage());
    }

}