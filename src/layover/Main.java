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
            RouteFinder finder = new RouteFinder(graph, airports);

            String[][] pairs = {{"IND", "ORD"}, {"IND", "HNL"}, {"IND", "JNU"}, {"IND", "AAA"}, {"IND", "XXX"}};
            for (String[] pair : pairs) {
                printRoutes(finder, pair[0], pair[1]);
            }
        } catch (IOException e) {
            System.err.println("Could not read data files: " + e.getMessage());
            System.err.println("Run this from the repo root and make sure data/ has airports.dat and routes.dat.");
            System.exit(1);
        }
    }

    private static void printRoutes(RouteFinder finder, String from, String to) {
        System.out.println(from + " -> " + to);
        printRoute("  fewest flights ", finder, finder.fewestConnections(from, to));
        printRoute("  shortest miles ", finder, finder.shortestDistance(from, to));
    }

    private static void printRoute(String label, RouteFinder finder, List<String> route) {
        if (route.isEmpty()) {
            System.out.println(label + ": no route found");
        } else {
            System.out.printf("%s: %s (%.0f miles)%n", label, String.join(" > ", route), finder.totalMiles(route));
        }
    }
}
