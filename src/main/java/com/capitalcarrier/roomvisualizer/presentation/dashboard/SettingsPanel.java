package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.config.ThemeConfig;
import com.capitalcarrier.roomvisualizer.domain.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setBackground(ThemeConfig.BACKGROUND);
        setLayout(new BorderLayout());

        // Header Section
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 40));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Content Section
        JPanel content = new JPanel();
        content.setBackground(ThemeConfig.BACKGROUND);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, 40, 40, 40));

        content.add(buildAccountSection());
        content.add(Box.createVerticalStrut(30));
        content.add(buildEditorSection());
        content.add(Box.createVerticalStrut(30));
        content.add(buildDataSection());
        content.add(Box.createVerticalStrut(50));
        content.add(buildFooter());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(ThemeConfig.BACKGROUND);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildAccountSection() {
        User user = AuthService.getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "user@example.com";
        String userName = user != null ? user.getUsername() : "Username";

        JPanel card = createGlassCard("Account Settings", "user");
        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);

        rows.add(createRow("Email Address", userEmail, "Change Email", ThemeConfig.ACCENT_PURPLE));
        rows.add(createDivider());
        rows.add(createRow("Password", "••••••••••••", "Change Password", ThemeConfig.ACCENT_PURPLE));
        rows.add(createDivider());
        rows.add(createRow("Username", userName, null, null));

        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildEditorSection() {
        JPanel card = createGlassCard("Editor Preferences", "settings");
        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);

        rows.add(createControlRow("Measurement Units", new String[]{"Meters (m)", "Feet (ft)", "Centimeters (cm)"}));
        rows.add(createDivider());
        rows.add(createControlRow("Grid Spacing", new String[]{"0.1m", "0.5m", "1.0m"}));
        rows.add(createDivider());
        
        JPanel toggleRow = new JPanel(new BorderLayout());
        toggleRow.setOpaque(false);
        JLabel label = new JLabel("Auto-save Designs");
        label.setFont(new Font("Inter", Font.PLAIN, 15));
        label.setForeground(ThemeConfig.GLASS_TEXT);
        JCheckBox toggle = new JCheckBox();
        toggle.setSelected(true);
        toggle.setOpaque(false);
        toggleRow.add(label, BorderLayout.WEST);
        toggleRow.add(toggle, BorderLayout.EAST);
        rows.add(toggleRow);

        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildDataSection() {
        JPanel card = createGlassCard("Data Management", "data");
        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);

        rows.add(createRow("Export Data", "Download a local backup of all designs.", "Download ZIP", ThemeConfig.ACCENT_CYAN));
        rows.add(createDivider());
        rows.add(createRow("Clear All Data", "Permanently delete all saved designs.", "Delete All", new Color(255, 70, 70)));

        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel createGlassCard(String title, String iconType) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glass Body
                g2.setColor(ThemeConfig.GLASS_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ThemeConfig.CARD_ROUNDING, ThemeConfig.CARD_ROUNDING));
                
                // Border highlight
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(ThemeConfig.GLASS_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, ThemeConfig.CARD_ROUNDING, ThemeConfig.CARD_ROUNDING));
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(20, 25));
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setMaximumSize(new Dimension(1000, 400));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);
        
        // Section Icon (Simple Geometric Representation)
        JPanel iconPlaceholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeConfig.ACCENT_PURPLE);
                if (iconType.equals("user")) {
                    g2.fillOval(4, 2, 10, 10);
                    g2.fillArc(0, 12, 18, 12, 0, 180);
                } else if (iconType.equals("settings")) {
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(4, 4, 10, 10);
                    for(int i=0; i<8; i++) {
                        g2.rotate(Math.toRadians(45), 9, 9);
                        g2.fillRect(8, 0, 2, 4);
                    }
                } else {
                    g2.fillRect(2, 2, 14, 4);
                    g2.fillRect(2, 8, 14, 4);
                    g2.fillRect(2, 14, 14, 4);
                }
                g2.dispose();
            }
        };
        iconPlaceholder.setPreferredSize(new Dimension(24, 24));
        iconPlaceholder.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        header.add(iconPlaceholder, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        
        card.add(header, BorderLayout.NORTH);
        return card;
    }

    private JPanel createRow(String label, String value, JButton action) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Inter", Font.BOLD, 14));
        lbl.setForeground(ThemeConfig.GLASS_SUBTEXT);
        
        JLabel val = new JLabel(value);
        val.setFont(new Font("Inter", Font.PLAIN, 16));
        val.setForeground(ThemeConfig.GLASS_TEXT);
        
        info.add(lbl);
        info.add(Box.createVerticalStrut(4));
        info.add(val);

        row.add(info, BorderLayout.CENTER);
        if (action != null) {
            row.add(action, BorderLayout.EAST);
        }

        return row;
    }

    private JPanel createRow(String label, String value, String actionText, Color actionColor) {
        return createRow(label, value, actionText != null ? createActionButton(actionText, actionColor) : null);
    }

    private JPanel createControlRow(String labelText, String[] options) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Inter", Font.BOLD, 15));
        label.setForeground(ThemeConfig.GLASS_TEXT);
        
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setBackground(ThemeConfig.DEEP_NAVY);
        combo.setForeground(Color.WHITE);
        combo.setPreferredSize(new Dimension(180, 36));
        
        row.add(label, BorderLayout.WEST);
        row.add(combo, BorderLayout.EAST);
        return row;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ThemeConfig.PILL_ROUNDING, ThemeConfig.PILL_ROUNDING));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(10, 22, 10, 22));
        return btn;
    }

    private JComponent createDivider() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(255, 255, 255, 20));
        s.setBackground(new Color(255, 255, 255, 20));
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 0, 15, 0));
        p.add(s);
        return p;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel copy = new JLabel("Furniture Room Visualizer v1.0.0");
        copy.setFont(new Font("Inter", Font.BOLD, 14));
        copy.setForeground(ThemeConfig.GLASS_SUBTEXT);
        copy.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rights = new JLabel("© 2026 Professional Interior Design Suite. All rights reserved.");
        rights.setFont(new Font("Inter", Font.PLAIN, 12));
        rights.setForeground(new Color(ThemeConfig.GLASS_SUBTEXT.getRed(), ThemeConfig.GLASS_SUBTEXT.getGreen(), ThemeConfig.GLASS_SUBTEXT.getBlue(), 150));
        rights.setAlignmentX(Component.CENTER_ALIGNMENT);

        footer.add(copy);
        footer.add(Box.createVerticalStrut(8));
        footer.add(rights);
        return footer;
    }
}
