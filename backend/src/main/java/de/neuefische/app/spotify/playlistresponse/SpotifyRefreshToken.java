package de.neuefische.app.spotify.playlistresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyRefreshToken {

    private String refreshToken = "";
}
