package lk.group40.frdv.model;

import java.awt.Color;
import java.awt.Rectangle;

public class FurnitureItem {
    private final String id;
    private final String name;
    private int x;      // world coords (top-left)
    private int y;
    private int width;
    private int height;
    private Color color = new Color(200, 200, 200);

    public FurnitureItem(String id, String name, int x, int y, int width, int height) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }


    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public String toString() {
        return name;
    }
}
