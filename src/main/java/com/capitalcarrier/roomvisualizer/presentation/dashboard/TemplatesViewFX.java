package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import com.capitalcarrier.roomvisualizer.presentation.components.RoomIllustrationBuilder;

public class TemplatesViewFX extends VBox {

    private static final String CARD_BG = "#0E1437";
    private static final String CARD_BORD = "#232D5A";
    private static final String PURPLE = "#6C37DC";
    private static final String TEXT_MUTED = "#8C94AF";

    private static final Object[][] ROOM_TEMPLATES = {
        {"Living Room",  "Standard living room layout", 6.0, 8.0, 3.0,  "#E0D2C8", "#A08764"},
        {"Bedroom",      "Standard bedroom layout",     4.5, 5.0, 2.8,  "#D2CDD3", "#8C785F"},
        {"Home Office",  "Standard office layout",      3.5, 4.0, 2.8,  "#C8CDD7", "#827D73"},
        {"Dining Room",  "Standard dining room layout", 5.0, 6.0, 3.0,  "#E1D7C8", "#9B825F"},
        {"Kitchen",      "Standard kitchen layout",     4.0, 5.0, 2.8,  "#DCDCD7", "#AA9B82"},
        {"Bathroom",     "Standard bathroom layout",    3.0, 3.5, 2.6,  "#CDD2DC", "#8C919E"},
    };

    public TemplatesViewFX() {
        setSpacing(0);
        setStyle("-fx-background-color: transparent;");

        // Use ScrollPane for the entire content
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        
        VBox container = new VBox(40);
        container.setPadding(new Insets(40, 60, 40, 60));
        container.getChildren().addAll(buildHeader(), buildGrid());
        
        scroll.setContent(container);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        getChildren().add(scroll);
    }

    private Node buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titlePanel = new VBox(6);
        Text title = new Text("New Design");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);
        Text sub = new Text("Select a room template to get started");
        sub.setFont(Font.font("Inter", 14));
        sub.setFill(Color.web(TEXT_MUTED));
        titlePanel.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button customBtn = new Button("+ Create Custom Room");
        customBtn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 10 20; -fx-cursor: hand;");
        customBtn.setOnAction(e -> showCustomRoomDialog());
        
        header.getChildren().addAll(titlePanel, spacer, customBtn);
        return header;
    }

    private Node buildGrid() {
        FlowPane grid = new FlowPane(30, 30);
        grid.setAlignment(Pos.TOP_LEFT);
        
        for (Object[] data : ROOM_TEMPLATES) {
            grid.getChildren().add(buildTemplateCard(data));
        }
        
        return grid;
    }

    private Node buildTemplateCard(Object[] data) {
        VBox card = new VBox();
        card.setPrefWidth(350); // Set to 350 for 3 per row on 1280px window
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 20; -fx-border-color: " + CARD_BORD + "; -fx-border-radius: 20; -fx-border-width: 1;");
        card.setCursor(javafx.scene.Cursor.HAND);

        // Preview Area with 2.5D Illustration
        StackPane preview = new StackPane();
        preview.setPrefHeight(200);
        preview.setClip(new Rectangle(350, 200) {{ setArcWidth(40); setArcHeight(40); }}); // Rounded corners top
        preview.setStyle("-fx-background-color: #141A3D; -fx-background-radius: 20 20 0 0;");
        preview.getChildren().add(RoomIllustrationBuilder.buildIllustration((String) data[0], (String) data[5], (String) data[6]));

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Header with Title and "Preset" Badge
        HBox cardTop = new HBox();
        cardTop.setAlignment(Pos.CENTER_LEFT);
        
        Text name = new Text((String) data[0]);
        name.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        name.setFill(Color.WHITE);
        
        Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);
        
        Label badge = new Label("Preset");
        badge.setStyle("-fx-background-color: #311B92; -fx-text-fill: #B388FF; -fx-font-size: 10; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 6;");
        
        cardTop.getChildren().addAll(name, s1, badge);

        Text desc = new Text((String) data[1]);
        desc.setFont(Font.font("Inter", 13));
        desc.setFill(Color.web(TEXT_MUTED));

        // Dimensions Row
        HBox dims = new HBox(30);
        dims.getChildren().addAll(
            createDimItem("Width", data[2] + "m"),
            createDimItem("Length", data[3] + "m"),
            createDimItem("Height", data[4] + "m")
        );

        // Color Swatches
        HBox colors = new HBox(15);
        colors.setAlignment(Pos.CENTER_LEFT);
        colors.getChildren().addAll(
            createSwatch("Walls", (String) data[5]),
            createSwatch("Floor", (String) data[6])
        );

        // Buttons
        HBox actions = new HBox(10);
        Button useBtn = new Button("Use This Room \u2192");
        useBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(useBtn, Priority.ALWAYS);
        useBtn.setStyle("-fx-background-color: " + PURPLE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 12;");
        
        Button editBtn = new Button("Edit");
        editBtn.setPrefWidth(70);
        editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 12; -fx-padding: 12;");

        actions.getChildren().addAll(useBtn, editBtn);

        useBtn.setOnAction(e -> {
            Room room = new Room();
            room.setName((String) data[0]);
            room.setWidth((double) data[2]);
            room.setLength((double) data[3]);
            room.setHeight((double) data[4]);
            room.setWallColor((String) data[5]);
            room.setFloorColor((String) data[6]);
            DashboardFX.getInstance().showEditor(room, null);
        });

        content.getChildren().addAll(cardTop, desc, dims, colors, actions);
        card.getChildren().addAll(preview, content);
        
        return card;
    }

    private VBox createDimItem(String label, String val) {
        VBox box = new VBox(2);
        Text l = new Text(label); l.setFont(Font.font("Inter", 10)); l.setFill(Color.web("#5A6282"));
        Text v = new Text(val); v.setFont(Font.font("Inter", FontWeight.BOLD, 14)); v.setFill(Color.WHITE);
        box.getChildren().addAll(l, v);
        return box;
    }

    private HBox createSwatch(String labelText, String hex) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Rectangle r = new Rectangle(12, 12);
        r.setFill(Color.web(hex));
        r.setArcWidth(4); r.setArcHeight(4);
        
        Label l = new Label(labelText);
        l.setFont(Font.font("Inter", 11));
        l.setTextFill(Color.web(TEXT_MUTED));
        
        box.getChildren().addAll(r, l);
        return box;
    }

    private void showCustomRoomDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0E1437; -fx-background-radius: 20; -fx-border-color: #232D5A; -fx-border-radius: 20; -fx-border-width: 1;");
        root.setAlignment(Pos.CENTER);
        
        Text title = new Text("Custom Room Dimensions");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);
        
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        
        TextField wField = createStyledField("Width (m)", "6.0");
        TextField lField = createStyledField("Length (m)", "8.0");
        TextField hField = createStyledField("Height (m)", "3.0");
        
        grid.add(new Label("Width") {{ setTextFill(Color.web(TEXT_MUTED)); }}, 0, 0);
        grid.add(wField, 1, 0);
        grid.add(new Label("Length") {{ setTextFill(Color.web(TEXT_MUTED)); }}, 0, 1);
        grid.add(lField, 1, 1);
        grid.add(new Label("Height") {{ setTextFill(Color.web(TEXT_MUTED)); }}, 0, 2);
        grid.add(hField, 1, 2);
        
        HBox btns = new HBox(15);
        btns.setAlignment(Pos.CENTER);
        
        Button createBtn = new Button("Create Room");
        createBtn.setStyle("-fx-background-color: " + PURPLE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 10 25; -fx-cursor: hand;");
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 12; -fx-padding: 10 25; -fx-cursor: hand;");
        
        createBtn.setOnAction(e -> {
            try {
                Room room = new Room();
                room.setName("Custom Room");
                room.setWidth(Double.parseDouble(wField.getText()));
                room.setLength(Double.parseDouble(lField.getText()));
                room.setHeight(Double.parseDouble(hField.getText()));
                room.setWallColor("#E0D2C8");
                room.setFloorColor("#A08764");
                dialog.close();
                DashboardFX.getInstance().showEditor(room, null);
            } catch (Exception ex) {}
        });
        
        cancelBtn.setOnAction(e -> dialog.close());
        
        btns.getChildren().addAll(cancelBtn, createBtn);
        root.getChildren().addAll(title, grid, btns);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.show();
    }

    private TextField createStyledField(String prompt, String initial) {
        TextField f = new TextField(initial);
        f.setPromptText(prompt);
        f.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-border-color: #232D5A; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; -fx-font-family: 'Inter';");
        f.setPrefWidth(120);
        return f;
    }
}
