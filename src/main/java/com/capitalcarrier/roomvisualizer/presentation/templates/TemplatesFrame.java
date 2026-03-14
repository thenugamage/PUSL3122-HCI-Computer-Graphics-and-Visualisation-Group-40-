package com.capitalcarrier.roomvisualizer.presentation.templates;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.domain.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class TemplatesFrame extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────
    private static final Color BG        = new Color(8,  12, 35);
    private static final Color NAV_BG    = new Color(5,   8, 25);
    private static final Color CARD_BG   = new Color(14, 20, 55);
    private static final Color CARD_BORD = new Color(35, 45, 90);
    private static final Color PURPLE    = new Color(108, 55, 220);
    private static final Color PURPLE_LT = new Color(130, 75, 245);
    private static final Color TXT_PRI   = Color.WHITE;
    private static final Color TXT_SEC   = new Color(140, 148, 175);
    private static final Color TXT_MUT   = new Color(90,  98, 130);
    private static final Color BADGE_BG  = new Color(60,  30, 130);

    // ── Template data: name, desc, width, length, height, wallColor, floorColor, type ──
    private static final Object[][] ROOMS = {
        {"Living Room",  "Standard living room layout", 6.0, 8.0, 3.0,  new Color(220,210,190), new Color(160,135,100), "LIVING"},
        {"Bedroom",      "Standard bedroom layout",     4.5, 5.0, 2.8,  new Color(210,205,195), new Color(140,120, 95), "BEDROOM"},
        {"Home Office",  "Standard office layout",      3.5, 4.0, 2.8,  new Color(200,205,215), new Color(130,125,115), "OFFICE"},
        {"Dining Room",  "Standard dining room layout", 5.0, 6.0, 3.0,  new Color(225,215,200), new Color(155,130, 95), "DINING"},
        {"Kitchen",      "Standard kitchen layout",     4.0, 5.0, 2.8,  new Color(220,220,215), new Color(170,155,130), "KITCHEN"},
        {"Bathroom",     "Standard bathroom layout",    3.0, 3.5, 2.6,  new Color(205,210,220), new Color(140,145,158), "BATHROOM"},
    };

    // ── Constructor ───────────────────────────────────────────────────────
    public TemplatesFrame() {
        setTitle("Room Visualizer – Templates");
        setSize(1280, 860);
        setMinimumSize(new Dimension(1024, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildNav(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(
            buildContent(),
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        root.add(scroll, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Navbar ────────────────────────────────────────────────────────────
    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(NAV_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 18));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        nav.setOpaque(false);
        nav.setPreferredSize(new Dimension(0, 56));
        nav.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Left: logo + nav items
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        left.setOpaque(false);
        left.add(buildLogo());
        left.add(Box.createHorizontalStrut(16));
        String[] labels = {"My Designs", "New Design", "Templates", "Settings"};
        for (String lbl : labels) left.add(buildNavItem(lbl, lbl.equals("Templates")));

        // Right: user chip
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        User user = AuthService.getCurrentUser();
        String name = (user != null && user.getFullName() != null) ? user.getFullName() : "User";
        String init = name.substring(0, 1).toUpperCase();

        JLabel userName = new JLabel(name);
        userName.setFont(new Font("Inter", Font.PLAIN, 13));
        userName.setForeground(TXT_SEC);

        JLabel avatar = new JLabel(init) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE);
                g2.fillOval(0, (getHeight() - 28) / 2, 28, 28);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Inter", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (28 - fm.stringWidth(getText())) / 2, (getHeight() - 28) / 2 + 19);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(28, 56); }
        };
        right.add(userName);
        right.add(avatar);

        nav.add(left, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private JLabel buildLogo() {
        return new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, (getHeight() - 22) / 2, 22, 22, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Inter", Font.BOLD, 11));
                g2.drawString("R", 7, (getHeight() - 22) / 2 + 15);
                g2.setFont(new Font("Inter", Font.BOLD, 15));
                g2.setColor(TXT_PRI);
                g2.drawString("Room Visualizer", 28, getHeight() / 2 + 5);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(185, 56); }
        };
    }

    private JLabel buildNavItem(String text, boolean active) {
        JLabel lbl = new JLabel(text) {
            boolean hov = false;
            { if (!active) addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(new Color(80, 45, 180));
                    g2.fillRoundRect(0, (getHeight() - 30) / 2, getWidth(), 30, 8, 8);
                }
                g2.setFont(new Font("Inter", Font.PLAIN, 13));
                g2.setColor(active || hov ? TXT_PRI : TXT_SEC);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, getHeight() / 2 + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width + 28, 56);
            }
        };
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (!active) {
            lbl.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(TemplatesFrame.this,
                        text + " — coming soon!", "Navigation", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return lbl;
    }

    // ── Main content ──────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(32, 36, 36, 36));

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 28, 0));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        JLabel title = new JLabel("Templates");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(TXT_PRI);
        JLabel sub = new JLabel("Quick-start templates with preset dimensions");
        sub.setFont(new Font("Inter", Font.PLAIN, 14));
        sub.setForeground(TXT_SEC);
        sub.setBorder(new EmptyBorder(5, 0, 0, 0));
        titles.add(title);
        titles.add(sub);

        JButton createBtn = buildPurpleButton("  + Create New Preset  ");
        createBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Custom preset creator coming soon!", "New Preset", JOptionPane.INFORMATION_MESSAGE));

        header.add(titles, BorderLayout.WEST);
        header.add(createBtn, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Card grid
        JPanel grid = new JPanel(new GridLayout(0, 3, 22, 22));
        grid.setBackground(BG);
        for (Object[] room : ROOMS) grid.add(buildCard(room));
        content.add(grid, BorderLayout.CENTER);

        return content;
    }

    // ── Template card ─────────────────────────────────────────────────────
    private JPanel buildCard(Object[] t) {
        String name   = (String) t[0];
        String desc   = (String) t[1];
        double w      = (double) t[2], l = (double) t[3], h = (double) t[4];
        Color wall    = (Color)  t[5];
        Color floor   = (Color)  t[6];
        String type   = (String) t[7];

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(CARD_BORD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        // Preview image (drawn with Java2D)
        JPanel preview = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Clip to top-rounded rectangle only
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() * 2, 16, 16));
                paintRoom(g2, type, getWidth(), getHeight(), wall, floor);
                g2.dispose();
            }
        };
        preview.setPreferredSize(new Dimension(0, 185));
        preview.setOpaque(false);

        // Info panel
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(14, 16, 16, 16));

        // Name + badge row
        JPanel nameRow = new JPanel(new BorderLayout(8, 0));
        nameRow.setOpaque(false);
        JLabel nameL = new JLabel(name);
        nameL.setFont(new Font("Inter", Font.BOLD, 16));
        nameL.setForeground(TXT_PRI);
        JLabel badge = new JLabel("Preset") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BADGE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(180, 140, 255));
                g2.setFont(new Font("Inter", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Preset", (getWidth() - fm.stringWidth("Preset")) / 2, 13);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(50, 20); }
        };
        nameRow.add(nameL, BorderLayout.WEST);
        nameRow.add(badge, BorderLayout.EAST);

        JLabel descL = new JLabel(desc);
        descL.setFont(new Font("Inter", Font.PLAIN, 12));
        descL.setForeground(TXT_SEC);
        descL.setBorder(new EmptyBorder(4, 0, 12, 0));

        // Dimensions row
        JPanel dims = new JPanel(new GridLayout(1, 3));
        dims.setOpaque(false);
        dims.setBorder(new EmptyBorder(0, 0, 12, 0));
        dims.add(dimBox("Width",  w + "m"));
        dims.add(dimBox("Length", l + "m"));
        dims.add(dimBox("Height", h + "m"));

        // Swatches
        JPanel swatches = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        swatches.setOpaque(false);
        swatches.setBorder(new EmptyBorder(0, 0, 14, 0));
        swatches.add(swatch(wall, "Walls"));
        swatches.add(swatch(floor, "Floor"));

        // Buttons
        JPanel btns = new JPanel(new BorderLayout(8, 0));
        btns.setOpaque(false);

        JButton useBtn = buildPurpleButton("Use This Room  →");
        useBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Opening \"" + name + "\" template.\nEditor coming soon!", "Use Template", JOptionPane.INFORMATION_MESSAGE));

        JButton editBtn = new JButton("Edit") {
            boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(55, 60, 105) : new Color(38, 42, 88));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.setColor(new Color(180, 180, 220));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                g2.setFont(new Font("Inter", Font.BOLD, 12));
                g2.setColor(TXT_PRI);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Edit", (getWidth() - fm.stringWidth("Edit")) / 2, getHeight() / 2 + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
        };
        editBtn.setPreferredSize(new Dimension(62, 36));
        editBtn.setBorderPainted(false); editBtn.setContentAreaFilled(false); editBtn.setFocusPainted(false);
        editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Editing \"" + name + "\" — coming soon!", "Edit Template", JOptionPane.INFORMATION_MESSAGE));

        btns.add(useBtn, BorderLayout.CENTER);
        btns.add(editBtn, BorderLayout.EAST);

        info.add(nameRow);
        info.add(descL);
        info.add(dims);
        info.add(swatches);
        info.add(btns);

        card.add(preview, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private JButton buildPurpleButton(String text) {
        JButton btn = new JButton(text) {
            boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hov ? PURPLE_LT : PURPLE, c2 = hov ? new Color(150,100,255) : PURPLE_LT;
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setFont(new Font("Inter", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, getHeight() / 2 + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 38));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel dimBox(String label, String value) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Inter", Font.PLAIN, 10)); lbl.setForeground(TXT_MUT);
        JLabel val = new JLabel(value); val.setFont(new Font("Inter", Font.BOLD, 13)); val.setForeground(TXT_PRI);
        val.setBorder(new EmptyBorder(2, 0, 0, 0));
        box.add(lbl); box.add(val);
        return box;
    }

    private JPanel swatch(Color color, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JLabel dot = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color); g2.fillRoundRect(0, (getHeight()-16)/2, 16, 16, 4, 4);
                g2.setColor(new Color(255,255,255,40)); g2.drawRoundRect(0, (getHeight()-16)/2, 15, 15, 4, 4);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(16, 20); }
        };
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Inter", Font.PLAIN, 11)); lbl.setForeground(TXT_SEC);
        p.add(dot); p.add(lbl);
        return p;
    }

    // ── Room preview renderer ─────────────────────────────────────────────
    private void paintRoom(Graphics2D g2, String type, int w, int h, Color wall, Color floor) {
        // Background gradient
        g2.setPaint(new GradientPaint(0, 0, new Color(20,15,45), w, h, new Color(35,25,70)));
        g2.fillRect(0, 0, w, h);

        // Perspective walls
        int[] flrX = {0, w, (int)(w*.78), (int)(w*.22)}, flrY = {h, h, (int)(h*.55), (int)(h*.55)};
        int[] bkX  = {(int)(w*.22),(int)(w*.78),(int)(w*.78),(int)(w*.22)}, bkY = {(int)(h*.55),(int)(h*.55),(int)(h*.10),(int)(h*.10)};
        int[] lftX = {0,(int)(w*.22),(int)(w*.22),0},  lftY = {h,(int)(h*.55),(int)(h*.10),(int)(h*.02)};
        int[] rgtX = {(int)(w*.78),w,w,(int)(w*.78)},  rgtY = {(int)(h*.55),h,(int)(h*.02),(int)(h*.10)};

        g2.setColor(floor);    g2.fillPolygon(flrX, flrY, 4);
        g2.setColor(wall);     g2.fillPolygon(bkX,  bkY,  4);
        g2.setColor(wall.darker()); g2.fillPolygon(lftX, lftY, 4);
        g2.setColor(new Color(Math.max(0,wall.getRed()-15), Math.max(0,wall.getGreen()-15), Math.max(0,wall.getBlue()-15)));
        g2.fillPolygon(rgtX, rgtY, 4);
        g2.setColor(new Color(255,255,255,25)); g2.fillPolygon(new int[]{0,w,(int)(w*.78),(int)(w*.22)}, new int[]{(int)(h*.02),(int)(h*.02),(int)(h*.10),(int)(h*.10)}, 4);

        switch (type) {
            case "LIVING"  -> drawLiving(g2, w, h);
            case "BEDROOM" -> drawBedroom(g2, w, h);
            case "OFFICE"  -> drawOffice(g2, w, h);
            case "DINING"  -> drawDining(g2, w, h);
            case "KITCHEN" -> drawKitchen(g2, w, h);
            case "BATHROOM"-> drawBathroom(g2, w, h);
        }

        // Depth shadow
        g2.setPaint(new GradientPaint(0, (int)(h*.55), new Color(0,0,0,70), 0, h, new Color(0,0,0,0)));
        g2.fillRect(0, (int)(h*.55), w, h);
    }

    private void drawLiving(Graphics2D g, int w, int h) {
        // Sofa
        g.setColor(new Color(90,70,130)); g.fillRoundRect((int)(w*.20),(int)(h*.61),(int)(w*.45),(int)(h*.19),8,8);
        g.setColor(new Color(110,90,150)); g.fillRoundRect((int)(w*.20),(int)(h*.58),(int)(w*.45),(int)(h*.06),6,6);
        g.setColor(new Color(70,50,25)); g.fillRect((int)(w*.21),(int)(h*.79),5,8); g.fillRect((int)(w*.62),(int)(h*.79),5,8);
        // Coffee table
        g.setColor(new Color(130,100,60)); g.fillRoundRect((int)(w*.29),(int)(h*.73),(int)(w*.28),(int)(h*.07),4,4);
        // Plant
        g.setColor(new Color(55,95,55)); g.fillOval((int)(w*.72),(int)(h*.40),22,20);
        g.setColor(new Color(70,50,25)); g.fillRect((int)(w*.73)+5,(int)(h*.58),7,18);
        // Window glow
        g.setColor(new Color(255,220,150,70)); g.fillRect((int)(w*.36),(int)(h*.14),(int)(w*.28),(int)(h*.30));
        g.setColor(new Color(255,255,255,40)); g.drawRect((int)(w*.36),(int)(h*.14),(int)(w*.28),(int)(h*.30));
    }

    private void drawBedroom(Graphics2D g, int w, int h) {
        g.setColor(new Color(75,65,105)); g.fillRoundRect((int)(w*.25),(int)(h*.56),(int)(w*.50),(int)(h*.28),6,6);
        g.setColor(new Color(215,210,195)); g.fillRoundRect((int)(w*.26),(int)(h*.54),(int)(w*.48),(int)(h*.27),5,5);
        g.setColor(Color.WHITE); g.fillRoundRect((int)(w*.30),(int)(h*.55),(int)(w*.17),(int)(h*.10),6,6); g.fillRoundRect((int)(w*.52),(int)(h*.55),(int)(w*.17),(int)(h*.10),6,6);
        g.setColor(new Color(95,75,115)); g.fillRoundRect((int)(w*.25),(int)(h*.43),(int)(w*.50),(int)(h*.13),8,8);
        g.setColor(new Color(130,100,65)); g.fillRect((int)(w*.15),(int)(h*.61),(int)(w*.08),(int)(h*.18));
        g.setColor(new Color(195,175,95)); g.fillOval((int)(w*.16),(int)(h*.57),18,10);
        g.setColor(new Color(195,225,255,55)); g.fillRect((int)(w*.38),(int)(h*.12),(int)(w*.24),(int)(h*.28));
    }

    private void drawOffice(Graphics2D g, int w, int h) {
        g.setColor(new Color(110,90,70)); g.fillRect((int)(w*.24),(int)(h*.60),(int)(w*.52),(int)(h*.09));
        g.setColor(new Color(75,60,45)); g.fillRect((int)(w*.25),(int)(h*.68),5,(int)(h*.18)); g.fillRect((int)(w*.73),(int)(h*.68),5,(int)(h*.18));
        g.setColor(new Color(38,38,48)); g.fillRoundRect((int)(w*.37),(int)(h*.36),(int)(w*.26),(int)(h*.23),4,4);
        g.setColor(new Color(55,75,115)); g.fillRect((int)(w*.38),(int)(h*.37),(int)(w*.24),(int)(h*.20));
        g.setColor(new Color(48,42,52)); g.fillRect((int)(w*.48),(int)(h*.58),10,7);
        g.setColor(new Color(65,60,80)); g.fillOval((int)(w*.41),(int)(h*.70),(int)(w*.18),(int)(h*.13)); g.fillRect((int)(w*.43),(int)(h*.63),(int)(w*.14),(int)(h*.09));
        g.setColor(new Color(105,85,60)); g.fillRect((int)(w*.72),(int)(h*.22),(int)(w*.06),(int)(h*.36));
        int[] bc = {0xFF4A4A, 0x4A8AFF, 0x4AFF8A, 0xFFAA4A};
        for (int i=0;i<4;i++) { g.setColor(new Color(bc[i])); g.fillRect((int)(w*.725),(int)(h*(.24+i*.07)),(int)(w*.05),(int)(h*.05)); }
    }

    private void drawDining(Graphics2D g, int w, int h) {
        g.setColor(new Color(145,105,55)); g.fillOval((int)(w*.27),(int)(h*.57),(int)(w*.46),(int)(h*.20));
        g.setColor(new Color(170,135,75)); g.fillOval((int)(w*.30),(int)(h*.55),(int)(w*.40),(int)(h*.15));
        g.setColor(new Color(175,75,55));
        g.fillOval((int)(w*.33),(int)(h*.44),(int)(w*.13),(int)(h*.11)); g.fillOval((int)(w*.54),(int)(h*.44),(int)(w*.13),(int)(h*.11));
        g.fillOval((int)(w*.33),(int)(h*.72),(int)(w*.13),(int)(h*.11)); g.fillOval((int)(w*.54),(int)(h*.72),(int)(w*.13),(int)(h*.11));
        g.setColor(new Color(75,135,75)); g.fillOval((int)(w*.46),(int)(h*.60),15,13);
    }

    private void drawKitchen(Graphics2D g, int w, int h) {
        g.setColor(new Color(155,125,85)); g.fillRect((int)(w*.22),(int)(h*.57),(int)(w*.56),(int)(h*.29));
        g.setColor(new Color(195,195,190)); g.fillRect((int)(w*.22),(int)(h*.54),(int)(w*.56),(int)(h*.05));
        g.setColor(new Color(185,155,105));
        for(int i=0;i<4;i++){
            g.fillRoundRect((int)(w*.24+i*w*.13),(int)(h*.60),(int)(w*.11),(int)(h*.22),3,3);
            g.setColor(new Color(165,135,85)); g.drawRoundRect((int)(w*.24+i*w*.13),(int)(h*.60),(int)(w*.11),(int)(h*.22),3,3);
            g.setColor(new Color(185,155,105));
        }
        g.setColor(new Color(180,155,100)); g.fillRect((int)(w*.22),(int)(h*.17),(int)(w*.56),(int)(h*.20));
        g.setColor(new Color(175,180,185)); g.fillRoundRect((int)(w*.42),(int)(h*.53),(int)(w*.18),(int)(h*.04),2,2);
    }

    private void drawBathroom(Graphics2D g, int w, int h) {
        g.setColor(new Color(225,225,238)); g.fillRoundRect((int)(w*.27),(int)(h*.51),(int)(w*.45),(int)(h*.29),10,10);
        g.setColor(new Color(175,205,238)); g.fillRoundRect((int)(w*.29),(int)(h*.53),(int)(w*.41),(int)(h*.25),8,8);
        g.setColor(new Color(175,180,198)); g.fillRoundRect((int)(w*.46),(int)(h*.49),14,5,3,3);
        g.setColor(new Color(232,232,238)); g.fillRoundRect((int)(w*.65),(int)(h*.58),(int)(w*.12),(int)(h*.22),8,8);
        g.setColor(new Color(215,215,228)); g.fillRoundRect((int)(w*.64),(int)(h*.55),(int)(w*.14),(int)(h*.06),5,5);
        g.setColor(new Color(222,222,232)); g.fillRoundRect((int)(w*.21),(int)(h*.58),(int)(w*.12),(int)(h*.14),4,4);
        g.setColor(new Color(255,255,255,18));
        for(int i=0;i<3;i++) for(int j=0;j<2;j++)
            g.fillRect((int)(w*.33+i*w*.10),(int)(h*.12+j*h*.17),(int)(w*.09),(int)(h*.14));
    }
}
