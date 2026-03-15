package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import com.capitalcarrier.roomvisualizer.presentation.templates.TemplatesFrame;
import com.capitalcarrier.roomvisualizer.presentation.components.PrimaryButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private static final Color BG = new Color(8, 12, 35);
    private static final Color NAV_BG = new Color(5, 8, 25);
    private static final Color TXT_PRI = Color.WHITE;
    private static final Color TXT_SEC = new Color(140, 148, 175);
    private static final Color PURPLE = new Color(108, 55, 220);

    public DashboardFrame() {
        setTitle("Lanka Furniture - My Designs");
        setSize(1280, 860);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildNav(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel title = new JLabel("My Designs");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(TXT_PRI);
        
        JLabel sub = new JLabel("Manage and view all your room designs");
        sub.setFont(new Font("Inter", Font.PLAIN, 14));
        sub.setForeground(TXT_SEC);
        sub.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        titlePanel.add(title);
        titlePanel.add(sub);
        
        JButton createBtn = new PrimaryButton("+ Create New Design");
        createBtn.addActionListener(e -> {
            new TemplatesFrame().setVisible(true);
            dispose();
        });
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(createBtn, BorderLayout.EAST);
        
        content.add(header, BorderLayout.NORTH);

        // Search & Filter Panel (Simulated)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        JTextField searchField = new JTextField("Search designs...");
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBackground(new Color(20, 25, 50));
        searchField.setForeground(TXT_SEC);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 45, 80)),
            BorderFactory.createEmptyBorder(0, 15, 0, 15)
        ));
        
        JComboBox<String> roomFilter = new JComboBox<>(new String[]{"All Rooms", "Living Room", "Bedroom", "Office"});
        roomFilter.setPreferredSize(new Dimension(150, 40));
        roomFilter.setBackground(new Color(20, 25, 50));
        roomFilter.setForeground(TXT_PRI);
        
        filterPanel.add(searchField);
        filterPanel.add(roomFilter);
        
        content.add(filterPanel, BorderLayout.CENTER);

        // Grid Area (Showing Empty State as per Screenshot)
        content.add(buildEmptyState(), BorderLayout.SOUTH);

        root.add(new JScrollPane(content) {{
            setBorder(null);
            getVerticalScrollBar().setUnitIncrement(16);
            getViewport().setBackground(BG);
        }}, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(NAV_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 10));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        nav.setPreferredSize(new Dimension(0, 64));
        nav.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        left.setOpaque(false);
        JLabel logo = new JLabel("Lanka Furniture");
        logo.setFont(new Font("Inter", Font.BOLD, 18));
        logo.setForeground(TXT_PRI);
        left.add(logo);

        String[] menu = {"My Designs", "New Design", "My Rooms", "Settings"};
        for (String m : menu) {
            JLabel lbl = new JLabel(m);
            lbl.setFont(new Font("Inter", Font.PLAIN, 14));
            lbl.setForeground(m.equals("My Designs") ? PURPLE : TXT_SEC);
            lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            left.add(lbl);
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        User user = AuthService.getCurrentUser();
        JLabel userLabel = new JLabel(user != null ? user.getFullName() : "User");
        userLabel.setForeground(TXT_SEC);
        right.add(userLabel);

        nav.add(left, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private JPanel buildEmptyState() {
        JPanel empty = new JPanel();
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
        empty.setOpaque(false);
        empty.setBorder(new EmptyBorder(100, 0, 100, 0));

        JLabel iconLabel = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 30, 60));
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(25, 25, 30, 30);
                g2.drawLine(25, 40, 55, 40);
                g2.drawLine(40, 25, 40, 55);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(80, 80); }
        };
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mainLabel = new JLabel("No designs yet");
        mainLabel.setFont(new Font("Inter", Font.BOLD, 20));
        mainLabel.setForeground(TXT_PRI);
        mainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainLabel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JLabel subLabel = new JLabel("Get started by creating your first room design");
        subLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        subLabel.setForeground(TXT_SEC);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setBorder(new EmptyBorder(10, 0, 30, 0));

        JButton createBtn = new PrimaryButton("Create New Design") {
            @Override public Dimension getPreferredSize() { return new Dimension(220, 45); }
        };
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.addActionListener(e -> {
            new TemplatesFrame().setVisible(true);
            dispose();
        });

        empty.add(iconLabel);
        empty.add(mainLabel);
        empty.add(subLabel);
        empty.add(createBtn);

        return empty;
    }
}
