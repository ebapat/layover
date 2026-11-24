package layover;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The flight network. Each airport code maps to the set of airport codes
 * you can fly to directly from it.
 */
public class Graph {
    private final Map<String, Set<String>> destinations = new HashMap<>();

    public void addRoute(String from, String to) {
        // A Set means ten airlines flying the same pair still count as one connection.
        destinations.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }

    /** Airports reachable with one direct flight. Empty if the airport has no routes. */
    public Set<String> directDestinations(String airport) {
        return destinations.getOrDefault(airport, Collections.emptySet());
    }

    public int airportCount() {
        return destinations.size();
    }

    public int connectionCount() {
        int total = 0;
        for (Set<String> set : destinations.values()) {
            total += set.size();
        }
        return total;
    }
}
