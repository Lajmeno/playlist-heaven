package de.neuefische.app.playlist.csv;


import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import de.neuefische.app.playlist.dto.PlaylistTrackDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Writer;
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

}
