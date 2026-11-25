package layover;

import java.util.List;

/**
 * The raw facts about one possible trip, before any scoring.
 * Kept separate from the score so the same options can be re-ranked
 * with different weights without searching for routes again.
 */
public class TripOption {
    public final Destination destination;
    public final List<String> route;
    public final double miles;
    public final double cost;
    public final double hours;
    public final int connections;   // stops in between, so a nonstop flight is 0
    public final boolean newCountry;

    public TripOption(Destination destination, List<String> route, double miles,
                      double cost, double hours, boolean newCountry) {
        this.destination = destination;
        this.route = route;
        this.miles = miles;
        this.cost = cost;
        this.hours = hours;
        this.connections = route.size() - 2;
        this.newCountry = newCountry;
    }
}
