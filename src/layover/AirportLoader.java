package layover;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Reads airports.dat into a map keyed by 3-letter IATA code. */
public class AirportLoader {

    // Column positions in airports.dat (the file has no header row).
    private static final int COL_NAME = 1;
    private static final int COL_CITY = 2;
    private static final int COL_COUNTRY = 3;
    private static final int COL_IATA = 4;
    private static final int COL_LATITUDE = 6;
    private static final int COL_LONGITUDE = 7;
    private static final int MIN_COLUMNS = 8;

    public static Map<String, Airport> load(Path file) throws IOException {
        Map<String, Airport> airports = new HashMap<>();

        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            List<String> f = CsvParser.parseLine(line);
            if (f.size() < MIN_COLUMNS) {
                continue;
            }
            String iata = f.get(COL_IATA);
            // Skip airports with no IATA code: users and routes identify airports by it.
            if (iata.length() != 3) {
                continue;
            }
            try {
                double lat = Double.parseDouble(f.get(COL_LATITUDE));
                double lon = Double.parseDouble(f.get(COL_LONGITUDE));
                airports.put(iata, new Airport(iata, f.get(COL_NAME), f.get(COL_CITY),
                        f.get(COL_COUNTRY), lat, lon));
            } catch (NumberFormatException e) {
                // A bad coordinate means we can't compute distances, so skip the row.
                continue;
            }
        }
        return airports;
    }
}
