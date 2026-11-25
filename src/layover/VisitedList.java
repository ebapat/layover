package layover;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Airports the user has already been to, one code per line in visited.csv.
 * Stored by airport (not destination name) so it still works if the bucket list changes.
 */
public class VisitedList {
    // TreeSet keeps the file in alphabetical order, so git diffs stay small.
    private final Set<String> codes = new TreeSet<>();

    public static VisitedList load(Path file) throws IOException {
        VisitedList list = new VisitedList();
        if (!Files.exists(file)) {
            return list;
        }
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (!line.isBlank()) {
                list.codes.add(line.trim().toUpperCase());
            }
        }
        return list;
    }

    public void add(String airportCode) {
        codes.add(airportCode.toUpperCase());
    }

    public boolean contains(String airportCode) {
        return codes.contains(airportCode.toUpperCase());
    }

    public Set<String> all() {
        return Collections.unmodifiableSet(codes);
    }

    public void save(Path file) throws IOException {
        Files.write(file, codes, StandardCharsets.UTF_8);
    }
}
