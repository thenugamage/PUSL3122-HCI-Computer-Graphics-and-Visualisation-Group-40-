package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Group;
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

    private String activeView = "My Designs";
    private BorderPane root;
    private StackPane contentArea;

    public void start(Stage stage) {
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

    private Node buildHeader() {
        HBox header = new HBox();
        header.setPrefHeight(64);
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
        HBox navMenu = new HBox(5);
        navMenu.setAlignment(Pos.CENTER);
        HBox.setHgrow(navMenu, Priority.ALWAYS);

        navMenu.getChildren().addAll(
            createNavItem("My Designs", "GRID", activeView.equals("My Designs")),
            createNavItem("New Design", "BOX", activeView.equals("New Design")),
            createNavItem("My Rooms", "HOME", activeView.equals("My Rooms")),
            createNavItem("Settings", "GEAR", activeView.equals("Settings"))
        );

        // User Profile
        HBox profileGroup = new HBox(15);
        profileGroup.setAlignment(Pos.CENTER_RIGHT);

        Button userBtn = new Button("User");
        userBtn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: rgba(255,255,255,0.1); -fx-padding: 0 15 0 15; -fx-min-height: 32; -fx-cursor: hand;");
        
        StackPane avatar = new StackPane();
        avatar.setPrefSize(32, 32);
        Circle avatarCircle = new Circle(16);
        LinearGradient grad = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, 
            new Stop(0, Color.web("#9933FF")), new Stop(1, Color.web("#00FFFF")));
        avatarCircle.setFill(grad);
        Text avatarInitial = new Text("Y");
        avatarInitial.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        avatarInitial.setFill(Color.WHITE);
        avatar.getChildren().addAll(avatarCircle, avatarInitial);

        profileGroup.getChildren().addAll(userBtn, avatar);

        header.getChildren().addAll(logoGroup, navMenu, profileGroup);
        return header;
    }

    private StackPane createLogoIcon() {
        StackPane pane = new StackPane();
        pane.setPrefSize(20, 20);
        Polygon poly = new Polygon(10,0, 20,5, 20,15, 10,20, 0,15, 0,5);
        poly.setFill(Color.web("#9933FF"));
        poly.setStroke(Color.WHITE);
        poly.setStrokeWidth(1.2);
        
        Line l1 = new Line(10, 0, 10, 10); l1.setStroke(Color.WHITE);
        Line l2 = new Line(10, 10, 20, 5); l2.setStroke(Color.WHITE);
        Line l3 = new Line(10, 10, 0, 5); l3.setStroke(Color.WHITE);
        
        pane.getChildren().addAll(poly, l1, l2, l3);
        return pane;
    }

    private Node createNavItem(String text, String iconType, boolean active) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 15, 0, 15));
        item.setPrefHeight(40);
        item.setPrefWidth(135);
        item.setStyle(active ? "-fx-background-color: #1E2341; -fx-background-radius: 12;" : "-fx-background-color: transparent;");
        item.setCursor(javafx.scene.Cursor.HAND);

        // Icon (Custom Drawing)
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(20, 20);
        Shape iconShape = getIcon(iconType, active);
        iconPane.getChildren().add(iconShape);

        Label label = new Label(text);
        label.setFont(Font.font("Inter", active ? FontWeight.BOLD : FontWeight.NORMAL, 13));
        label.setTextFill(active ? Color.WHITE : Color.web("#8C94AF"));

        item.getChildren().addAll(iconPane, label);
        
        item.setOnMouseClicked(e -> {
            if (active) return;
            if (text.equals("My Designs")) {
                activeView = "My Designs";
                contentArea.getChildren().setAll(new MyDesignsViewFX());
                root.setTop(buildHeader());
            } else if (text.equals("Settings")) {
                activeView = "Settings";
                contentArea.getChildren().setAll(new Label("Settings (Coming Soon)"));
                root.setTop(buildHeader());
            } else if (text.equals("New Design")) {
                new com.capitalcarrier.roomvisualizer.presentation.templates.TemplatesFX().start((Stage)root.getScene().getWindow());
            }
        });

        return item;
    }

    private Shape getIcon(String type, boolean active) {
        Color color = active ? Color.web("#9933FF") : Color.web("#8C94AF");
        switch (type) {
            case "GRID":
                Group grid = new Group();
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        Rectangle r = new Rectangle(i * 8, j * 8, 5, 5);
                        r.setFill(Color.TRANSPARENT);
                        r.setStroke(color);
                        r.setStrokeWidth(1.2);
                        grid.getChildren().add(r);
                    }
                }
                // Convert Group to Shape if possible or just return a region
                // For simplicity, let's use a composite shape for now
                return new Rectangle(0,0,0,0); // Placeholder
            default:
                return new Circle(2, color);
        }
    }
}
