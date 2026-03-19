package com.capitalcarrier.roomvisualizer.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static String currentUrl = null;

    private static String getUrl() {
        if (currentUrl != null) return currentUrl;
        
        java.util.Properties props = loadProperties();
        String remoteUrl = props.getProperty("REMOTE_DB_URL", "");
        if (!remoteUrl.trim().isEmpty()) {
            System.out.println("Using REMOTE database from properties.");
            currentUrl = remoteUrl;
            return currentUrl;
        }
        
        // --- MARKING MODE FALLBACK ---
        // If config file is missing, use these credentials automatically for markers
        System.out.println("No properties file found. Entering MARKING MODE (Cloud Fallback).");
        currentUrl = "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?user=postgres.miqglabqmtnqseyetwec&password=sBidOAfdYJdSUaDu";
        return currentUrl;
    }

    private static java.util.Properties loadProperties() {
        java.util.Properties props = new java.util.Properties();
        try (java.io.InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("google_oauth.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {}
        return props;
    }

    public static void setTestUrl() {
        currentUrl = "jdbc:sqlite::memory:";
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL Driver not found: " + e.getMessage());
        }
        
        String url = getUrl();
        if (url.startsWith("jdbc:sqlite:")) {
            return DriverManager.getConnection(url);
        } else {
            // For Remote PostgreSQL
            // We ensure we use Port 6543 (Transaction Pooler) for IPv4 compatibility
            String user = "postgres.miqglabqmtnqseyetwec";
            String pass = "sBidOAfdYJdSUaDu";
            
            // If the URL from properties contains credentials, we could parse them, 
            // but for this project we'll ensure the properties URL is used if it matches our pattern.
            String finalUrl = url;
            if (url.contains(":5432/")) {
                finalUrl = url.replace(":5432/", ":6543/"); // Force pooler port for reliability
            }

            java.util.Properties dbProps = new java.util.Properties();
            dbProps.setProperty("user", user);
            dbProps.setProperty("password", pass);
            dbProps.setProperty("ssl", "true");
            dbProps.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
            dbProps.setProperty("prepareThreshold", "0");
            
            System.out.println("Connecting to Remote Database: " + finalUrl.split("\\?")[0]);
            return DriverManager.getConnection(finalUrl, dbProps);
        }
    }

    public static void initializeDatabase() {
        String timestampType = getUrl().startsWith("jdbc:sqlite:") ? "DATETIME" : "TIMESTAMP";
        
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
                "created_at " + timestampType + " DEFAULT CURRENT_TIMESTAMP," +
                "updated_at " + timestampType + " DEFAULT CURRENT_TIMESTAMP," +
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
