package dev.malinoskj2.csvtosqlite;

import java.sql.Connection;
import java.sql.SQLException;

public class Driver {

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
