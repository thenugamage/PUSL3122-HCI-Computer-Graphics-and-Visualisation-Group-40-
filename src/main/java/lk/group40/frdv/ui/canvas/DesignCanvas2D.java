package lk.group40.frdv.ui.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class DesignCanvas2D extends JPanel {

    // Room size in "world units" (we’ll treat as cm for now)
    private int roomWidth = 500;
    private int roomHeight = 350;

    // View transform (pan + zoom)
    private double zoom = 1.0;
    private double panX = 0;
    private double panY = 0;

    // Mouse panning
    private Point lastMouse = null;

    public DesignCanvas2D() {
        setBackground(Color.WHITE);
        setFocusable(true);

        // Mouse wheel: zoom in/out
        addMouseWheelListener(e -> {
            double oldZoom = zoom;
            if (e.getPreciseWheelRotation() < 0) zoom *= 1.1;
            else zoom /= 1.1;

            zoom = clamp(zoom, 0.3, 3.0);

            // Zoom around mouse pointer (nice UX)
            Point p = e.getPoint();
            double dx = p.x - getWidth() / 2.0;
            double dy = p.y - getHeight() / 2.0;

            panX = panX - dx * (zoom - oldZoom) / (oldZoom);
            panY = panY - dy * (zoom - oldZoom) / (oldZoom);

            repaint();
        });

        // Mouse drag: pan
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
                Point now = e.getPoint();
                panX += (now.x - lastMouse.x);
                panY += (now.y - lastMouse.y);
                lastMouse = now;
                repaint();
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // Later we can call this from RoomConfigPanel
    public void setRoomSize(int width, int height) {
        this.roomWidth = Math.max(100, width);
        this.roomHeight = Math.max(100, height);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Center of screen
        double cx = getWidth() / 2.0;
        double cy = getHeight() / 2.0;

        // Build view transform: move to center, apply pan, then zoom
        AffineTransform at = new AffineTransform();
        at.translate(cx + panX, cy + panY);
        at.scale(zoom, zoom);

        g2.transform(at);

        // Draw grid in world space
        drawGrid(g2);

        // Draw room centered at (0,0) in world space
        int x = -roomWidth / 2;
        int y = -roomHeight / 2;

        // Room fill
        g2.setColor(new Color(245, 245, 245));
        g2.fillRect(x, y, roomWidth, roomHeight);

        // Room border
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(60, 60, 60));
        g2.drawRect(x, y, roomWidth, roomHeight);

        // Label
        g2.setColor(new Color(80, 80, 80));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("Room (2D)", x + 10, y + 22);

        // Hint text (not transformed by zoom)
        g2.dispose();

        // UI hint overlay (screen space)
        Graphics2D overlay = (Graphics2D) g.create();
        overlay.setColor(new Color(0, 0, 0, 160));
        overlay.setFont(overlay.getFont().deriveFont(12f));
        overlay.drawString("Mouse wheel: zoom  |  Drag: pan", 14, getHeight() - 14);
        overlay.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        int grid = 50; // 50 world units per grid line
        int halfW = 2000;
        int halfH = 2000;

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(230, 230, 230));

        for (int x = -halfW; x <= halfW; x += grid) {
            g2.drawLine(x, -halfH, x, halfH);
        }
        for (int y = -halfH; y <= halfH; y += grid) {
            g2.drawLine(-halfW, y, halfW, y);
        }

        // Axes
        g2.setColor(new Color(210, 210, 210));
        g2.drawLine(-halfW, 0, halfW, 0);
        g2.drawLine(0, -halfH, 0, halfH);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
