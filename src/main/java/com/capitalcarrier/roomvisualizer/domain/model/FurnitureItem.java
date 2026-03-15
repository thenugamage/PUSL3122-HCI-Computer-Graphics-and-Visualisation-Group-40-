package com.capitalcarrier.roomvisualizer.domain.model;

public class FurnitureItem {
    private String id;
    private String name;
    private String type;
    private double x, y, z;
    private double rotation;
    private double width, height, depth;
    private String color;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public double getDepth() { return depth; }
    public void setDepth(double depth) { this.depth = depth; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
