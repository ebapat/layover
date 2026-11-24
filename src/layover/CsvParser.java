package layover;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits one line of CSV into fields.
 * Written by hand because OpenFlights wraps text in quotes and some names
 * contain commas (e.g. "Dallas, Fort Worth"), so line.split(",") would break them.
 */
public class CsvParser {

    // OpenFlights writes \N when a value is unknown.
    private static final String MISSING = "\\N";

    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Two quotes in a row inside a quoted field mean one literal quote.
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (c == ',' && !insideQuotes) {
                fields.add(clean(current.toString()));
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(clean(current.toString()));
        return fields;
    }

    // Turn OpenFlights' \N marker into an empty string so callers only check isEmpty().
    private static String clean(String value) {
        return value.equals(MISSING) ? "" : value;
    }
}
