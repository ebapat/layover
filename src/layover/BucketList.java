package layover;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** The user's destinations, stored in bucket_list.csv as: name,airport code,interest. */
public class BucketList {
    public static final int MIN_INTEREST = 1;
    public static final int MAX_INTEREST = 10;

    private final List<Destination> destinations = new ArrayList<>();

    /** A missing file just means a new user with an empty list, not an error. */
    public static BucketList load(Path file, Map<String, Airport> airports) throws IOException {
        BucketList list = new BucketList();
        if (!Files.exists(file)) {
            return list;
        }
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            List<String> f = CsvParser.parseLine(line);
            if (f.size() < 3) {
                System.err.println("Skipping bucket list row (needs name,airport,interest): " + line);
                continue;
            }
            String code = f.get(1).trim().toUpperCase();
            if (!airports.containsKey(code)) {
                System.err.println("Skipping " + f.get(0) + ": unknown airport code '" + code + "'");
                continue;
            }
            try {
                int interest = Integer.parseInt(f.get(2).trim());
                list.add(new Destination(f.get(0).trim(), code, clampInterest(interest)));
            } catch (NumberFormatException e) {
                System.err.println("Skipping " + f.get(0) + ": interest must be a number 1-10");
            }
        }
        return list;
    }

    /** Returns false if a destination with the same name is already on the list. */
    public boolean add(Destination destination) {
        for (Destination existing : destinations) {
            if (existing.name.equalsIgnoreCase(destination.name)) {
                return false;
            }
        }
        destinations.add(destination);
        return true;
    }

    public boolean remove(String name) {
        return destinations.removeIf(d -> d.name.equalsIgnoreCase(name));
    }

    public List<Destination> all() {
        return Collections.unmodifiableList(destinations);
    }

    public boolean isEmpty() {
        return destinations.isEmpty();
    }

    public void save(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Destination d : destinations) {
            lines.add(quote(d.name) + "," + d.airportCode + "," + d.interest);
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    // Keep a typo like 99 from distorting the score later.
    private static int clampInterest(int value) {
        return Math.max(MIN_INTEREST, Math.min(MAX_INTEREST, value));
    }

    // Names can contain commas ("Washington, D.C."), so wrap them in quotes when saving.
    private static String quote(String text) {
        if (text.contains(",") || text.contains("\"")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
