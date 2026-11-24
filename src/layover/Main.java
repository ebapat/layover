package layover;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, Airport> airports = AirportLoader.load(Path.of("data/airports.dat"));
            Graph graph = RouteLoader.load(Path.of("data/routes.dat"), airports);
            System.out.println("Loaded " + airports.size() + " airports.");
            System.out.println("Built graph: " + graph.airportCount() + " airports with flights, "
                    + graph.connectionCount() + " direct connections.");
            System.out.println("IND flies directly to " + graph.directDestinations("IND").size()
                    + " airports: " + graph.directDestinations("IND"));
        } catch (IOException e) {
            System.err.println("Could not read data files: " + e.getMessage());
            System.err.println("Run this from the repo root and make sure data/ has airports.dat and routes.dat.");
            System.exit(1);
        }
    }
}
