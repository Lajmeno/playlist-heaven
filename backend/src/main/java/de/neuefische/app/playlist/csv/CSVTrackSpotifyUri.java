package de.neuefische.app.playlist.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CSVTrackSpotifyUri {

    @CsvBindByName
    private String spotifyUri;


    public CSVTrackSpotifyUri toSpotifyUri(){
        return new CSVTrackSpotifyUri(spotifyUri);
    }
}
