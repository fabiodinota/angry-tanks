package com.angrytanks.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    // Update these constants with your actual database credentials.
    private static final String DB_URL = "jdbc:postgresql://localhost:8080/angrytanks";
    private static final String DB_USER = "fabiodinota";
    private static final String DB_PASSWORD = "fabio";

    /**
     * Returns a new connection to the database.
     *
     * @return a {@link Connection} instance.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
