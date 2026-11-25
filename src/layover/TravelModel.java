package layover;

import java.util.List;
import java.util.Map;

/**
 * Estimates what a route costs and how long it takes. These are rough estimates
 * from distance, not real fares: there is no free, reliable flight price source.
 * Every number can be overridden in config.properties.
 */
public class TravelModel {
    // Price bands are applied like tax brackets: each band's rate only covers
    // the miles inside that band, so the price never jumps at a boundary.
    private static final double SHORT_BAND_END = 500;
    private static final double MEDIUM_BAND_END = 1500;

    private static final double DEFAULT_BASE_FEE = 50;
    private static final double DEFAULT_RATE_SHORT = 0.25;
    private static final double DEFAULT_RATE_MEDIUM = 0.12;
    private static final double DEFAULT_RATE_LONG = 0.08;
    private static final double DEFAULT_SPEED_MPH = 500;
    private static final double DEFAULT_LAYOVER_HOURS = 1.5;

    private final Map<String, Airport> airports;
    private final double baseFee;
    private final double rateShort;
    private final double rateMedium;
    private final double rateLong;
    private final double speedMph;
    private final double layoverHours;

    public TravelModel(Config config, Map<String, Airport> airports) {
        this.airports = airports;
        this.baseFee = config.getSetting("cost.base_fee", DEFAULT_BASE_FEE);
        this.rateShort = config.getSetting("cost.rate_short", DEFAULT_RATE_SHORT);
        this.rateMedium = config.getSetting("cost.rate_medium", DEFAULT_RATE_MEDIUM);
        this.rateLong = config.getSetting("cost.rate_long", DEFAULT_RATE_LONG);
        // A speed of 0 would divide by zero below, so only accept positive speeds.
        double speed = config.getSetting("time.speed_mph", DEFAULT_SPEED_MPH);
        this.speedMph = speed > 0 ? speed : DEFAULT_SPEED_MPH;
        this.layoverHours = config.getSetting("time.layover_hours", DEFAULT_LAYOVER_HOURS);
    }

    /** Cost of the whole route. Each flight pays the base fee, so extra stops cost more. */
    public double cost(List<String> route) {
        double total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += flightCost(legMiles(route, i));
        }
        return total;
    }

    /** Hours in the air plus a layover wait for every stop in between. */
    public double hours(List<String> route) {
        double flying = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            flying += legMiles(route, i) / speedMph;
        }
        int stops = Math.max(0, route.size() - 2);
        return flying + stops * layoverHours;
    }

    private double flightCost(double miles) {
        double inShort = Math.min(miles, SHORT_BAND_END);
        double inMedium = Math.max(0, Math.min(miles, MEDIUM_BAND_END) - SHORT_BAND_END);
        double inLong = Math.max(0, miles - MEDIUM_BAND_END);
        return baseFee + inShort * rateShort + inMedium * rateMedium + inLong * rateLong;
    }

    private double legMiles(List<String> route, int index) {
        return Distance.miles(airports.get(route.get(index)), airports.get(route.get(index + 1)));
    }
}
