package de.neuefische.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public UserDocument saveUser(UserDocument user) {
        return userRepo.save(user);
    }
}
