package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class RoomCanvas2DFX extends Pane {
    private Room room;
    private Canvas canvas;
    private double zoom = 50.0;
    private boolean snapToGrid = true;

    private FurnitureItem selectedItem = null;
    private double dragStartMouseX, dragStartMouseY;
    private double dragStartItemX, dragStartItemZ;

    public RoomCanvas2DFX(Room room) {
        this.room = room;
        canvas = new Canvas(800, 600);
        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        canvas.setOnMousePressed(e -> {
            double offX = (getWidth()  - room.getWidth()  * zoom) / 2.0;
            double offY = (getHeight() - room.getLength() * zoom) / 2.0;
            selectedItem = null;
            List<FurnitureItem> items = room.getFurnitureItems();
            if (items != null) {
                for (int i = items.size() - 1; i >= 0; i--) {
                    FurnitureItem item = items.get(i);
                    double fx = offX + item.getX() * zoom;
                    double fz = offY + item.getZ() * zoom;
                    double fw = item.getWidth() * zoom;
                    double fd = item.getDepth() * zoom;
                    if (e.getX() >= fx && e.getX() <= fx + fw &&
                        e.getY() >= fz && e.getY() <= fz + fd) {
                        selectedItem = item;
                        dragStartMouseX = e.getX();
                        dragStartMouseY = e.getY();
                        dragStartItemX = item.getX();
                        dragStartItemZ = item.getZ();
                        break;
                    }
                }
            }
            draw();
        });

        canvas.setOnMouseDragged(e -> {
            if (selectedItem != null) {
                double dx = (e.getX() - dragStartMouseX) / zoom;
                double dz = (e.getY() - dragStartMouseY) / zoom;
                double newX = dragStartItemX + dx;
                double newZ = dragStartItemZ + dz;

                newX = Math.max(0, Math.min(room.getWidth()  - selectedItem.getWidth(),  newX));
                newZ = Math.max(0, Math.min(room.getLength() - selectedItem.getDepth(), newZ));

                if (snapToGrid) {
                    newX = Math.round(newX * 2.0) / 2.0;
                    newZ = Math.round(newZ * 2.0) / 2.0;
                }
                selectedItem.setX(newX);
                selectedItem.setZ(newZ);
                draw();
            }
        });

        // Scroll to zoom
        canvas.setOnScroll(e -> {
            double delta = e.getDeltaY() > 0 ? 5 : -5;
            setZoom(zoom + delta);
        });

        draw();
    }

    public void setZoom(double zoom) {
        this.zoom = Math.max(20, Math.min(150, zoom));
        draw();
    }

    public double getZoom() { return zoom; }

    public void setSnapToGrid(boolean snap) { this.snapToGrid = snap; }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Background
        gc.setFill(Color.web("#050A1E"));
        gc.fillRect(0, 0, w, h);

        double rw = room.getWidth()  * zoom;
        double rl = room.getLength() * zoom;
        double offX = (w - rw) / 2.0;
        double offY = (h - rl) / 2.0;

        // ── Wall border (thick, wall colour) ────────────────────────────────
        double wallThick = Math.max(4, zoom * 0.12);
        String wallHex = room.getWallColor() != null ? room.getWallColor() : "#DCD2BE";
        gc.setFill(Color.web(wallHex));
        // top / bottom / left / right wall strips
        gc.fillRect(offX - wallThick, offY - wallThick, rw + wallThick * 2, wallThick);        // top
        gc.fillRect(offX - wallThick, offY + rl, rw + wallThick * 2, wallThick);               // bottom
        gc.fillRect(offX - wallThick, offY, wallThick, rl);                                    // left
        gc.fillRect(offX + rw, offY, wallThick, rl);                                           // right

        // ── Floor ────────────────────────────────────────────────────────────
        String floorHex = room.getFloorColor() != null ? room.getFloorColor() : "#A08764";
        gc.setFill(Color.web(floorHex));
        gc.fillRect(offX, offY, rw, rl);

        // ── Grid lines ───────────────────────────────────────────────────────
        gc.setStroke(Color.web("white", 0.08));
        gc.setLineWidth(0.5);
        for (double i = 1; i < room.getWidth(); i++) {
            gc.strokeLine(offX + i * zoom, offY, offX + i * zoom, offY + rl);
        }
        for (double i = 1; i < room.getLength(); i++) {
            gc.strokeLine(offX, offY + i * zoom, offX + rw, offY + i * zoom);
        }

        // ── Room outline ─────────────────────────────────────────────────────
        gc.setStroke(Color.web("#FFFFFF", 0.9));
        gc.setLineWidth(1.5);
        gc.strokeRect(offX, offY, rw, rl);

        // ── Dimension labels ─────────────────────────────────────────────────
        gc.setFill(Color.web("#8C94AF"));
        gc.setFont(Font.font("Inter", 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.format("%.0fm", room.getWidth()), offX + rw / 2.0, offY - wallThick - 8);
        gc.save();
        gc.translate(offX - wallThick - 14, offY + rl / 2.0);
        gc.rotate(-90);
        gc.fillText(String.format("%.0fm", room.getLength()), 0, 0);
        gc.restore();

        // ── Compass (top-right corner of canvas) ─────────────────────────────
        double cx = w - 28, cy = 30, cr = 14;
        gc.setFill(Color.web("#141932", 0.8));
        gc.fillOval(cx - cr, cy - cr, cr * 2, cr * 2);
        gc.setStroke(Color.web("#8C94AF", 0.5)); gc.setLineWidth(0.7);
        gc.strokeOval(cx - cr, cy - cr, cr * 2, cr * 2);
        gc.setFill(Color.web("#8B5CF6"));
        gc.fillPolygon(new double[]{cx, cx - 4, cx + 4}, new double[]{cy - cr + 3, cy + 2, cy + 2}, 3);
        gc.setFill(Color.web("#FFFFFF", 0.3));
        gc.fillPolygon(new double[]{cx, cx - 4, cx + 4}, new double[]{cy + cr - 3, cy - 2, cy - 2}, 3);
        gc.setFill(Color.web("#8C94AF"));
        gc.setFont(Font.font("Inter", 9));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("N", cx, cy - cr + 11);

        // ── Furniture items ───────────────────────────────────────────────────
        List<FurnitureItem> items = room.getFurnitureItems();
        if (items != null) {
            for (FurnitureItem item : items) {
                double fx = offX + item.getX() * zoom;
                double fz = offY + item.getZ() * zoom;
                double fw = item.getWidth() * zoom;
                double fd = item.getDepth() * zoom;
                boolean sel = item == selectedItem;

                // Drop shadow
                gc.setFill(Color.web("#000000", 0.25));
                gc.fillRoundRect(fx + 3, fz + 4, fw, fd, 7, 7);

                // Body
                String col = item.getColor();
                gc.setFill(Color.web(col != null ? col : "#8B5CF6", sel ? 1.0 : 0.88));
                gc.fillRoundRect(fx, fz, fw, fd, 7, 7);

                // Highlight stripe at top
                gc.setFill(Color.web("#FFFFFF", 0.12));
                gc.fillRoundRect(fx, fz, fw, Math.min(fd * 0.3, 8), 7, 7);

                // Border
                gc.setStroke(sel ? Color.web("#ca4bf6") : Color.web("#000000", 0.3));
                gc.setLineWidth(sel ? 2.5 : 1);
                gc.strokeRoundRect(fx, fz, fw, fd, 7, 7);

                // Name label
                gc.setFill(Color.WHITE);
                double fontSize = Math.max(8, Math.min(12, fw / 7.0));
                gc.setFont(Font.font("Inter", fontSize));
                gc.setTextAlign(TextAlignment.CENTER);
                String name = item.getName() != null ? item.getName() : "";
                gc.fillText(name, fx + fw / 2.0, fz + fd / 2.0 + fontSize / 3.0, fw - 4);

                // Selection handles
                if (sel) {
                    gc.setFill(Color.web("#ca4bf6"));
                    double r = 5;
                    gc.fillOval(fx - r, fz - r, r * 2, r * 2);
                    gc.fillOval(fx + fw - r, fz - r, r * 2, r * 2);
                    gc.fillOval(fx - r, fz + fd - r, r * 2, r * 2);
                    gc.fillOval(fx + fw - r, fz + fd - r, r * 2, r * 2);
                }
            }
        }
    }
}
