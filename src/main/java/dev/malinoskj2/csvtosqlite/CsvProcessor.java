package dev.malinoskj2.csvtosqlite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

public class CsvProcessor {

    private final String csvPath;
    private final Connection conn;
    private final BufferedWriter badCsvWriter;

    private int numSuccessful = 0;
    private int numFailed = 0;

    public CsvProcessor(File csv, Connection conn, BufferedWriter badCsvWriter) {
        this(csv.getAbsolutePath(), conn, badCsvWriter);
    }

    public CsvProcessor(String csv, Connection conn, BufferedWriter badCsvWriter) {
        this.csvPath = csv;
        this.conn = conn;
        this.badCsvWriter = badCsvWriter;
    }

    private String[] getHeaderNames () throws IOException {
        Path myPath = Paths.get(this.csvPath);
        return Files.lines(myPath)
                .findFirst()
                .get()
                .split(",");
    }

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
