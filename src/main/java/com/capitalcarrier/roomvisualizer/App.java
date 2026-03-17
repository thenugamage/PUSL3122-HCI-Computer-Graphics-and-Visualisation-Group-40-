package com.capitalcarrier.roomvisualizer;

import com.capitalcarrier.roomvisualizer.config.DatabaseConfig;
import com.capitalcarrier.roomvisualizer.presentation.templates.TemplatesFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Initialize the embedded SQLite database before starting the UI
        DatabaseConfig.initializeDatabase();

        // Start the application GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            TemplatesFrame frame = new TemplatesFrame();
            frame.setVisible(true);
        });
    }
}
