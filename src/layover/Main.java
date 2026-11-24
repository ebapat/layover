package layover;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, Airport> airports = AirportLoader.load(Path.of("data/airports.dat"));
            System.out.println("Loaded " + airports.size() + " airports.");
            for (String code : new String[] {"IND", "SEA", "DFW"}) {
                System.out.println(airports.get(code));
            }
        } catch (IOException e) {
            System.err.println("Could not read data/airports.dat: " + e.getMessage());
            System.err.println("Run this from the repo root and make sure the file exists.");
            System.exit(1);
        }
    }
}
