package layover;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/** Settings stored in config.properties: home airport, scoring weights, and cost/time estimates. */
public class Config {
    public static final String HOME_AIRPORT = "home_airport";

    /** The five factors the scorer weighs. Each has a key like weight.cost. */
    public static final List<String> WEIGHT_NAMES =
            List.of("cost", "time", "connections", "new_countries", "interest");

    // Equal weights until the user changes them.
    private static final double DEFAULT_WEIGHT = 1.0;

    private final Properties properties = new Properties();

    /** A missing file gives defaults, so a first run works with no setup. */
    public static Config load(Path file) throws IOException {
        Config config = new Config();
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                config.properties.load(reader);
            }
        }
        return config;
    }

    /** Empty string means no home airport has been chosen yet. */
    public String getHomeAirport() {
        return properties.getProperty(HOME_AIRPORT, "").trim().toUpperCase();
    }

    public void setHomeAirport(String airportCode) {
        properties.setProperty(HOME_AIRPORT, airportCode.trim().toUpperCase());
    }

    /**
     * Reads a number setting, using the fallback if it is missing or not a number.
     * Negative values are rejected too: a negative weight would reward the wrong
     * thing and a negative price makes no sense.
     */
    public double getSetting(String key, double fallback) {
        String text = properties.getProperty(key);
        if (text == null) {
            return fallback;
        }
        try {
            double value = Double.parseDouble(text.trim());
            return Double.isFinite(value) && value >= 0 ? value : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public double getWeight(String name) {
        return getSetting("weight." + name, DEFAULT_WEIGHT);
    }

    /** All five weights in a fixed order, ready to hand to the scorer. */
    public Map<String, Double> getWeights() {
        Map<String, Double> weights = new LinkedHashMap<>();
        for (String name : WEIGHT_NAMES) {
            weights.put(name, getWeight(name));
        }
        return weights;
    }

    public void setWeight(String name, double value) {
        properties.setProperty("weight." + name, String.valueOf(value));
    }

    public void save(Path file) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            properties.store(writer, "Layover settings");
        }
    }
}
