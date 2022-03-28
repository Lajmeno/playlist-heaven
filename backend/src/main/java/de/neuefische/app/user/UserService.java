package de.neuefische.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public void saveUser(UserDocument user) {
        userRepo.save(user);
    }
}
