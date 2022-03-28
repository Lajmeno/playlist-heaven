package de.neuefische.app.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/callback")
public class SpotifyApiController {

    private static final String ACCESS_TOKEN_URL = "https://accounts.spotify.com/api/token";

    private final RestTemplate restTemplate;
    private final String spotifyClientId;
    private final String spotifyAuthSecret;

    public SpotifyApiController(RestTemplate restTemplate, @Value("${spotify.client.id}") String spotifyClientId, @Value("${spotify.client.secret}") String spotifyAuthSecret) {
        this.restTemplate = restTemplate;
        this.spotifyClientId = spotifyClientId;
        this.spotifyAuthSecret = spotifyAuthSecret;
    }

    @GetMapping
    public void callbackUrl(@RequestParam String code)  {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", "http://localhost:8080/api/callback");
        HttpHeaders headers = createGetTokenHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<SpotifyResponse> accessTokenResponse = restTemplate.exchange(
                ACCESS_TOKEN_URL,
                HttpMethod.POST,
                request,
                SpotifyResponse.class
        );
    }

    HttpHeaders createGetTokenHeaders(){
        HttpHeaders header = new HttpHeaders();
            header.setBasicAuth(spotifyClientId,spotifyAuthSecret);
            return header;
        }


}
