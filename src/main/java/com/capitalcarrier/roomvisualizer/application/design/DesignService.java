package com.capitalcarrier.roomvisualizer.application.design;

import com.capitalcarrier.roomvisualizer.config.DatabaseConfig;
import com.capitalcarrier.roomvisualizer.domain.model.DesignProject;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DesignService {
    private static final Gson gson = new Gson();

    public static void saveDesign(String name, Room room) throws SQLException {
        User user = AuthService.getCurrentUser();
        if (user == null) throw new SQLException("User not logged in");

        String roomData = gson.toJson(room);
        String sql = "INSERT INTO design_projects (id, user_id, name, room_data) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT(id) DO UPDATE SET name=excluded.name, room_data=excluded.room_data, updated_at=CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, user.getId());
            pstmt.setString(3, name);
            pstmt.setString(4, roomData);
            pstmt.executeUpdate();
        }
    }

    public static void deleteDesign(String id) throws SQLException {
        String sql = "DELETE FROM design_projects WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    public static void updateDesignName(String id, String newName) throws SQLException {
        String sql = "UPDATE design_projects SET name = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        }
    }

    public static List<DesignProject> getUserDesigns() throws SQLException {
        User user = AuthService.getCurrentUser();
        if (user == null) return new ArrayList<>();

        List<DesignProject> projects = new ArrayList<>();
        String sql = "SELECT * FROM design_projects WHERE user_id = ? ORDER BY updated_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DesignProject project = new DesignProject();
                project.setId(rs.getString("id"));
                project.setUserId(rs.getString("user_id"));
                project.setName(rs.getString("name"));
                String roomData = rs.getString("room_data");
                project.setRoom(gson.fromJson(roomData, Room.class));
                project.setCreatedAt(rs.getTimestamp("created_at"));
                project.setUpdatedAt(rs.getTimestamp("updated_at"));
                projects.add(project);
            }
        }
        return projects;
    }
}
