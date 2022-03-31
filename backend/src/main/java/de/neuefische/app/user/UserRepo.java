package de.neuefische.app.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findBySpotifyId(String spotifyId);
}
