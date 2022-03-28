package de.neuefische.app.user;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<UserDocument, String> {
}
