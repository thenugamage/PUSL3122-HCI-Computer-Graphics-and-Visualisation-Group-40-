package com.capitalcarrier.roomvisualizer.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name = "New Design";
    private double width;
    private double length;
    private double height;
    private String wallColor = "#8e9196";
    private String floorColor = "#6d573f";
    private List<FurnitureItem> furnitureItems = new ArrayList<>();
    
    // Lighting & Shading defaults from mockup
    private int brightness = 50; 
    private int lightX = 82;
    private int lightY = -600;
    private int lightZ = 0;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
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
    
    public int getBrightness() { return brightness; }
    public void setBrightness(int brightness) { this.brightness = brightness; }
    public int getLightX() { return lightX; }
    public void setLightX(int lightX) { this.lightX = lightX; }
    public int getLightY() { return lightY; }
    public void setLightY(int lightY) { this.lightY = lightY; }
    public int getLightZ() { return lightZ; }
    public void setLightZ(int lightZ) { this.lightZ = lightZ; }
    
    public void addFurnitureItem(FurnitureItem item) {
        this.furnitureItems.add(item);
    }
}
