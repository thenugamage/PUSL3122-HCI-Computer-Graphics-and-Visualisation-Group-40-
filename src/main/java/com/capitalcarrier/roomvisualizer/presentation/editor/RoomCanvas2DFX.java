package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class RoomCanvas2DFX extends Pane {
    private Room room;
    private Canvas canvas;
    private double zoom = 50.0; // pixels per meter

    public RoomCanvas2DFX(Room room) {
        this.room = room;
        canvas = new Canvas(800, 600);
        getChildren().add(canvas);
        
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
        
        draw();
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        
        gc.clearRect(0, 0, w, h);
        
        // Draw Room Floor
        double rw = room.getWidth() * zoom;
        double rl = room.getLength() * zoom;
        double offX = (w - rw) / 2;
        double offY = (h - rl) / 2;

        gc.setFill(Color.web(room.getFloorColor() != null ? room.getFloorColor() : "#A08764"));
        gc.fillRect(offX, offY, rw, rl);
        
        // Draw Grid
        gc.setStroke(Color.web("white", 0.1));
        gc.setLineWidth(1);
        for (double i = 0; i <= room.getWidth(); i++) {
            gc.strokeLine(offX + i * zoom, offY, offX + i * zoom, offY + rl);
        }
        for (double i = 0; i <= room.getLength(); i++) {
            gc.strokeLine(offX, offY + i * zoom, offX + rw, offY + i * zoom);
        }

        // Draw Walls
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRect(offX, offY, rw, rl);

        // Draw Furniture
        if (room.getFurnitureItems() != null) {
            for (FurnitureItem item : room.getFurnitureItems()) {
                double fx = offX + item.getX() * zoom;
                double fz = offY + item.getZ() * zoom;
                double fw = item.getWidth() * zoom;
                double fd = item.getDepth() * zoom;
                
                gc.setFill(Color.web(item.getColor() != null ? item.getColor() : "#FFFFFF", 0.8));
                gc.fillRect(fx, fz, fw, fd);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(fx, fz, fw, fd);
            }
        }
    }
}
