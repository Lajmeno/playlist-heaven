package de.neuefische.app.playlist;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistRepository extends MongoRepository<PlaylistData, String> {

    Optional<PlaylistData> findBySpotifyId(String spotifyId);
}
