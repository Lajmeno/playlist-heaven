package de.neuefische.app.playlist.csv;


import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import de.neuefische.app.playlist.data.PlaylistTrack;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistCSVService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistCSVService.class);

    public void writeToCSV(Writer writer, List<PlaylistCSVTrack> trackList){
        try {
            StatefulBeanToCsvBuilder<PlaylistCSVTrack> builder= new StatefulBeanToCsvBuilder(writer);
            StatefulBeanToCsv beanWriter = builder
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(';')
                    .build();

            beanWriter.write(trackList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<List<String>> readCSV(InputStream content){
        try (Reader reader = new BufferedReader(new InputStreamReader(content))) {
            CsvToBean<PlaylistCSVTrack> csvToBean = new CsvToBeanBuilder<PlaylistCSVTrack>(reader)
                    .withType(PlaylistCSVTrack.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(';')
                    .build();

            List<String> uris = csvToBean.parse().stream()
                    .map(item -> item.toPlaylistTrack())
                    .map(item -> item.getSpotifyUri())
                    .toList();

            return Optional.of(uris);


        } catch (IllegalStateException | IllegalArgumentException | IOException e) {
            LOGGER.info("csv could not be imported", e);
            return Optional.empty();
        }
    }

}
