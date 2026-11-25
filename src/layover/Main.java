package layover;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, Airport> airports = AirportLoader.load(Path.of("data/airports.dat"));
            BucketList bucketList = BucketList.load(Path.of("data/bucket_list.csv"), airports);
            VisitedList visited = VisitedList.load(Path.of("data/visited.csv"));
            Config config = Config.load(Path.of("data/config.properties"));

            System.out.println("Home airport: " + config.getHomeAirport());
            System.out.println("Visited: " + visited.all());
            for (String name : Config.WEIGHT_NAMES) {
                System.out.println("weight " + name + " = " + config.getWeight(name));
            }
            System.out.println("Bucket list (" + bucketList.all().size() + " places):");
            for (Destination d : bucketList.all()) {
                System.out.println("  " + d.name + " via " + d.airportCode
                        + " (interest " + d.interest + ")");
            }
        } catch (IOException e) {
            System.err.println("Could not read data files: " + e.getMessage());
            System.err.println("Run this from the repo root and make sure the data/ folder exists.");
            System.exit(1);
        }
    }
}
