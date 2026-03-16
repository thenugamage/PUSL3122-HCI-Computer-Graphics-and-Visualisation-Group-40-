package com.capitalcarrier.roomvisualizer;

import javafx.application.Application;
import javafx.stage.Stage;
import com.capitalcarrier.roomvisualizer.config.DatabaseConfig;
import com.capitalcarrier.roomvisualizer.presentation.auth.LoginFX;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize database if not already done (though App.java handles it)
        DatabaseConfig.initializeDatabase();
        
        // Load the initial Login View
        LoginFX loginView = new LoginFX();
        loginView.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
