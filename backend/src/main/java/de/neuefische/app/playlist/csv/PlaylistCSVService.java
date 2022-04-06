package de.neuefische.app.playlist.csv;


import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@AllArgsConstructor
public class PlaylistCSVService {

    public void writeToCSV(Writer writer, List<PlaylistCSVTrack> trackList){
        try {
            ColumnPositionMappingStrategy mappingStrategy= new ColumnPositionMappingStrategy();
            mappingStrategy.setType(PlaylistCSVTrack.class);

            String[] columns = new String[]{ "title", "artists", "album", "albumReleaseDate" };
            mappingStrategy.setColumnMapping(columns);

            StatefulBeanToCsvBuilder<PlaylistCSVTrack> builder= new StatefulBeanToCsvBuilder(writer);
            StatefulBeanToCsv beanWriter = builder
                    .withMappingStrategy(mappingStrategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(';')
                    .build();

            beanWriter.write(trackList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImportStatus readCSV(InputStream content){
        //LOGGER.info("user with id {} started CSV import", userId);
        try (Reader reader = new BufferedReader(new InputStreamReader(content))) {
            CsvToBean<PlaylistCSVTrack> csvToBean = new CsvToBeanBuilder<PlaylistCSVTrack>(reader)
                    .withType(PlaylistCSVTrack.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            /*
            itemRepository.saveAll(csvToBean.parse().stream()
                    .map(item -> item.toitem(userId))
                    .toList());

             */

            return ImportStatus.SUCCESS;
        } catch (IllegalStateException | IllegalArgumentException | IOException e) {
            //LOGGER.warn("csv could not be imported", e);
            return ImportStatus.FAILURE;
        }

    }

}
