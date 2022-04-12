package de.neuefische.app.spotify;


import de.neuefische.app.security.JwtService;
import de.neuefische.app.spotify.playlistresponse.SpotifyRefreshToken;
import de.neuefische.app.user.UserDocument;
import de.neuefische.app.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Objects;


@Controller
@RequestMapping("/api/callback")
public class SpotifyCallbackController {

    private static final String ACCESS_TOKEN_URL = "https://accounts.spotify.com/api/token";

    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;
    private final String spotifyCallbackURL;
    private final UserService userService;
    private final JwtService jwtService;
    private final SpotifyRefreshToken refreshToken;
    private final SpotifyApiService spotifyApiService;

    public SpotifyCallbackController(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId,
                                     @Value("${spotify.client.secret}") String spotifyAuthSecret,
                                     @Value("${spotify.callback.url}") String spotifyCallbackURL,
                                     UserService userService, JwtService jwtService, SpotifyRefreshToken refreshToken,
                                     SpotifyApiService spotifyApiService) {
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshToken = refreshToken;
        this.spotifyApiService = spotifyApiService;
        this.spotifyCallbackURL = spotifyCallbackURL;
    }

    @GetMapping
    public String callbackUrl(@RequestParam String code, Model model)  {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", spotifyCallbackURL);
        HttpHeaders headers = createGetTokenHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse = restTemplate.exchange(
                ACCESS_TOKEN_URL,
                HttpMethod.POST,
                request,
                SpotifyGetAccessTokenBody.class
        );

        refreshToken.setRefreshToken(accessTokenResponse.getBody().refreshToken());
        UserDocument user= saveSpotifyUser(accessTokenResponse);
        if(!Objects.equals(user.getId(), null)){
            spotifyApiService.getSpotifyUserPlaylists(accessTokenResponse, user.getSpotifyId());
        }
        model.addAttribute("jwt", jwtService.createToken(new HashMap<>(), user.getSpotifyId()));

        return "oauth-landing";
    }


    HttpHeaders createGetTokenHeaders(){
        HttpHeaders header = new HttpHeaders();
            header.setBasicAuth(spotifyClientId,spotifyAuthSecret);
            return header;
    }

    private UserDocument saveSpotifyUser(ResponseEntity<SpotifyGetAccessTokenBody> accessTokenResponse){
        ResponseEntity<SpotifyGetUserBody> userResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/me",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessTokenResponse.getBody().accessToken())),
                SpotifyGetUserBody.class
        );

        return userService.saveUser(new UserDocument(userResponse.getBody().email(), userResponse.getBody().id(), userResponse.getBody().id(), userResponse.getBody().name(), null, null));
    }

    HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

}
