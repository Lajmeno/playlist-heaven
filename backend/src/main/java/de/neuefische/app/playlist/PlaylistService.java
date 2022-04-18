package de.neuefische.app.playlist;

import de.neuefische.app.playlist.data.PlaylistData;
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

    public PlaylistData overridePlaylist(PlaylistData playlistData) throws NoSuchFieldException {
        Optional<PlaylistData> playlistFromRepo = playlistRepository.findBySpotifyIdAndSpotifyUserId(playlistData.getSpotifyId(),  playlistData.getSpotifyUserId());
        if(playlistFromRepo.isPresent()){
            playlistData.setId(playlistFromRepo.get().getId());
            return playlistRepository.save(playlistData);
        }
        throw new NoSuchFieldException("Could not find requested playlist to override.");
    }

    public List<PlaylistData> getUserPlaylists(String spotifyUserId) {
        return playlistRepository.findBySpotifyUserId(spotifyUserId);
    }

    public Optional<PlaylistData> getPlaylistById(String id) {
        return playlistRepository.findBySpotifyId(id);
    }

    public Optional<PlaylistData> deletePlaylist(String spotifyId, String name) {
        Optional<PlaylistData> playlistData = playlistRepository.findBySpotifyIdAndSpotifyUserId(spotifyId, name);
        playlistData.ifPresent(playlist -> playlistRepository.delete(playlist));
        return playlistData;
    }
}
