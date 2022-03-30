package de.neuefische.app;

import de.neuefische.app.playlist.PlaylistService;
import de.neuefische.app.playlist.dto.PlaylistDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/playlists")
@RequiredArgsConstructor
@CrossOrigin
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public List<PlaylistDTO> getUserPlaylists(){
        return playlistService.getPlaylists().stream().map(playlist -> PlaylistDTO.of(playlist)).toList();
    }

}
