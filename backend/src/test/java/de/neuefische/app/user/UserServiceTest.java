package de.neuefische.app.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void shouldNotSaveUserBecauseAlreadyExistInDB(){
        UserDocument user = new UserDocument("user@mai.com", "1223", "userone", null, null, null);
        UserRepo repo = Mockito.mock(UserRepo.class);
        UserService service = new UserService(repo);

        when(repo.findBySpotifyId(user.getSpotifyId())).thenReturn(Optional.of(user));

        Assertions.assertThat(service.saveUser(user)).isSameAs(user);
    }

}