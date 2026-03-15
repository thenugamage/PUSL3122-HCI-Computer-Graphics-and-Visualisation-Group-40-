package com.capitalcarrier.roomvisualizer.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:sqlite:furniture_visualizer.db";
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

        String createProjectsTable = "CREATE TABLE IF NOT EXISTS design_projects (" +
                "id TEXT PRIMARY KEY," +
                "user_id TEXT," +
                "name TEXT," +
                "room_data TEXT," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createProjectsTable);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
