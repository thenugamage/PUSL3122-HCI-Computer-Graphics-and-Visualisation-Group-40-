package com.capitalcarrier.roomvisualizer.domain.model;

import java.util.Date;

public class DesignProject {
    private String id;
    private String userId;
    private String name;
    private Room room;
    private Date createdAt;
    private Date updatedAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
