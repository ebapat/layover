package layover;

import java.util.List;

/** Prints ranked trips as a table. */
public class TripPrinter {

    public static void printRanking(List<ScoredTrip> trips) {
        if (trips.isEmpty()) {
            System.out.println("No trips to rank. Add a destination or check your home airport.");
            return;
        }
        System.out.printf("%-4s %-18s %-5s %7s %9s %6s %5s  %s%n",
                "Rank", "Destination", "Code", "Miles", "Est. cost", "Hours", "Score", "Route");
        int rank = 1;
        for (ScoredTrip trip : trips) {
            TripOption o = trip.option;
            System.out.printf("%-4d %-18s %-5s %7.0f %9s %6.1f %5.1f  %s%s%n",
                    rank++, o.destination.name, o.destination.airportCode, o.miles,
                    String.format("$%.0f", o.cost), o.hours, trip.score,
                    String.join(" > ", o.route), o.newCountry ? "  (new country)" : "");
        }
        System.out.println("Costs are rough estimates from distance, not real fares.");
    }
}