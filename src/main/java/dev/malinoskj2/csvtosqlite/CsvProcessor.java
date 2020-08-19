package dev.malinoskj2.csvtosqlite;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CsvProcessor {

    private static final int BUFF_SIZE = 1024 * 1024;
    private static final int BATCH_COUNT = 900;
    private static final String SQL_INSERT = "INSERT INTO users(a,b,c,d,e,f,g,h,i,j) VALUES(?,?,?,?,?,?,?,?,?,?)";

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

    public void process() throws SQLException {

        Reader in = null;
        try {
            in = new BufferedReader(new FileReader(this.csvPath), BUFF_SIZE);

        } catch (FileNotFoundException e) {
            System.out.println("Failed to open Reader:" + e.getMessage());
        }

        try {
            this.conn.setAutoCommit(false);

            PreparedStatement goodDbInserts = this.conn.prepareStatement(SQL_INSERT);
            CSVPrinter csvPrinter = new CSVPrinter(this.badCsvWriter, CSVFormat.DEFAULT
                    .withHeader(getHeaderNames()));

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

            for (CSVRecord record : records) {

                if (record.isConsistent()) {
                    numSuccessful++;
                    insert(record, goodDbInserts);
                } else {
                    numFailed++;
                    csvPrinter.printRecord(record);
                }

                if (numSuccessful % BATCH_COUNT == 0) {
                    goodDbInserts.executeBatch();
                }

                if (numFailed % BATCH_COUNT == 0) {
                    csvPrinter.flush();
                }
            }

            if (!(numSuccessful % BATCH_COUNT == 0)) {
                goodDbInserts.executeBatch();
            }

            if (!(numFailed % BATCH_COUNT == 0)) {
                csvPrinter.flush();
            }

            this.conn.commit();
            writeLog();
        } catch (IOException e) {
            System.out.println("IOException when writing output: " + e.getMessage());
        }
    }

    private void insert(CSVRecord record, PreparedStatement statement) throws SQLException {
        statement.setString(1, record.get(0));
        statement.setString(2, record.get(1));
        statement.setString(3, record.get(2));
        statement.setString(4, record.get(3));
        statement.setString(5, record.get(4));
        statement.setString(6, record.get(5));
        statement.setString(7, record.get(6));
        statement.setInt(8, (record.get(7) == "TRUE") ? 1 : 0);
        statement.setInt(9, (record.get(8) == "TRUE") ? 1 : 0);
        statement.setString(10, record.get(9));
        statement.addBatch();
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
