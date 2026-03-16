package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.capitalcarrier.roomvisualizer.presentation.editor.EditorFX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DashboardFX {

    private static DashboardFX instance;
    private String activeView = "My Designs";
    private BorderPane root;
    private StackPane contentArea;
    private Stage stage;

    public static DashboardFX getInstance() {
        return instance;
    }

    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0A0F2D;"); // Matches ThemeConfig.BACKGROUND

        root.setTop(buildHeader());

        contentArea = new StackPane();
        contentArea.getChildren().add(new MyDesignsViewFX());

        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1280, 900);
        stage.setScene(scene);
        stage.show();
    }

    public void showEditor(Room room, String designId) {
        activeView = "Editor";
        EditorFX editor = new EditorFX(room, stage);
        editor.setDesignId(designId);
        contentArea.getChildren().setAll(editor.getContent());
        root.setTop(buildHeader());
    }

    private Node buildHeader() {
        HBox header = new HBox();
        header.setPrefHeight(72);
        header.setPadding(new Insets(0, 48, 0, 48));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #0F1437; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 0 1 0;");

        // Logo
        HBox logoGroup = new HBox(12);
        logoGroup.setAlignment(Pos.CENTER_LEFT);
        
        StackPane logoIcon = createLogoIcon();
        Text logoText = new Text("Room Visualizer");
        logoText.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        logoText.setFill(Color.WHITE);
        
        logoGroup.getChildren().addAll(logoIcon, logoText);

        // Navigation
        HBox navMenu = new HBox(10);
        navMenu.setAlignment(Pos.CENTER);
        HBox.setHgrow(navMenu, Priority.ALWAYS);

        navMenu.getChildren().addAll(
            createNavItem("My Designs", activeView.equals("My Designs")),
            createSeparatorDot(),
            createNavItem("New Design", activeView.equals("New Design")),
            createSeparatorDot(),
            createNavItem("Settings", activeView.equals("Settings"))
        );

        // User Profile
        HBox profileGroup = new HBox(15);
        profileGroup.setAlignment(Pos.CENTER_RIGHT);

        User user = AuthService.getCurrentUser();
        String username = user != null ? user.getUsername() : "User";
        String initial = !username.isEmpty() ? username.substring(0, 1).toUpperCase() : "?";

        Button userBtn = new Button(username);
        userBtn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand; -fx-font-weight: bold;");
        
        StackPane avatar = new StackPane();
        avatar.setPrefSize(36, 36);
        Circle avatarCircle = new Circle(18);
        LinearGradient grad = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, 
            new Stop(0, Color.web("#9933FF")), new Stop(1, Color.web("#00FFFF")));
        avatarCircle.setFill(grad);
        Text avatarInitial = new Text(initial);
        avatarInitial.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        avatarInitial.setFill(Color.WHITE);
        avatar.getChildren().addAll(avatarCircle, avatarInitial);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 12;");
        logoutBtn.setOnAction(e -> {
            AuthService.logout();
            new com.capitalcarrier.roomvisualizer.presentation.auth.LoginFX().start(stage);
        });

        profileGroup.getChildren().addAll(userBtn, avatar, logoutBtn);

        header.getChildren().addAll(logoGroup, navMenu, profileGroup);
        return header;
    }

    private Node createSeparatorDot() {
        Circle dot = new Circle(2, Color.web("#3E4461"));
        return dot;
    }

    private Node createNavItem(String text, boolean active) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Inter", active ? FontWeight.BOLD : FontWeight.NORMAL, 14));
        btn.setTextFill(active ? Color.WHITE : Color.web("#8C94AF"));
        
        String baseStyle = active 
            ? "-fx-background-color: #1E2341; -fx-background-radius: 12; -fx-padding: 10 20; -fx-cursor: hand;" 
            : "-fx-background-color: transparent; -fx-padding: 10 20; -fx-cursor: hand;";
            
        btn.setStyle(baseStyle);
        
        btn.setOnAction(e -> {
            if (active) return;
            switch (text) {
                case "My Designs":
                    activeView = "My Designs";
                    contentArea.getChildren().setAll(new MyDesignsViewFX());
                    break;
                case "New Design":
                    activeView = "New Design";
                    contentArea.getChildren().setAll(new TemplatesViewFX());
                    break;
                case "Settings":
                    activeView = "Settings";
                    contentArea.getChildren().setAll(new SettingsViewFX());
                    break;
                default:
                    return;
            }
            root.setTop(buildHeader());
        });

        return btn;
    }

    private StackPane createLogoIcon() {
        StackPane pane = new StackPane();
        pane.setPrefSize(24, 24);
        Polygon poly = new Polygon(12,0, 24,6, 24,18, 12,24, 0,18, 0,6);
        poly.setFill(Color.web("#9933FF"));
        poly.setStroke(Color.WHITE);
        poly.setStrokeWidth(1.5);
        
        Line l1 = new Line(12, 0, 12, 12); l1.setStroke(Color.WHITE);
        Line l2 = new Line(12, 12, 24, 6); l2.setStroke(Color.WHITE);
        Line l3 = new Line(12, 12, 0, 6); l3.setStroke(Color.WHITE);
        
        pane.getChildren().addAll(poly, l1, l2, l3);
        return pane;
    }
}
