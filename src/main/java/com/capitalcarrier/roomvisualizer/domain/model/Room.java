package com.capitalcarrier.roomvisualizer.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private double width;
    private double length;
    private double height;
    private String wallColor;
    private String floorColor;
    private List<FurnitureItem> furnitureItems = new ArrayList<>();

    // Getters and Setters
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public String getWallColor() { return wallColor; }
    public void setWallColor(String wallColor) { this.wallColor = wallColor; }
    public String getFloorColor() { return floorColor; }
    public void setFloorColor(String floorColor) { this.floorColor = floorColor; }
    public List<FurnitureItem> getFurnitureItems() { return furnitureItems; }
    public void setFurnitureItems(List<FurnitureItem> furnitureItems) { this.furnitureItems = furnitureItems; }
    
    public void addFurnitureItem(FurnitureItem item) {
        this.furnitureItems.add(item);
    }
}
