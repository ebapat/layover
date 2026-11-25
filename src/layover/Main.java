package layover;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, Airport> airports = AirportLoader.load(Path.of("data/airports.dat"));
            Graph graph = RouteLoader.load(Path.of("data/routes.dat"), airports);
            BucketList bucketList = BucketList.load(Path.of("data/bucket_list.csv"), airports);
            VisitedList visited = VisitedList.load(Path.of("data/visited.csv"));
            Config config = Config.load(Path.of("data/config.properties"));

            RouteFinder finder = new RouteFinder(graph, airports);
            TravelModel model = new TravelModel(config, airports);

            List<TripOption> options = TripPlanner.plan(config.getHomeAirport(), bucketList,
                    visited, airports, finder, model);
            List<ScoredTrip> ranked = Scorer.rank(options, config.getWeights());

            System.out.println("Trips from " + config.getHomeAirport() + ":");
            TripPrinter.printRanking(ranked);
        } catch (IOException e) {
            System.err.println("Could not read data files: " + e.getMessage());
            System.err.println("Run this from the repo root and make sure the data/ folder exists.");
            System.exit(1);
        }
    }
}