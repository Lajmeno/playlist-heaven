package de.neuefische.app.playlist;

import de.neuefische.app.playlist.data.PlaylistData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

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

}