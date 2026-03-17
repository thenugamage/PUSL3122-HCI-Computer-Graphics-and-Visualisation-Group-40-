package com.capitalcarrier.roomvisualizer.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConfig {

    private static String url;
    private static String username;
    private static String password;
    private static Connection connection;

    // Load configuration on startup
    static {
        loadProperties();
    }

    /**
     * Load DB config from application.properties
     */
    private static void loadProperties() {
        try (InputStream input = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input != null) {
                Properties props = new Properties();
                props.load(input);

                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");

                System.out.println("✅ Using database from application.properties");
            } else {
                // 🔥 MARKING MODE FALLBACK (VERY IMPORTANT)
                System.out.println("⚠ No config file found → Using MARKING MODE (Cloud DB)");

                url = "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:6543/postgres";
                username = "postgres.miqglabqmtnqseyetwec";
                password = "sBidOAfdYJdSUaDu";
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load DB configuration", e);
        }
    }

    /**
     * Get database connection (Singleton)
     */
public static Connection getConnection() {
    try {
        if (connection == null || connection.isClosed()) {

            if (url.startsWith("jdbc:sqlite:")) {

                // ✅ Load SQLite driver
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);

            } else {

                // ✅ Load PostgreSQL driver (FIXES YOUR ERROR)
                Class.forName("org.postgresql.Driver");

                Properties dbProps = new Properties();
                dbProps.setProperty("user", username);
                dbProps.setProperty("password", password);
                dbProps.setProperty("ssl", "true");
                dbProps.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
                dbProps.setProperty("prepareThreshold", "0");

                connection = DriverManager.getConnection(url, dbProps);
            }

            System.out.println("✅ Database connected!");
        }

    } catch (Exception e) {
        throw new RuntimeException("❌ Database connection failed", e);
    }

    return connection;
}

    /**
     * Initialize database tables (FIXES YOUR ERROR)
     */
    public static void initializeDatabase() {

        String timestampType = url.startsWith("jdbc:sqlite:") ? "DATETIME" : "TIMESTAMP";

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

            System.out.println("✅ Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close DB connection safely
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("⚠ Error closing DB: " + e.getMessage());
        }
    }
}