package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;

import javax.swing.*;
import java.awt.*;

public class RoomCanvas2DPanel extends JPanel {
    private Room room;
    private static final int PADDING = 50;
    private double scale = 50.0; // pixels per meter

    public RoomCanvas2DPanel(Room room) {
        this.room = room;
        setBackground(new Color(25, 30, 55));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int canvasWidth = getWidth();
        int canvasHeight = getHeight();

        // Calculate scale to fit room in canvas
        double scaleW = (canvasWidth - 2 * PADDING) / room.getWidth();
        double scaleL = (canvasHeight - 2 * PADDING) / room.getLength();
        scale = Math.min(scaleW, scaleL);

        // Center the room
        int offsetX = (int) (canvasWidth - room.getWidth() * scale) / 2;
        int offsetY = (int) (canvasHeight - room.getLength() * scale) / 2;
        g2.translate(offsetX, offsetY);

        // Draw Floor
        g2.setColor(Color.decode(room.getFloorColor() != null ? room.getFloorColor() : "#A08764"));
        g2.fillRect(0, 0, (int) (room.getWidth() * scale), (int) (room.getLength() * scale));

        // Draw Grid
        g2.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i <= room.getWidth(); i++) {
            g2.drawLine((int)(i * scale), 0, (int)(i * scale), (int)(room.getLength() * scale));
        }
        for (int i = 0; i <= room.getLength(); i++) {
            g2.drawLine(0, (int)(i * scale), (int)(room.getWidth() * scale), (int)(i * scale));
        }

        // Draw Walls
        g2.setStroke(new BasicStroke(12f));
        g2.setColor(new Color(30, 35, 70)); // Dark navy wall
        g2.drawRect(0, 0, (int) (room.getWidth() * scale), (int) (room.getLength() * scale));

        // Draw Furniture
        for (FurnitureItem item : room.getFurnitureItems()) {
            drawFurniture(g2, item);
        }

        g2.dispose();
    }

    private void drawFurniture(Graphics2D g2, FurnitureItem item) {
        int x = (int) (item.getX() * scale);
        int y = (int) (item.getZ() * scale); // Using Z as the Y coordinate in 2D top-down
        int w = (int) (item.getWidth() * scale);
        int d = (int) (item.getDepth() * scale);

        g2.rotate(Math.toRadians(item.getRotation()), x + w/2, y + d/2);
        
        g2.setColor(new Color(153, 51, 255, 180));
        g2.fillRoundRect(x, y, w, d, 5, 5);
        
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x, y, w, d, 5, 5);
        
        g2.setFont(new Font("Inter", Font.PLAIN, 10));
        g2.drawString(item.getName(), x + 2, y + 12);

        g2.rotate(-Math.toRadians(item.getRotation()), x + w/2, y + d/2);
    }
}
