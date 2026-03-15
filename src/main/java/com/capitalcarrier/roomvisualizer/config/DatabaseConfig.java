package com.capitalcarrier.roomvisualizer.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:sqlite:furniture_visualizer.db";
    private static String currentUrl = null;

    private static String getUrl() {
        if (currentUrl != null) return currentUrl;
        
        // Load remote URL if configured
        java.util.Properties props = new java.util.Properties();
        try (java.io.InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("google_oauth.properties")) {
            if (in != null) {
                props.load(in);
                String remoteUrl = props.getProperty("REMOTE_DB_URL", "");
                if (!remoteUrl.trim().isEmpty()) {
                    System.out.println("Using REMOTE database: " + remoteUrl.split("@")[remoteUrl.split("@").length - 1]); // Hide credentials in log
                    currentUrl = remoteUrl;
                    return currentUrl;
                }
            }
        } catch (Exception e) {
            // Fallback to SQLite
        }
        
        currentUrl = DEFAULT_URL;
        return currentUrl;
    }

    public static void setTestUrl() {
        currentUrl = "jdbc:sqlite::memory:";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl());
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
            
            // Get absolute path for logging (if using SQLite)
            String url = getUrl();
            if (url.startsWith("jdbc:sqlite:")) {
                String dbPath = new java.io.File(url.replace("jdbc:sqlite:", "")).getAbsolutePath();
                System.out.println("Local SQLite database initialized at: " + dbPath);
            } else {
                System.out.println("Remote database connection verified.");
            }
        } catch (SQLException e) {
            System.err.println("CRITICAL: Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
