package lk.group40.frdv.ui.canvas;

import lk.group40.frdv.model.FurnitureItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Viewer3DPanel extends JPanel {

    private final DesignCanvas2D canvas2D;

    // rotation around Y axis (simulated)
    private double rotY = Math.toRadians(25);

    private Point lastMouse = null;

    public Viewer3DPanel(DesignCanvas2D canvas2D) {
        this.canvas2D = canvas2D;
        setBackground(Color.WHITE);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouse = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastMouse = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMouse == null) return;
                int dx = e.getX() - lastMouse.x;

                // drag to rotate
                rotY += dx * 0.01; // sensitivity
                lastMouse = e.getPoint();
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Center in screen space
        int cx = w / 2;
        int cy = h / 2 + 50;

        // Draw "floor" grid using iso projection
        drawIsoGrid(g2, cx, cy);

        // Draw room boundary in 3D (a simple box outline)
        drawRoomBox(g2, cx, cy);

        // Draw furniture as 3D blocks based on 2D items
        List<FurnitureItem> items = canvas2D.getItemsSnapshot();
        for (FurnitureItem it : items) {
            drawFurnitureBlock(g2, cx, cy, it);
        }

        // Overlay hint
        g2.setColor(new Color(0, 0, 0, 160));
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.drawString("3D View (Simulated) — Drag mouse to rotate", 14, getHeight() - 14);

        g2.dispose();
    }

    // ================== Drawing helpers ==================

    private void drawIsoGrid(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(235, 235, 235));

        int size = 900;
        int step = 50;

        for (int x = -size; x <= size; x += step) {
            Point p1 = project3D(x, 0, -size, cx, cy);
            Point p2 = project3D(x, 0, size, cx, cy);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        for (int z = -size; z <= size; z += step) {
            Point p1 = project3D(-size, 0, z, cx, cy);
            Point p2 = project3D(size, 0, z, cx, cy);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void drawRoomBox(Graphics2D g2, int cx, int cy) {
        // Use current room size from canvas
        int rw = canvas2D.getRoomWidth();
        int rh = canvas2D.getRoomHeight();

        // In 3D we'll treat width as X, height as Z (floor plane)
        int halfX = rw / 2;
        int halfZ = rh / 2;

        int wallH = 120; // fixed wall height for visualization

        // floor corners
        Point a = project3D(-halfX, 0, -halfZ, cx, cy);
        Point b = project3D(halfX, 0, -halfZ, cx, cy);
        Point c = project3D(halfX, 0, halfZ, cx, cy);
        Point d = project3D(-halfX, 0, halfZ, cx, cy);

        // top corners (walls)
        Point a2 = project3D(-halfX, wallH, -halfZ, cx, cy);
        Point b2 = project3D(halfX, wallH, -halfZ, cx, cy);
        Point c2 = project3D(halfX, wallH, halfZ, cx, cy);
        Point d2 = project3D(-halfX, wallH, halfZ, cx, cy);

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(80, 80, 80));

        // floor rectangle
        line(g2, a, b); line(g2, b, c); line(g2, c, d); line(g2, d, a);

        // vertical edges
        line(g2, a, a2); line(g2, b, b2); line(g2, c, c2); line(g2, d, d2);

        // top rectangle
        line(g2, a2, b2); line(g2, b2, c2); line(g2, c2, d2); line(g2, d2, a2);

        // label
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
        g2.drawString("Room (3D)", 16, 22);
    }

    private void drawFurnitureBlock(Graphics2D g2, int cx, int cy, FurnitureItem it) {
        // Map 2D coordinates (x,y) in canvas to 3D floor plane (X,Z)
        // Your 2D room is centered, same assumption here.
        int x = it.getX();
        int z = it.getY(); // treat Y as Z on floor

        int w = it.getWidth();
        int d = it.getHeight();

        int h = it.getName().toLowerCase().contains("chair") ? 60 : 45; // chair taller

        // corners on floor
        Point p1 = project3D(x, 0, z, cx, cy);
        Point p2 = project3D(x + w, 0, z, cx, cy);
        Point p3 = project3D(x + w, 0, z + d, cx, cy);
        Point p4 = project3D(x, 0, z + d, cx, cy);

        // top corners
        Point t1 = project3D(x, h, z, cx, cy);
        Point t2 = project3D(x + w, h, z, cx, cy);
        Point t3 = project3D(x + w, h, z + d, cx, cy);
        Point t4 = project3D(x, h, z + d, cx, cy);

        // fill top face (simple)
        g2.setColor(it.getColor());
        Polygon top = new Polygon(
                new int[]{t1.x, t2.x, t3.x, t4.x},
                new int[]{t1.y, t2.y, t3.y, t4.y},
                4
        );
        g2.fillPolygon(top);

        // outline
        g2.setColor(new Color(60, 60, 60));
        g2.setStroke(new BasicStroke(1.5f));

        // bottom
        line(g2, p1, p2); line(g2, p2, p3); line(g2, p3, p4); line(g2, p4, p1);

        // top
        line(g2, t1, t2); line(g2, t2, t3); line(g2, t3, t4); line(g2, t4, t1);

        // verticals
        line(g2, p1, t1); line(g2, p2, t2); line(g2, p3, t3); line(g2, p4, t4);

        // label
        g2.setFont(g2.getFont().deriveFont(11f));
        g2.setColor(new Color(20, 20, 20));
        g2.drawString(it.getName(), t1.x + 4, t1.y - 4);
    }

    private Point project3D(double x, double y, double z, int cx, int cy) {
        // Rotate around Y axis
        double cos = Math.cos(rotY);
        double sin = Math.sin(rotY);

        double xr = x * cos + z * sin;
        double zr = -x * sin + z * cos;

        // Isometric-ish projection (screen X from xr, screen Y from y and zr)
        double scale = 0.8;
        int sx = (int) Math.round(cx + xr * scale);
        int sy = (int) Math.round(cy - y * scale + zr * 0.45);

        return new Point(sx, sy);
    }

    private void line(Graphics2D g2, Point a, Point b) {
        g2.drawLine(a.x, a.y, b.x, b.y);
    }
}
