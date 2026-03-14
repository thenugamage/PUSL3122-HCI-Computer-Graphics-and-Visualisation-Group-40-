package com.capitalcarrier.roomvisualizer.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DB_DIR = System.getenv("APP_DATA_DIR") != null
            ? System.getenv("APP_DATA_DIR")
            : ".";
    private static final String DEFAULT_URL = "jdbc:sqlite:" + DB_DIR + "/furniture_visualizer.db";
    private static String currentUrl = DEFAULT_URL;

    public static void setTestUrl() {
        currentUrl = "jdbc:sqlite::memory:";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(currentUrl);
    }

    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id TEXT PRIMARY KEY," +
                "username TEXT UNIQUE," +
                "email TEXT UNIQUE," +
                "full_name TEXT," +
                "password_hash TEXT," +
                "google_id TEXT UNIQUE" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
