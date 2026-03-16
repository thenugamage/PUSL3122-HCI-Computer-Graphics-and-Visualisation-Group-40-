package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFX;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EditorFX {
    private Room room;
    private BorderPane root;
    private StackPane centerStack;
    private RoomViewport3DFX view3D;
    private RoomCanvas2DFX view2D;

    public EditorFX(Room room) {
        this.room = room != null ? room : new Room();
    }

    public void start(Stage stage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0A0F2D;");

        // Top Navbar
        root.setTop(buildNavbar(stage));

        // Left Sidebar
        root.setLeft(buildLeftSidebar());

        // Right Sidebar
        root.setRight(buildRightSidebar());

        // Center Area
        centerStack = new StackPane();
        view3D = new RoomViewport3DFX(room);
        view2D = new RoomCanvas2DFX(room);
        
        // Use a SubScene for 3D content
        SubScene subScene = new SubScene(view3D, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#050A1E"));
        subScene.widthProperty().bind(centerStack.widthProperty());
        subScene.heightProperty().bind(centerStack.heightProperty());
        
        centerStack.getChildren().addAll(view2D, subScene);
        subScene.setVisible(false); // Default to 2D
        root.setCenter(centerStack);

        // Add Toggle Button to Navbar
        Button toggleBtn = new Button("Switch to 3D View");
        toggleBtn.setStyle("-fx-background-color: #6C37DC; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        toggleBtn.setOnAction(e -> {
            boolean is3D = subScene.isVisible();
            subScene.setVisible(!is3D);
            view2D.setVisible(is3D);
            toggleBtn.setText(is3D ? "Switch to 3D View" : "Switch to 2D View");
        });
        ((HBox)root.getTop()).getChildren().add(1, toggleBtn);

        Scene scene = new Scene(root, 1440, 900);
        stage.setScene(scene);
        stage.show();
    }

    private Node buildNavbar(Stage stage) {
        HBox nav = new HBox(48);
        nav.setPrefHeight(64);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(0, 48, 0, 48));
        nav.setStyle("-fx-background-color: #0F1437; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 0 1 0;");

        Text logo = new Text("Room Visualizer");
        logo.setFill(Color.WHITE);
        logo.setFont(Font.font("Inter", FontWeight.BOLD, 16));

        HBox menu = new HBox(20);
        menu.setAlignment(Pos.CENTER);
        HBox.setHgrow(menu, Priority.ALWAYS);
        // Add nav items...

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            new DashboardFX().start(stage);
        });

        nav.getChildren().addAll(logo, menu, backBtn);
        return nav;
    }

    private Node buildLeftSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(270);
        sidebar.setPadding(new Insets(28, 20, 28, 20));
        sidebar.setStyle("-fx-background-color: #141932;");

        Text title = new Text("Room Properties");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        VBox wBox = createLabelledSlider("Width (m)", room.getWidth(), 2, 20, v -> { room.setWidth(v); refreshViews(); });
        VBox lBox = createLabelledSlider("Length (m)", room.getLength(), 2, 20, v -> { room.setLength(v); refreshViews(); });
        VBox hBox = createLabelledSlider("Height (m)", room.getHeight(), 2, 5, v -> { room.setHeight(v); refreshViews(); });

        sidebar.getChildren().addAll(title, wBox, lBox, hBox);
        return sidebar;
    }

    private void refreshViews() {
        // Redraw logic
        if (view3D != null) {
            // Re-setup 3D or update properties
        }
    }

    private VBox createLabelledSlider(String labelText, double value, double min, double max, java.util.function.Consumer<Double> onAction) {
        VBox box = new VBox(8);
        Label label = new Label(labelText + ": " + String.format("%.1f", value));
        label.setTextFill(Color.web("#8C94AF"));
        label.setFont(Font.font("Inter", 12));
        
        Slider slider = new Slider(min, max, value);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(labelText + ": " + String.format("%.1f", newVal.doubleValue()));
            onAction.accept(newVal.doubleValue());
        });
        
        box.getChildren().addAll(label, slider);
        return box;
    }

    private Node buildRightSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(300);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #141932;");

        Text title = new Text("Furniture Catalog");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        ListView<String> catalog = new ListView<>();
        catalog.getItems().addAll("Modern Sofa", "Armchair", "Dining Table", "Office Desk");
        catalog.setStyle("-fx-background-color: #0F1437; -fx-control-inner-background: #0F1437; -fx-text-fill: white;");

        sidebar.getChildren().addAll(title, catalog);
        return sidebar;
    }
}
