package layover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Turns the bucket list into trip options by finding a route from home to each place. */
public class TripPlanner {

    public static List<TripOption> plan(String homeCode, BucketList bucketList, VisitedList visited,
                                        Map<String, Airport> airports, RouteFinder finder,
                                        TravelModel model) {
        List<TripOption> options = new ArrayList<>();
        if (!airports.containsKey(homeCode)) {
            System.err.println("Home airport '" + homeCode + "' is not a known airport code.");
            return options;
        }
        Set<String> knownCountries = countriesAlreadyKnown(homeCode, visited, airports);

        for (Destination destination : bucketList.all()) {
            if (visited.contains(destination.airportCode)) {
                System.err.println("Skipping " + destination.name + ": already visited.");
                continue;
            }
            if (destination.airportCode.equals(homeCode)) {
                System.err.println("Skipping " + destination.name + ": it is your home airport.");
                continue;
            }
            // Shortest miles, not fewest flights: cost and time both follow distance.
            List<String> route = finder.shortestDistance(homeCode, destination.airportCode);
            if (route.isEmpty()) {
                System.err.println("Skipping " + destination.name + ": no route found from " + homeCode + ".");
                continue;
            }
            String country = airports.get(destination.airportCode).country;
            options.add(new TripOption(destination, route, finder.totalMiles(route),
                    model.cost(route), model.hours(route), !knownCountries.contains(country)));
        }
        return options;
    }

    // Home counts as visited: going to your own country is not a "new country".
    private static Set<String> countriesAlreadyKnown(String homeCode, VisitedList visited,
                                                     Map<String, Airport> airports) {
        Set<String> countries = new HashSet<>();
        countries.add(airports.get(homeCode).country);
        for (String code : visited.all()) {
            Airport airport = airports.get(code);
            if (airport != null) {
                countries.add(airport.country);
            }
        }
        return countries;
    }
}
