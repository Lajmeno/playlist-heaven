package de.neuefische.app.security;

import de.neuefische.app.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MongoUserDetailsService implements UserDetailsService {

    private final UserService userService;


    @Override
    public UserDetails loadUserByUsername(String spotifyId) throws UsernameNotFoundException {
        return userService.getUser(spotifyId)
                .map(user -> new User(user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority(""))))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
