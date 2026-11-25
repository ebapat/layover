package layover;

import java.util.Map;
import java.util.Scanner;

/**
 * Reads typed answers from the user. Every method treats a blank answer as
 * "cancel", and reaching the end of input (Ctrl+D or a finished script) as a
 * blank answer, so no loop here can spin forever.
 */
public class Prompt {
    private final Scanner in;
    private boolean inputEnded = false;

    public Prompt(Scanner in) {
        this.in = in;
    }

    public boolean hasEnded() {
        return inputEnded;
    }

    public String ask(String question) {
        System.out.print(question);
        if (!in.hasNextLine()) {
            inputEnded = true;
            System.out.println();
            return "";
        }
        return in.nextLine().trim();
    }

    /** Keeps asking until the answer is a known airport code. Returns "" if cancelled. */
    public String askAirportCode(String question, Map<String, Airport> airports) {
        while (true) {
            String code = ask(question).toUpperCase();
            if (code.isEmpty() || airports.containsKey(code)) {
                return code;
            }
            System.out.println("'" + code + "' is not in the airport data. "
                    + "Try a 3-letter code like IND, or press Enter to cancel.");
        }
    }

    /** Whole number between min and max. A blank answer returns the fallback. */
    public int askWholeNumber(String question, int min, int max, int fallback) {
        while (true) {
            String text = ask(question);
            if (text.isEmpty()) {
                return fallback;
            }
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Falls through to the same message as an out-of-range number.
            }
            System.out.println("Please enter a whole number from " + min + " to " + max + ".");
        }
    }

    /** A number that is zero or more. Returns null if cancelled. */
    public Double askNonNegativeNumber(String question) {
        while (true) {
            String text = ask(question);
            if (text.isEmpty()) {
                return null;
            }
            try {
                double value = Double.parseDouble(text);
                if (Double.isFinite(value) && value >= 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Falls through to the same message as a negative number.
            }
            System.out.println("Please enter a number that is 0 or higher.");
        }
    }
}