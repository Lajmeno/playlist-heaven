package de.neuefische.app.playlist.csv;


import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;


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

    public List<String> readCSV(InputStream content) throws Exception {
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

            return uris;

        } catch (IllegalStateException | IllegalArgumentException | IOException e) {
            LOGGER.info("csv could not be imported", e);
            throw new Exception(e.getMessage());
        }
    }

}
