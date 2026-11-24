package layover;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Reads routes.dat and builds the flight graph. */
public class RouteLoader {

    // Column positions in routes.dat (no header row). Columns 2 and 4 hold IATA codes.
    private static final int COL_SOURCE = 2;
    private static final int COL_DESTINATION = 4;
    private static final int MIN_COLUMNS = 5;

    public static Graph load(Path file, Map<String, Airport> airports) throws IOException {
        Graph graph = new Graph();

        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            List<String> f = CsvParser.parseLine(line);
            if (f.size() < MIN_COLUMNS) {
                continue;
            }
            String from = f.get(COL_SOURCE);
            String to = f.get(COL_DESTINATION);
            // Some routes use airports missing from airports.dat. Without coordinates
            // we can't measure them, so leave them out of the graph.
            if (airports.containsKey(from) && airports.containsKey(to)) {
                graph.addRoute(from, to);
            }
        }
        return graph;
    }
}
