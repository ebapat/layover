package layover;

import java.io.IOException;
import java.util.List;

/** The command-line menu. Each option is its own small method. */
public class Menu {
    private static final int DEFAULT_INTEREST = 5;

    private final AppData data;
    private final Prompt prompt;

    public Menu(AppData data, Prompt prompt) {
        this.data = data;
        this.prompt = prompt;
    }

    public void run() {
        System.out.println("Layover: decide which trip to take next.");
        if (data.config.getHomeAirport().isEmpty()) {
            System.out.println("First time here? Let's set your home airport.");
            setHomeAirport();
        }

        boolean running = true;
        while (running && !prompt.hasEnded()) {
            printMenu();
            String choice = prompt.ask("Choose an option: ");
            if (prompt.hasEnded()) {
                break;
            }
            switch (choice) {
                case "1" -> rankTrips();
                case "2" -> lookUpRoute();
                case "3" -> showBucketList();
                case "4" -> addDestination();
                case "5" -> removeDestination();
                case "6" -> markVisited();
                case "7" -> changeWeight();
                case "8" -> setHomeAirport();
                case "0" -> running = false;
                default -> System.out.println("Please enter a number from the menu.");
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        String home = data.config.getHomeAirport();
        System.out.println();
        System.out.println("=== Layover (home: " + (home.isEmpty() ? "not set" : home) + ") ===");
        System.out.println("1. Rank my trips");
        System.out.println("2. Look up a route");
        System.out.println("3. Show my bucket list");
        System.out.println("4. Add a destination");
        System.out.println("5. Remove a destination");
        System.out.println("6. Mark a place as visited");
        System.out.println("7. Change a scoring weight");
        System.out.println("8. Set my home airport");
        System.out.println("0. Quit");
    }

    private void rankTrips() {
        String home = requireHomeAirport();
        if (home.isEmpty()) {
            return;
        }
        if (data.bucketList.isEmpty()) {
            System.out.println("Your bucket list is empty. Add a destination from the menu.");
            return;
        }
        TravelModel model = new TravelModel(data.config, data.airports);
        List<TripOption> options = TripPlanner.plan(home, data.bucketList, data.visited,
                data.airports, data.finder, model);

        System.out.println("Weights: " + describeWeights());
        TripPrinter.printRanking(Scorer.rank(options, data.config.getWeights()));
    }

    private void lookUpRoute() {
        String home = data.config.getHomeAirport();
        String fromQuestion = home.isEmpty() ? "From airport code: "
                : "From airport code (Enter for " + home + "): ";
        String from = prompt.askAirportCode(fromQuestion, data.airports);
        if (from.isEmpty()) {
            from = home;
        }
        if (from.isEmpty()) {
            return;
        }
        String to = prompt.askAirportCode("To airport code: ", data.airports);
        if (to.isEmpty()) {
            return;
        }
        if (from.equals(to)) {
            System.out.println("Those are the same airport.");
            return;
        }
        printRoute("Fewest flights", data.finder.fewestConnections(from, to));
        printRoute("Shortest miles", data.finder.shortestDistance(from, to));
    }

    private void printRoute(String label, List<String> route) {
        if (route.isEmpty()) {
            System.out.println(label + ": no route found");
            return;
        }
        int stops = route.size() - 2;
        System.out.printf("%s: %s (%.0f miles, %d %s)%n", label, String.join(" > ", route),
                data.finder.totalMiles(route), stops, stops == 1 ? "stop" : "stops");
    }

    private void showBucketList() {
        if (data.bucketList.isEmpty()) {
            System.out.println("Your bucket list is empty. Add a destination from the menu.");
            return;
        }
        int number = 1;
        for (Destination d : data.bucketList.all()) {
            System.out.printf("%2d. %-18s %-4s interest %2d%s%n", number++, d.name, d.airportCode,
                    d.interest, data.visited.contains(d.airportCode) ? "  (visited)" : "");
        }
    }

    private void addDestination() {
        String name = prompt.ask("Destination name (Enter to cancel): ");
        if (name.isEmpty()) {
            return;
        }
        String code = prompt.askAirportCode("Airport code to fly into: ", data.airports);
        if (code.isEmpty()) {
            return;
        }
        int interest = prompt.askWholeNumber("How much do you want to go, 1-10 (Enter for "
                + DEFAULT_INTEREST + "): ", BucketList.MIN_INTEREST, BucketList.MAX_INTEREST,
                DEFAULT_INTEREST);

        if (data.bucketList.add(new Destination(name, code, interest))) {
            saveWith("bucket list", data::saveBucketList);
            System.out.println("Added " + name + ".");
        } else {
            System.out.println(name + " is already on your list.");
        }
    }

    private void removeDestination() {
        String name = prompt.ask("Name of the destination to remove (Enter to cancel): ");
        if (name.isEmpty()) {
            return;
        }
        if (data.bucketList.remove(name)) {
            saveWith("bucket list", data::saveBucketList);
            System.out.println("Removed " + name + ".");
        } else {
            System.out.println("No destination named '" + name + "' on your list.");
        }
    }

    private void markVisited() {
        String answer = prompt.ask("Destination name or airport code (Enter to cancel): ");
        if (answer.isEmpty()) {
            return;
        }
        // Accept a bucket list name too, so the user doesn't have to remember the code.
        String code = answer.toUpperCase();
        for (Destination d : data.bucketList.all()) {
            if (d.name.equalsIgnoreCase(answer)) {
                code = d.airportCode;
            }
        }
        Airport airport = data.airports.get(code);
        if (airport == null) {
            System.out.println("'" + answer + "' is not on your list or in the airport data.");
            return;
        }
        data.visited.add(code);
        saveWith("visited list", data::saveVisited);
        System.out.println("Marked " + code + " (" + airport.city + ", " + airport.country
                + ") as visited.");
    }

    private void changeWeight() {
        List<String> names = Config.WEIGHT_NAMES;
        for (int i = 0; i < names.size(); i++) {
            System.out.println((i + 1) + ". " + names.get(i) + " = " + data.config.getWeight(names.get(i)));
        }
        int choice = prompt.askWholeNumber("Which weight to change (Enter to cancel): ",
                0, names.size(), 0);
        if (choice == 0) {
            return;
        }
        String name = names.get(choice - 1);
        Double value = prompt.askNonNegativeNumber("New value for " + name
                + " (0 ignores it, higher matters more): ");
        if (value == null) {
            return;
        }
        data.config.setWeight(name, value);
        saveWith("settings", data::saveConfig);
        System.out.println(name + " is now " + value + ".");
    }

    private void setHomeAirport() {
        String code = prompt.askAirportCode("Home airport code, like IND (Enter to skip): ", data.airports);
        if (code.isEmpty()) {
            System.out.println("Home airport not set. You can set it from the menu (option 8).");
            return;
        }
        data.config.setHomeAirport(code);
        saveWith("settings", data::saveConfig);
        System.out.println("Home airport set to " + code + " (" + data.airports.get(code).city + ").");
    }

    // Ranking needs a starting point, so ask for one now if it is missing.
    private String requireHomeAirport() {
        if (data.config.getHomeAirport().isEmpty()) {
            System.out.println("Ranking needs a home airport.");
            setHomeAirport();
        }
        return data.config.getHomeAirport();
    }

    private String describeWeights() {
        StringBuilder text = new StringBuilder();
        for (String name : Config.WEIGHT_NAMES) {
            if (text.length() > 0) {
                text.append(", ");
            }
            text.append(name).append("=").append(data.config.getWeight(name));
        }
        return text.toString();
    }

    private interface FileAction {
        void run() throws IOException;
    }

    // A failed save shouldn't crash the menu; tell the user and keep going.
    private void saveWith(String what, FileAction action) {
        try {
            action.run();
        } catch (IOException e) {
            System.err.println("Could not save your " + what + ": " + e.getMessage());
        }
    }
}
