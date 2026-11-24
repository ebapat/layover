package layover;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Finds routes through the flight graph. Every method returns the list of
 * airport codes from start to end, or an empty list if no route exists.
 */
public class RouteFinder {
    private final Graph graph;
    private final Map<String, Airport> airports;

    public RouteFinder(Graph graph, Map<String, Airport> airports) {
        this.graph = graph;
        this.airports = airports;
    }

    /**
     * Breadth-first search: explores airports in order of number of flights,
     * so the first time we reach the destination it is by the fewest flights.
     */
    public List<String> fewestConnections(String start, String end) {
        if (!airports.containsKey(start) || !airports.containsKey(end)) {
            return Collections.emptyList();
        }
        // cameFrom doubles as the "already visited" set.
        Map<String, String> cameFrom = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();
        cameFrom.put(start, null);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (current.equals(end)) {
                return buildPath(cameFrom, end);
            }
            for (String next : graph.directDestinations(current)) {
                if (!cameFrom.containsKey(next)) {
                    cameFrom.put(next, current);
                    queue.add(next);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Dijkstra's algorithm: always expands the airport with the smallest total
     * distance so far. Edge weights are haversine miles, which are never negative,
     * so the first time an airport is taken off the queue its distance is final.
     */
    public List<String> shortestDistance(String start, String end) {
        if (!airports.containsKey(start) || !airports.containsKey(end)) {
            return Collections.emptyList();
        }
        Map<String, Double> bestMiles = new HashMap<>();
        Map<String, String> cameFrom = new HashMap<>();
        // Java's PriorityQueue can't lower an entry's priority, so we add a new
        // entry each time we find a shorter way and skip stale ones when they come out.
        PriorityQueue<Stop> queue = new PriorityQueue<>((a, b) -> Double.compare(a.miles, b.miles));

        bestMiles.put(start, 0.0);
        cameFrom.put(start, null);
        queue.add(new Stop(start, 0.0));

        while (!queue.isEmpty()) {
            Stop current = queue.remove();
            if (current.miles > bestMiles.get(current.code)) {
                continue;
            }
            if (current.code.equals(end)) {
                return buildPath(cameFrom, end);
            }
            for (String next : graph.directDestinations(current.code)) {
                double miles = current.miles
                        + Distance.miles(airports.get(current.code), airports.get(next));
                if (miles < bestMiles.getOrDefault(next, Double.MAX_VALUE)) {
                    bestMiles.put(next, miles);
                    cameFrom.put(next, current.code);
                    queue.add(new Stop(next, miles));
                }
            }
        }
        return Collections.emptyList();
    }

    /** Total miles flown along a route returned by either search. */
    public double totalMiles(List<String> route) {
        double total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += Distance.miles(airports.get(route.get(i)), airports.get(route.get(i + 1)));
        }
        return total;
    }

    // Walk backwards from the end using cameFrom, then flip to get start-to-end order.
    private List<String> buildPath(Map<String, String> cameFrom, String end) {
        List<String> path = new ArrayList<>();
        for (String code = end; code != null; code = cameFrom.get(code)) {
            path.add(code);
        }
        Collections.reverse(path);
        return path;
    }

    private static class Stop {
        final String code;
        final double miles;

        Stop(String code, double miles) {
            this.code = code;
            this.miles = miles;
        }
    }
}
