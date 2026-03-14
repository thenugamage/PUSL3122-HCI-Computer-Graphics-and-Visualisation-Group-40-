package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import javax.swing.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("Room Visualizer - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Use the SettingsPanel we implemented
        setContentPane(new SettingsPanel());
    }
}
