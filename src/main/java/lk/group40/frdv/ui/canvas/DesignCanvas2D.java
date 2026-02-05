package lk.group40.frdv.ui.canvas;

import lk.group40.frdv.model.FurnitureItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DesignCanvas2D extends JPanel {

    // ===== Selection Listener =====
    public interface SelectionListener {
        void onSelectionChanged(FurnitureItem selected);
    }

    private SelectionListener selectionListener;

    public void setSelectionListener(SelectionListener listener) {
        this.selectionListener = listener;
    }

    private void notifySelectionChanged() {
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selected);
        }
    }

    // ===== Room size (world units) =====
    private int roomWidth = 500;
    private int roomHeight = 350;

    // ===== View transform (pan + zoom) =====
    private double zoom = 1.0;
    private double panX = 0;
    private double panY = 0;

    // Mouse state
    private Point lastMouseScreen = null;
    private boolean panning = false;

    // Furniture items
    private final List<FurnitureItem> items = new ArrayList<>();
    private FurnitureItem selected = null;

    // Drag selected furniture
    private Point lastMouseWorld = null;

    public DesignCanvas2D() {
        setBackground(Color.WHITE);
        setFocusable(true);

        addMouseWheelListener(this::handleZoom);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                lastMouseScreen = e.getPoint();

                Point world = screenToWorld(e.getPoint());
                FurnitureItem hit = hitTest(world);

                if (hit != null) {
                    selected = hit;
                    lastMouseWorld = world;
                    panning = false;
                } else {
                    selected = null;
                    lastMouseWorld = null;
                    panning = true; // click empty space = pan
                }

                notifySelectionChanged();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastMouseScreen = null;
                lastMouseWorld = null;
                panning = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMouseScreen == null) return;

                // If an item is selected, drag it
                if (selected != null && lastMouseWorld != null) {
                    Point nowWorld = screenToWorld(e.getPoint());
                    int dx = nowWorld.x - lastMouseWorld.x;
                    int dy = nowWorld.y - lastMouseWorld.y;

                    selected.setX(selected.getX() + dx);
                    selected.setY(selected.getY() + dy);

                    lastMouseWorld = nowWorld;
                    repaint();
                    return;
                }

                // Otherwise pan
                if (panning) {
                    Point now = e.getPoint();
                    panX += (now.x - lastMouseScreen.x);
                    panY += (now.y - lastMouseScreen.y);
                    lastMouseScreen = now;
                    repaint();
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // ===== Public API =====

    public void setRoomSize(int width, int height) {
        this.roomWidth = Math.max(100, width);
        this.roomHeight = Math.max(100, height);
        repaint();
    }

    public FurnitureItem getSelected() {
        return selected;
    }

    public void deleteSelected() {
        if (selected != null) {
            items.remove(selected);
            selected = null;
            notifySelectionChanged();
            repaint();
        }
    }

    // Called by FurniturePanel
    public void addFurniture(String name) {
        int w = name.toLowerCase().contains("chair") ? 60 : 140;
        int h = name.toLowerCase().contains("chair") ? 60 : 90;

        // place near center of room
        int rx = -roomWidth / 2;
        int ry = -roomHeight / 2;

        int x = rx + (roomWidth - w) / 2;
        int y = ry + (roomHeight - h) / 2;

        FurnitureItem item = new FurnitureItem(
                UUID.randomUUID().toString(),
                name,
                x, y, w, h
        );

        items.add(item);
        selected = item;

        notifySelectionChanged();
        repaint();
    }

    // ===== Paint =====

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Center of screen
        double cx = getWidth() / 2.0;
        double cy = getHeight() / 2.0;

        // View transform: translate then zoom
        AffineTransform view = new AffineTransform();
        view.translate(cx + panX, cy + panY);
        view.scale(zoom, zoom);
        g2.transform(view);

        drawGrid(g2);
        drawRoom(g2);
        drawFurniture(g2);

        g2.dispose();

        // Overlay hint (screen space)
        Graphics2D overlay = (Graphics2D) g.create();
        overlay.setColor(new Color(0, 0, 0, 160));
        overlay.setFont(overlay.getFont().deriveFont(12f));
        overlay.drawString("Wheel: zoom  |  Drag empty space: pan  |  Drag item: move", 14, getHeight() - 14);
        overlay.dispose();
    }

    private void drawRoom(Graphics2D g2) {
        int x = -roomWidth / 2;
        int y = -roomHeight / 2;

        g2.setColor(new Color(245, 245, 245));
        g2.fillRect(x, y, roomWidth, roomHeight);

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(60, 60, 60));
        g2.drawRect(x, y, roomWidth, roomHeight);

        g2.setColor(new Color(80, 80, 80));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("Room (2D)", x + 10, y + 22);
    }

    private void drawFurniture(Graphics2D g2) {
        for (FurnitureItem item : items) {
            Rectangle r = item.getBounds();

            // fill
            g2.setColor(item.getColor());
            g2.fillRect(r.x, r.y, r.width, r.height);

            // outline
            if (item == selected) {
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(30, 120, 255));
            } else {
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(120, 120, 120));
            }
            g2.drawRect(r.x, r.y, r.width, r.height);

            // label
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setColor(new Color(50, 50, 50));
            g2.drawString(item.getName(), r.x + 6, r.y + 18);
        }
    }

    private void drawGrid(Graphics2D g2) {
        int grid = 50;
        int halfW = 2000;
        int halfH = 2000;

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(230, 230, 230));

        for (int x = -halfW; x <= halfW; x += grid) g2.drawLine(x, -halfH, x, halfH);
        for (int y = -halfH; y <= halfH; y += grid) g2.drawLine(-halfW, y, halfW, y);

        g2.setColor(new Color(210, 210, 210));
        g2.drawLine(-halfW, 0, halfW, 0);
        g2.drawLine(0, -halfH, 0, halfH);
    }

    // ===== Zoom / Hit Test / Coordinate Helpers =====

    private void handleZoom(MouseWheelEvent e) {
        double oldZoom = zoom;
        if (e.getPreciseWheelRotation() < 0) zoom *= 1.1;
        else zoom /= 1.1;

        zoom = clamp(zoom, 0.3, 3.0);

        // Zoom around pointer
        Point p = e.getPoint();
        double dx = p.x - getWidth() / 2.0;
        double dy = p.y - getHeight() / 2.0;

        panX = panX - dx * (zoom - oldZoom) / (oldZoom);
        panY = panY - dy * (zoom - oldZoom) / (oldZoom);

        repaint();
    }

    private FurnitureItem hitTest(Point worldPoint) {
        // check top-most first
        for (int i = items.size() - 1; i >= 0; i--) {
            FurnitureItem it = items.get(i);
            if (it.getBounds().contains(worldPoint)) return it;
        }
        return null;
    }

    private Point screenToWorld(Point screen) {
        double cx = getWidth() / 2.0;
        double cy = getHeight() / 2.0;

        double wx = (screen.x - (cx + panX)) / zoom;
        double wy = (screen.y - (cy + panY)) / zoom;

        return new Point((int) Math.round(wx), (int) Math.round(wy));
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public int getRoomWidth() { return roomWidth; }
public int getRoomHeight() { return roomHeight; }

public java.util.List<lk.group40.frdv.model.FurnitureItem> getItemsSnapshot() {
    return new java.util.ArrayList<>(items);
}

}
