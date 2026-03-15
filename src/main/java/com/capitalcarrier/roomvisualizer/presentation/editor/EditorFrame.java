package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.capitalcarrier.roomvisualizer.application.design.DesignService;
import com.capitalcarrier.roomvisualizer.presentation.components.PrimaryButton;
import com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditorFrame extends JFrame {
    private Room room;
    private RoomCanvas2DPanel canvas2D;
    private RoomViewport3DPanel viewport3D;
    
    private static final Color DARK_BG = new Color(15, 20, 45);
    private static final Color NAV_BG = new Color(10, 15, 35);
    private static final Color SIDEBAR_BG = new Color(12, 18, 40);
    private static final Color ACCENT = new Color(153, 51, 255);
    private static final Color INPUT_BG = new Color(25, 30, 60);

    public EditorFrame(Room room) {
        this.room = room;
        setTitle("Room Visualizer - Editor");
        setSize(1400, 950);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(DARK_BG);

        // Navbar (Lanka Furniture Theme)
        add(createNavbar(), BorderLayout.NORTH);

        // Sidebar Left (Config)
        add(createLeftSidebar(), BorderLayout.WEST);

        // Sidebar Right (Catalog)
        add(createRightSidebar(), BorderLayout.EAST);

        // Main Center Area (Grid & Controls)
        add(createCenterArea(), BorderLayout.CENTER);
    }

    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(NAV_BG);
        nav.setPreferredSize(new Dimension(0, 64));
        nav.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        left.setOpaque(false);
        JLabel logo = new JLabel("Room Visualizer");
        logo.setFont(new Font("Inter", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        left.add(logo);

        String[] menuItems = {"My Designs", "New Design", "My Rooms", "Settings"};
        for (String item : menuItems) {
            JLabel lbl = new JLabel(item);
            lbl.setFont(new Font("Inter", Font.PLAIN, 14));
            lbl.setForeground(item.equals("New Design") ? ACCENT : new Color(140, 148, 175));
            left.add(lbl);
        }

        nav.add(left, BorderLayout.WEST);
        return nav;
    }

    private JPanel createLeftSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(new EmptyBorder(30, 25, 30, 25));

        // Room Name
        sidebar.add(createSectionHeader("Room Name"));
        JTextField nameField = new JTextField("New Design");
        styleInput(nameField);
        sidebar.add(nameField);
        sidebar.add(Box.createVerticalStrut(25));

        // Dimensions
        sidebar.add(createSectionHeader("Dimensions"));
        JPanel dimGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        dimGrid.setOpaque(false);
        dimGrid.add(createInputLabel("Width (m)"));
        dimGrid.add(createInputLabel("Length (m)"));
        JTextField wField = new JTextField(String.valueOf(room.getWidth()));
        JTextField lField = new JTextField(String.valueOf(room.getLength()));
        styleInput(wField);
        styleInput(lField);
        dimGrid.add(wField);
        dimGrid.add(lField);
        sidebar.add(dimGrid);
        
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createInputLabel("Height (m)"));
        JTextField hField = new JTextField(String.valueOf(room.getHeight()));
        styleInput(hField);
        sidebar.add(hField);
        sidebar.add(Box.createVerticalStrut(25));

        // Colors
        sidebar.add(createSectionHeader("Colors"));
        sidebar.add(createInputLabel("Wall Color"));
        sidebar.add(createColorSwatches(new String[]{"#FFFFFF", "#AEB4FF", "#C0C0C0", "#EAEAEA", "#F5F5DC", "#D2B48C"}, false));
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createInputLabel("Floor Color"));
        sidebar.add(createColorSwatches(new String[]{"#6D4C41", "#A1887F", "#4E342E", "#F5F5F5", "#D7CCC8", "#8D6E63"}, true));
        
        sidebar.add(Box.createVerticalGlue());

        // Save & Back
        PrimaryButton saveBtn = new PrimaryButton("Save Design");
        saveBtn.addActionListener(e -> {
            try {
                DesignService.saveDesign(nameField.getText(), room);
                JOptionPane.showMessageDialog(this, "Design Saved!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        sidebar.add(saveBtn);
        sidebar.add(Box.createVerticalStrut(12));
        
        JButton backBtn = new JButton("Back to Designs");
        backBtn.setBackground(new Color(45, 50, 80));
        backBtn.setForeground(Color.WHITE);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(250, 45));
        backBtn.addActionListener(e -> {
            new DashboardFrame().setVisible(true);
            dispose();
        });
        sidebar.add(backBtn);

        return sidebar;
    }

    private JPanel createRightSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(320, 0));

        // Tabs
        JPanel tabs = new JPanel(new GridLayout(1, 2));
        tabs.setBackground(NAV_BG);
        tabs.setPreferredSize(new Dimension(0, 48));
        tabs.add(createTabButton("Catalog", true));
        tabs.add(createTabButton("Properties", false));
        sidebar.add(tabs, BorderLayout.NORTH);

        // Catalog Content
        JPanel catalog = new JPanel();
        catalog.setLayout(new BoxLayout(catalog, BoxLayout.Y_AXIS));
        catalog.setBackground(SIDEBAR_BG);
        catalog.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel catHeader = new JLabel("Category");
        catHeader.setFont(new Font("Inter", Font.BOLD, 14));
        catHeader.setForeground(Color.WHITE);
        catalog.add(catHeader);
        catalog.add(Box.createVerticalStrut(15));

        // Category Grid (Simulated)
        JPanel catGrid = new JPanel(new GridLayout(2, 3, 10, 10));
        catGrid.setOpaque(false);
        String[][] categories = {{"Seating", "#AEB4FF"}, {"Tables", "#A1887F"}, {"Storage", "#FFFFFF"}, {"Beds", "#F5F5DC"}, {"Decor", "#D7CCC8"}, {"Lighting", "#EAEAEA"}};
        for (String[] cat : categories) {
            JButton catBtn = new JButton(cat[0]);
            catBtn.setBackground(new Color(35, 40, 70));
            catBtn.setForeground(Color.WHITE);
            catBtn.setFont(new Font("Inter", Font.PLAIN, 12));
            catGrid.add(catBtn);
        }
        catalog.add(catGrid);
        catalog.add(Box.createVerticalStrut(30));

        // Items List
        String[][] furniture = {
            {"Modern Sofa", "2.2m x 0.9m x 0.8m", "2.2", "0.9", "0.8"},
            {"Armchair", "0.9m x 0.9m x 0.9m", "0.9", "0.9", "0.9"},
            {"Dining Chair", "0.5m x 0.5m x 0.9m", "0.5", "0.5", "0.9"},
            {"Ottoman", "0.8m x 0.8m x 0.4m", "0.8", "0.8", "0.4"},
            {"Bench", "1.5m x 0.4m x 0.5m", "1.5", "0.4", "0.5"}
        };

        for (String[] f : furniture) {
            JPanel itemBox = new JPanel(new BorderLayout(10, 5));
            itemBox.setOpaque(false);
            itemBox.setMaximumSize(new Dimension(280, 70));
            itemBox.setBorder(new EmptyBorder(10, 0, 10, 0));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            
            JLabel name = new JLabel(f[0]);
            name.setForeground(Color.WHITE);
            name.setFont(new Font("Inter", Font.BOLD, 13));
            
            JLabel desc = new JLabel(f[1]);
            desc.setForeground(new Color(140, 148, 175));
            desc.setFont(new Font("Inter", Font.PLAIN, 11));
            
            textPanel.add(name);
            textPanel.add(desc);
            
            JButton addBtn = new JButton("+");
            styleCatalogAddButton(addBtn);
            addBtn.addActionListener(e -> {
                com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem item = new com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem();
                item.setName(f[0]);
                item.setWidth(Double.parseDouble(f[2]));
                item.setHeight(Double.parseDouble(f[4]));
                item.setDepth(Double.parseDouble(f[3]));
                item.setX(room.getWidth() / 2);
                item.setZ(room.getLength() / 2);
                room.addFurnitureItem(item);
                canvas2D.repaint();
            });

            itemBox.add(textPanel, BorderLayout.CENTER);
            itemBox.add(addBtn, BorderLayout.EAST);
            
            catalog.add(itemBox);
            catalog.add(new JSeparator(SwingConstants.HORIZONTAL) {{
                setForeground(new Color(255, 255, 255, 10));
                setMaximumSize(new Dimension(280, 1));
            }});
        }

        sidebar.add(catalog, BorderLayout.CENTER);
        return sidebar;
    }

    private void styleCatalogAddButton(JButton btn) {
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createCenterArea() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controls.setOpaque(false);
        
        JPanel toggleContainer = new JPanel(new GridLayout(1, 2));
        toggleContainer.setPreferredSize(new Dimension(160, 36));
        JButton btn2D = new JButton("2D View");
        JButton btn3D = new JButton("3D View");
        btn2D.setBackground(ACCENT); btn2D.setForeground(Color.WHITE);
        btn3D.setBackground(new Color(35, 40, 70)); btn3D.setForeground(new Color(140, 148, 175));
        toggleContainer.add(btn2D); toggleContainer.add(btn3D);
        controls.add(toggleContainer);
        
        JCheckBox snap = new JCheckBox("Snap to Grid", true);
        snap.setForeground(Color.WHITE);
        snap.setOpaque(false);
        controls.add(snap);

        center.add(controls, BorderLayout.NORTH);

        // View Area
        canvas2D = new RoomCanvas2DPanel(room);
        center.add(canvas2D, BorderLayout.CENTER);

        return center;
    }

    // Helper Styles
    private JLabel createSectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Inter", Font.BOLD, 14));
        lbl.setForeground(new Color(200, 200, 220));
        lbl.setBorder(new EmptyBorder(0, 0, 12, 0));
        return lbl;
    }

    private JLabel createInputLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Inter", Font.PLAIN, 12));
        lbl.setForeground(new Color(140, 148, 175));
        return lbl;
    }

    private void styleInput(JTextField f) {
        f.setBackground(INPUT_BG);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 60, 100)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        f.setMaximumSize(new Dimension(250, 40));
    }

    private JPanel createColorSwatches(String[] hexes, boolean isFloor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);
        for (String hex : hexes) {
            JPanel swatch = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (hex.equals(isFloor ? room.getFloorColor() : room.getWallColor())) {
                        g.setColor(ACCENT);
                        g.drawRect(0, 0, getWidth()-1, getHeight()-1);
                    }
                }
            };
            swatch.setPreferredSize(new Dimension(32, 32));
            swatch.setBackground(Color.decode(hex));
            swatch.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40)));
            swatch.setCursor(new Cursor(Cursor.HAND_CURSOR));
            swatch.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (isFloor) room.setFloorColor(hex);
                    else room.setWallColor(hex);
                    canvas2D.repaint();
                    p.repaint();
                }
            });
            p.add(swatch);
        }
        return p;
    }

    private JButton createTabButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setBackground(active ? ACCENT : NAV_BG);
        b.setForeground(Color.WHITE);
        b.setBorder(null);
        return b;
    }
}
