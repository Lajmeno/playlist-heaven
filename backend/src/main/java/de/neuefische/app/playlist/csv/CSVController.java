package de.neuefische.app.playlist.csv;

import de.neuefische.app.playlist.dto.PlaylistDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
@RequestMapping("/api/csv")
@AllArgsConstructor
public class CSVController {

    private final PlaylistCSVService playlistCSVService;

    @PostMapping
    public void downloadAsCSV(@RequestBody PlaylistDTO playlist , HttpServletResponse servletResponse) throws IOException{
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"Playlist.csv\"");
        playlistCSVService.writeToCSV(servletResponse.getWriter(),playlist.getTracks());
    }

}
