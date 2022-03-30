package de.neuefische.app.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public Optional<PlaylistDTO> savePlaylist(PlaylistData playlistData){
        Optional<PlaylistData> playlistFromRepo = playlistRepository.findBySpotifyId(playlistData.getSpotifyId());
        if(playlistFromRepo.equals(Optional.empty())){
            return Optional.of(PlaylistDTO.of(playlistRepository.save(playlistData)));
        }
        return Optional.empty();
    }
}
