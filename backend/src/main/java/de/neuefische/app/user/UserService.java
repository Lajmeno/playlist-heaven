package de.neuefische.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public UserDocument saveUser(UserDocument user) {
        return userRepo.save(user);
    }

    public Optional<UserDocument> getUser(String spotifyId){
        return userRepo.findBySpotifyId(spotifyId);
    }
}
