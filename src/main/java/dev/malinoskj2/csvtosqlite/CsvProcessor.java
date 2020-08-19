package dev.malinoskj2.csvtosqlite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvProcessor {

    private final String csvPath;

    private int numSuccessful = 0;
    private int numFailed = 0;

    private void writeLog() throws IOException {
        String logOutput = String.format("%d %s%n%d %s%n%d %s%n",
                numSuccessful + numFailed, "records received",
                numSuccessful, "records successful",
                numFailed, "records failed");

        Path path = Paths.get(this.csvPath.replaceAll("\\.csv$", ".log"));
        byte[] logBytes = logOutput.getBytes();

        Files.write(path, logBytes);
    }
}
