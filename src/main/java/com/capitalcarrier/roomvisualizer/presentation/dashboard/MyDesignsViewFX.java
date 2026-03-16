package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.application.design.DesignService;
import com.capitalcarrier.roomvisualizer.domain.model.DesignProject;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import com.capitalcarrier.roomvisualizer.presentation.components.RoomIllustrationBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyDesignsViewFX extends VBox {

    private static final String CARD_BG = "#0E1437";
    private static final String CARD_BORD = "#232D5A";

    private List<DesignProject> allProjects = new ArrayList<>();
    private FlowPane gridPane;
    private TextField searchField;
    private ComboBox<String> filterCombo;

    public MyDesignsViewFX() {
        setSpacing(30);
        setPadding(new Insets(40, 60, 40, 60));
        setStyle("-fx-background-color: transparent;");

        getChildren().addAll(buildHeader(), buildScrollArea());
        loadDesigns();
    }

    private Node buildHeader() {
        VBox header = new VBox(30);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titlePanel = new VBox(4);
        Text title = new Text("My Designs");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);
        Text sub = new Text("Manage and view all your room designs");
        sub.setFont(Font.font("Inter", 14));
        sub.setFill(Color.web("#8C94AF"));
        titlePanel.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createBtn = new Button("+ Create New Design");
        createBtn.setStyle("-fx-background-color: linear-gradient(to right, #6C37DC, #06B6D4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 12; -fx-padding: 10 20; -fx-cursor: hand;");
        createBtn.setOnAction(e -> DashboardFX.getInstance().showEditor(new Room(), null));
        
        topRow.getChildren().addAll(titlePanel, spacer, createBtn);

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Search designs...");
        searchField.setPrefWidth(280);
        searchField.setPrefHeight(38);
        searchField.setStyle("-fx-background-color: #191E37; -fx-text-fill: white; -fx-prompt-text-fill: #5A6282; -fx-background-radius: 12; -fx-border-color: #232D5A; -fx-border-radius: 12; -fx-padding: 0 15 0 35;");
        searchField.setOnKeyReleased(e -> applyFilter());

        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All Rooms", "Living Room", "Bedroom", "Office", "Dining Room", "Kitchen", "Bathroom");
        filterCombo.setValue("All Rooms");
        filterCombo.setPrefWidth(140);
        filterCombo.setPrefHeight(38);
        filterCombo.setStyle("-fx-background-color: #191E37; -fx-text-fill: white; -fx-background-radius: 12; -fx-border-color: #232D5A; -fx-border-radius: 12;");
        filterCombo.setOnAction(e -> applyFilter());

        controls.getChildren().addAll(searchField, filterCombo);

        header.getChildren().addAll(topRow, controls);
        return header;
    }

    private ScrollPane buildScrollArea() {
        gridPane = new FlowPane(22, 22);
        gridPane.setPadding(new Insets(20, 0, 20, 0));
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.setStyle("-fx-background-color: transparent;");

        ScrollPane scroll = new ScrollPane(gridPane);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private void loadDesigns() {
        try {
            allProjects = DesignService.getUserDesigns();
        } catch (SQLException ex) {
            ex.printStackTrace();
            allProjects = new ArrayList<>();
        }
        applyFilter();
    }

    private void applyFilter() {
        String query = searchField.getText().toLowerCase().trim();
        String filter = filterCombo.getValue();

        List<DesignProject> filtered = allProjects.stream()
            .filter(p -> p.getName().toLowerCase().contains(query))
            .filter(p -> {
                if ("All Rooms".equals(filter)) return true;
                return p.getName().toLowerCase().contains(filter.toLowerCase().split(" ")[0]);
            })
            .collect(Collectors.toList());

        renderGrid(filtered);
    }

    private void renderGrid(List<DesignProject> projects) {
        gridPane.getChildren().clear();
        if (projects.isEmpty()) {
            gridPane.getChildren().add(buildEmptyState());
        } else {
            for (DesignProject p : projects) {
                gridPane.getChildren().add(buildDesignCard(p));
            }
        }
    }

    private Node buildDesignCard(DesignProject project) {
        VBox card = new VBox();
        card.setPrefSize(350, 300);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 16; -fx-border-color: " + CARD_BORD + "; -fx-border-radius: 16;");
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> DashboardFX.getInstance().showEditor(project.getRoom(), project.getId()));

        // Preview Area with 2.5D Illustration
        StackPane preview = new StackPane();
        preview.setPrefHeight(160);
        preview.setStyle("-fx-background-color: #140F2D; -fx-background-radius: 16 16 0 0;");
        preview.setClip(new Rectangle(350, 160) {{ setArcWidth(32); setArcHeight(32); }});
        
        Room r = project.getRoom();
        String wallColor = r != null ? r.getWallColor() : "#E0D2C8";
        String floorColor = r != null ? r.getFloorColor() : "#A08764";
        
        Node illustration = RoomIllustrationBuilder.buildIllustration(project.getName(), wallColor, floorColor);
        illustration.setScaleX(0.8);
        illustration.setScaleY(0.8);
        preview.getChildren().add(illustration);
        
        VBox info = new VBox(8);
        info.setPadding(new Insets(14, 16, 16, 16));

        Text name = new Text(project.getName());
        name.setFont(Font.font("Inter", FontWeight.BOLD, 15));
        name.setFill(Color.WHITE);

        String dimText = r != null ? String.format("%.1fm \u00d7 %.1fm \u00d7 %.1fm", r.getWidth(), r.getLength(), r.getHeight()) : "0m \u00d7 0m \u00d7 0m";
        Text dims = new Text(dimText);
        dims.setFont(Font.font("Inter", 12));
        dims.setFill(Color.web("#8C94AF"));

        Text meta = new Text("Items: " + (r != null && r.getFurnitureItems() != null ? r.getFurnitureItems().size() : 0));
        meta.setFont(Font.font("Inter", 11));
        meta.setFill(Color.web("#5A6282"));

        HBox actions = new HBox(8);
        Button openBtn = createSmallButton("Open", "#6C37DC");
        openBtn.setOnAction(e -> DashboardFX.getInstance().showEditor(project.getRoom(), project.getId()));
        Button deleteBtn = createSmallButton("Delete", "#50141E");
        actions.getChildren().addAll(openBtn, deleteBtn);

        info.getChildren().addAll(name, dims, meta, actions);
        card.getChildren().addAll(preview, info);

        return card;
    }

    private Button createSmallButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11; -fx-background-radius: 8; -fx-padding: 5 15; -fx-cursor: hand;");
        return btn;
    }

    private Node buildEmptyState() {
        VBox empty = new VBox(20);
        empty.setAlignment(Pos.CENTER);
        empty.setMinWidth(800);
        Text t = new Text("No designs yet");
        t.setFont(Font.font("Inter", FontWeight.BOLD, 22));
        t.setFill(Color.WHITE);
        empty.getChildren().add(t);
        return empty;
    }
}
