package de.neuefische.app.playlist;

import de.neuefische.app.playlist.dto.PlaylistDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public Optional<PlaylistData> savePlaylist(PlaylistData playlistData){
        Optional<PlaylistData> playlistFromRepo = playlistRepository.findBySpotifyId(playlistData.getSpotifyId());
        if(playlistFromRepo.equals(Optional.empty())){
            return Optional.of(playlistRepository.save(playlistData));
        }
        return Optional.empty();
    }

    public List<PlaylistData> getPlaylists() {
        return playlistRepository.findAll();
    }

    public Optional<PlaylistData> getPlaylistById(String id) {
        return playlistRepository.findBySpotifyId(id);
    }
}
