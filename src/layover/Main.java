package layover;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            AppData data = AppData.load(Path.of("data"));
            new Menu(data, new Prompt(new Scanner(System.in))).run();
        } catch (IOException e) {
            System.err.println("Could not read data files: " + e.getMessage());
            System.err.println("Run this from the repo root and make sure data/ has airports.dat and routes.dat.");
            System.exit(1);
        }
    }
}