package com.capitalcarrier.roomvisualizer.application.auth;

import com.capitalcarrier.roomvisualizer.config.DatabaseConfig;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import com.capitalcarrier.roomvisualizer.security.PasswordHasher;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

public class AuthService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static Properties loadOAuthConfig() {
        Properties props = new Properties();
        try (InputStream in = AuthService.class.getClassLoader().getResourceAsStream("google_oauth.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            // Will fail gracefully when Google login is attempted
        }
        return props;
    }


    // Store currently logged in user
    private static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static User registerLocalUser(String fullName, String username, String email, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username is required");
        }
        if (username.length() < 3) {
            throw new Exception("Username must be at least 3 characters");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Invalid email format");
        }
        if (password == null || password.isEmpty()) {
            throw new Exception("Password is required");
        }
        if (password.length() < 6) {
            throw new Exception("Password must be at least 6 characters");
        }
        
        if (getUserByUsername(username).isPresent()) {
            throw new Exception("Username already exists");
        }
        
        String id = generateId();
        String hash = PasswordHasher.hashPassword(password);
        
        String sql = "INSERT INTO users (id, username, email, full_name, password_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, username);
            pstmt.setString(3, email);
            pstmt.setString(4, fullName);
            pstmt.setString(5, hash);
            pstmt.executeUpdate();
            
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPasswordHash(hash);
            return user;
        }
    }

    public static User loginLocalUser(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username is required");
        }
        if (password == null || password.isEmpty()) {
            throw new Exception("Password is required");
        }
        
        Optional<User> optUser = getUserByUsername(username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                currentUser = user;
                return user;
            }
        }
        throw new Exception("Invalid username or password");
    }

    private static Optional<User> getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        }
        return Optional.empty();
    }

    private static Optional<User> getUserByGoogleId(String googleId) throws SQLException {
        String sql = "SELECT * FROM users WHERE google_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, googleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        }
        return Optional.empty();
    }

    private static User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setGoogleId(rs.getString("google_id"));
        return user;
    }

    public static User loginWithGoogle() throws Exception {
        NetHttpTransport httpTransport = new NetHttpTransport();
        
        Properties oauthConfig = loadOAuthConfig();
        String clientId = oauthConfig.getProperty("GOOGLE_CLIENT_ID", "");
        String clientSecret = oauthConfig.getProperty("GOOGLE_CLIENT_SECRET", "");
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            throw new Exception("Google OAuth credentials are missing in 'src/main/resources/google_oauth.properties'.");
        }

        String clientSecretsJson = String.format("{\"installed\":{\"client_id\":\"%s\",\"client_secret\":\"%s\"}}", clientId, clientSecret);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(clientSecretsJson));
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Arrays.asList("https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email"))
                .build();
                
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        
        Oauth2 oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("Room Visualizer").build();
        Userinfo userinfo = oauth2.userinfo().get().execute();
        
        String googleId = userinfo.getId();
        Optional<User> existing = getUserByGoogleId(googleId);
        if (existing.isPresent()) {
            currentUser = existing.get();
            return currentUser;
        }
        
        String id = generateId();
        String username = userinfo.getEmail() != null ? userinfo.getEmail().split("@")[0] : "user_" + id.substring(0, 5);
        
        // Ensure username is unique
        int i = 1;
        String baseUsername = username;
        while (getUserByUsername(username).isPresent()) {
            username = baseUsername + i++;
        }
        
        String sql = "INSERT INTO users (id, username, email, full_name, google_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, username);
            pstmt.setString(3, userinfo.getEmail());
            pstmt.setString(4, userinfo.getName());
            pstmt.setString(5, googleId);
            pstmt.executeUpdate();
            
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(userinfo.getEmail());
            user.setFullName(userinfo.getName());
            user.setGoogleId(googleId);
            currentUser = user;
            return user;
        }
    }
}
