package util;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private static final String FILE = "log.txt";

    public static void log(String message) {
        try {
            FileWriter fw = new FileWriter(FILE, true);
            fw.write(message + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            System.out.println("Logging error: " + e.getMessage());
        }
    }
}