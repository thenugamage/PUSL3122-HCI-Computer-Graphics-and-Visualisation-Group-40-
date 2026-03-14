package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.domain.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private Color bgColor = new Color(13, 17, 30);
    private Color cardColor = new Color(20, 26, 45);
    private Color borderColor = new Color(35, 45, 75);
    private Color accentColor = new Color(143, 85, 255);

    public SettingsPanel() {
        setBackground(bgColor);
        setLayout(new BorderLayout());
        
        // Main container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel headerLabel = new JLabel("Settings");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setIcon(UIManager.getIcon("Tree.leafIcon")); // Using a generic icon as placeholder
        
        JLabel subHeaderLabel = new JLabel("Manage your preferences and application settings");
        subHeaderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(150, 160, 180));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(bgColor);
        headerPanel.add(headerLabel);
        headerPanel.add(subHeaderLabel);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(800, 60));

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Sections
        contentPanel.add(createAccountSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createEditorPreferencesSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createDataManagementSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createFooter());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSettingsCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(800, 500));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(20));
        return card;
    }

    private JPanel createRow(String label, String valueOrDesc, JComponent control) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(cardColor);
        row.setMaximumSize(new Dimension(800, 60));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(cardColor);
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel(valueOrDesc);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(new Color(150, 160, 180));
        
        infoPanel.add(titleLabel);
        infoPanel.add(descLabel);

        row.add(infoPanel, BorderLayout.CENTER);
        
        if (control != null) {
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
            controlPanel.setBackground(cardColor);
            controlPanel.add(control);
            row.add(controlPanel, BorderLayout.EAST);
        }
        
        return row;
    }

    private JPanel createAccountSection() {
        JPanel card = createSettingsCard("Account");
        User user = AuthService.getCurrentUser();
        
        String email = user != null && user.getEmail() != null ? user.getEmail() : "Not Logged In";
        card.add(createRow("Email", email, createSecondaryButton("Change Email")));
        card.add(createDivider());
        
        card.add(createRow("Password", "********", createSecondaryButton("Change Password")));
        card.add(createDivider());
        
        card.add(createRow("Current Role", "Designer (or User depending on logic)", null));
        return card;
    }

    private JPanel createEditorPreferencesSection() {
        JPanel card = createSettingsCard("Editor Preferences");
        
        JComboBox<String> unitCombo = createComboBox(new String[]{"Metric (m)", "Imperial (ft)"});
        card.add(createRow("Measurement Units", "Choose between metric (meters) or imperial (feet)", unitCombo));
        card.add(createDivider());
        
        JComboBox<String> gridCombo = createComboBox(new String[]{"0.5m", "1.0m", "2.0m"});
        card.add(createRow("Grid Size", "Default grid size for 2D view", gridCombo));
        card.add(createDivider());
        
        // A simple toggle switch using a JCheckBox styled
        JCheckBox autoSaveCheck = new JCheckBox();
        autoSaveCheck.setSelected(true);
        autoSaveCheck.setBackground(cardColor);
        autoSaveCheck.setOpaque(true);
        card.add(createRow("Auto-save", "Automatically save changes while editing", autoSaveCheck));
        card.add(createDivider());

        JComboBox<String> viewModeCombo = createComboBox(new String[]{"2D View", "3D View"});
        card.add(createRow("Default View Mode", "Choose default view when opening designs", viewModeCombo));

        return card;
    }

    private JPanel createDataManagementSection() {
        JPanel card = createSettingsCard("Data Management");
        
        card.add(createRow("Storage Used", "0 designs saved locally", new JLabel("~0.0 KB", SwingConstants.RIGHT)));
        card.add(createDivider());
        
        JButton exportBtn = createSecondaryButton("Download Backup");
        card.add(createRow("Export Data", "Download all your designs as a backup", exportBtn));
        card.add(createDivider());
        
        JButton deleteBtn = new JButton("Delete All Designs");
        styleDeleteButton(deleteBtn);
        card.add(createRow("Clear All Data", "Permanently delete all designs from local storage", deleteBtn));

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new GridLayout(2, 1));
        footer.setBackground(cardColor);
        footer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(15, 0, 15, 0)
        ));
        footer.setMaximumSize(new Dimension(800, 80));
        
        JLabel brand = new JLabel("Furniture Room Visualizer", SwingConstants.CENTER);
        brand.setFont(new Font("SansSerif", Font.BOLD, 14));
        brand.setForeground(Color.WHITE);
        
        JLabel version = new JLabel("Version 1.0.0 | © 2026 Room Visualizer", SwingConstants.CENTER);
        version.setFont(new Font("SansSerif", Font.PLAIN, 12));
        version.setForeground(new Color(150, 160, 180));
        
        footer.add(brand);
        footer.add(version);
        return footer;
    }

    private JComponent createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        sep.setBackground(borderColor);
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(cardColor);
        p.add(Box.createVerticalStrut(15), BorderLayout.NORTH);
        p.add(sep, BorderLayout.CENTER);
        p.add(Box.createVerticalStrut(15), BorderLayout.SOUTH);
        p.setMaximumSize(new Dimension(800, 31));
        return p;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(35, 45, 75));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void styleDeleteButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(new Color(255, 80, 80));
        btn.setBackground(cardColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 80), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(120, 35));
        combo.setBackground(new Color(35, 45, 75));
        combo.setForeground(Color.WHITE);
        return combo;
    }
}
