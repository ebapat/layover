package layover;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ranks trips with a weighted sum. Cost, time, and connections are measured in
 * different units, so each is first scaled to 0-1 (1 = best) and then combined:
 * score = 100 * sum(weight * factor) / sum(weights).
 */
public class Scorer {

    public static List<ScoredTrip> rank(List<TripOption> options, Map<String, Double> weights) {
        List<ScoredTrip> ranked = new ArrayList<>();
        if (options.isEmpty()) {
            return ranked;
        }
        double minCost = Double.MAX_VALUE, maxCost = 0;
        double minHours = Double.MAX_VALUE, maxHours = 0;
        double minConnections = Double.MAX_VALUE, maxConnections = 0;
        for (TripOption o : options) {
            minCost = Math.min(minCost, o.cost);
            maxCost = Math.max(maxCost, o.cost);
            minHours = Math.min(minHours, o.hours);
            maxHours = Math.max(maxHours, o.hours);
            minConnections = Math.min(minConnections, o.connections);
            maxConnections = Math.max(maxConnections, o.connections);
        }

        double totalWeight = 0;
        for (String name : Config.WEIGHT_NAMES) {
            totalWeight += weights.getOrDefault(name, 0.0);
        }

        for (TripOption o : options) {
            Map<String, Double> factors = new LinkedHashMap<>();
            factors.put("cost", lowerIsBetter(o.cost, minCost, maxCost));
            factors.put("time", lowerIsBetter(o.hours, minHours, maxHours));
            factors.put("connections", lowerIsBetter(o.connections, minConnections, maxConnections));
            factors.put("new_countries", o.newCountry ? 1.0 : 0.0);
            factors.put("interest", interestFactor(o.destination.interest));

            double weighted = 0;
            for (String name : Config.WEIGHT_NAMES) {
                weighted += weights.getOrDefault(name, 0.0) * factors.get(name);
            }
            // If every weight is 0 there is nothing to rank by, so every trip scores 0.
            double score = totalWeight > 0 ? 100 * weighted / totalWeight : 0;
            ranked.add(new ScoredTrip(o, score, factors));
        }

        // Name as tie-breaker so equal scores always print in the same order.
        ranked.sort(Comparator.comparingDouble((ScoredTrip t) -> t.score).reversed()
                .thenComparing(t -> t.option.destination.name));
        return ranked;
    }

    // Cheapest/shortest trip gets 1, most expensive/longest gets 0.
    // If every trip is equal there is nothing to compare, so none is penalised.
    private static double lowerIsBetter(double value, double min, double max) {
        if (max == min) {
            return 1.0;
        }
        return (max - value) / (max - min);
    }

    // Interest is already on a fixed 1-10 scale, so it maps to 0-1 directly.
    private static double interestFactor(int interest) {
        return (interest - BucketList.MIN_INTEREST)
                / (double) (BucketList.MAX_INTEREST - BucketList.MIN_INTEREST);
    }
}
