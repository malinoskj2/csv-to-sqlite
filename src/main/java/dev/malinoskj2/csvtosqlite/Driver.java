package dev.malinoskj2.csvtosqlite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver {

    // args[0] = CSV Path
    public static void main(String[] args) {

        File csv = new File(args[0]);

        // register jdbc driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load sqlite JDBC driver");
        }

        if (csv.exists()) {
            String dbPath = args[0].replaceAll("\\.csv$", ".db");
            String csvBadPath = args[0].replaceAll("\\.csv$", "-bad.csv");

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                 BufferedWriter badCsvWriter = Files.newBufferedWriter(Paths.get(csvBadPath))) {

                initTable(conn);

                CsvProcessor processor = new CsvProcessor(csv, conn, badCsvWriter);
                processor.process();
            } catch (SQLException | IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Please provide the path to a csv file.");
        }
    }

    public static void initTable(Connection con) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	A text,\n"
                + "	B text,\n"
                + "	C text,\n"
                + "	D text,\n"
                + "	E text,\n"
                + "	F text,\n"
                + "	G text,\n"
                + "	H integer,\n"
                + "	I integer,\n"
                + "	J text\n"
                + ");";

        con.createStatement().execute(sql);
    }
}
