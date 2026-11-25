package layover;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/** Everything the program loads at startup, plus the file paths needed to save changes. */
public class AppData {
    public final Map<String, Airport> airports;
    public final RouteFinder finder;
    public final BucketList bucketList;
    public final VisitedList visited;
    public final Config config;

    private final Path bucketListFile;
    private final Path visitedFile;
    private final Path configFile;

    private AppData(Map<String, Airport> airports, RouteFinder finder, BucketList bucketList,
                    VisitedList visited, Config config, Path bucketListFile,
                    Path visitedFile, Path configFile) {
        this.airports = airports;
        this.finder = finder;
        this.bucketList = bucketList;
        this.visited = visited;
        this.config = config;
        this.bucketListFile = bucketListFile;
        this.visitedFile = visitedFile;
        this.configFile = configFile;
    }

    /** Loads the airport data and the user's own files from one data folder. */
    public static AppData load(Path dataDir) throws IOException {
        Map<String, Airport> airports = AirportLoader.load(dataDir.resolve("airports.dat"));
        Graph graph = RouteLoader.load(dataDir.resolve("routes.dat"), airports);

        Path bucketListFile = dataDir.resolve("bucket_list.csv");
        Path visitedFile = dataDir.resolve("visited.csv");
        Path configFile = dataDir.resolve("config.properties");

        return new AppData(airports, new RouteFinder(graph, airports),
                BucketList.load(bucketListFile, airports), VisitedList.load(visitedFile),
                Config.load(configFile), bucketListFile, visitedFile, configFile);
    }

    public void saveBucketList() throws IOException {
        bucketList.save(bucketListFile);
    }

    public void saveVisited() throws IOException {
        visited.save(visitedFile);
    }

    public void saveConfig() throws IOException {
        config.save(configFile);
    }
}
