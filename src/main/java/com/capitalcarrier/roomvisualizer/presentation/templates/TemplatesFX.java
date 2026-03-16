package com.capitalcarrier.roomvisualizer.presentation.templates;

import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFX;
import com.capitalcarrier.roomvisualizer.presentation.editor.EditorFX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TemplatesFX {

    private static final String BG = "#0A0F2D";
    private static final String CARD_BG = "#0E1437";
    private static final String CARD_BORD = "#232D5A";
    private static final String PURPLE = "#6C37DC";
    
    private static final Object[][] ROOMS = {
        {"Living Room",  "Standard living room layout", 6.0, 8.0, 3.0,  "#E0D2C8", "#A08764"},
        {"Bedroom",      "Standard bedroom layout",     4.5, 5.0, 2.8,  "#D2CDD3", "#8C785F"},
        {"Home Office",  "Standard office layout",      3.5, 4.0, 2.8,  "#C8CDD7", "#827D73"},
        {"Dining Room",  "Standard dining room layout", 5.0, 6.0, 3.0,  "#E1D7C8", "#9B825F"},
        {"Kitchen",      "Standard kitchen layout",     4.0, 5.0, 2.8,  "#DCDCD7", "#AA9B82"},
        {"Bathroom",     "Standard bathroom layout",    3.0, 3.5, 2.6,  "#CDD2DC", "#8C919E"},
    };

    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        root.setTop(buildNavbar(stage));

        ScrollPane scroll = new ScrollPane(buildContent(stage));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        root.setCenter(scroll);

        Scene scene = new Scene(root, 1280, 860);
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new DashboardFX().start(stage));

        nav.getChildren().addAll(logo, spacer, backBtn);
        return nav;
    }

    private Node buildContent(Stage stage) {
        VBox content = new VBox(32);
        content.setPadding(new Insets(32, 36, 36, 36));

        HBox header = new HBox();
        VBox titles = new VBox(5);
        Text title = new Text("Templates");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);
        Text sub = new Text("Quick-start templates with preset dimensions");
        sub.setFill(Color.web("#8C94AF"));
        titles.getChildren().addAll(title, sub);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button createBtn = new Button("+ Create Custom Room");
        createBtn.setStyle("-fx-background-color: " + PURPLE + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 20; -fx-font-weight: bold;");

        header.getChildren().addAll(titles, spacer, createBtn);

        FlowPane grid = new FlowPane(22, 22);
        for (Object[] roomData : ROOMS) {
            grid.getChildren().add(buildCard(stage, roomData));
        }

        content.getChildren().addAll(header, grid);
        return content;
    }

    private Node buildCard(Stage stage, Object[] data) {
        VBox card = new VBox();
        card.setPrefWidth(380);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 16; -fx-border-color: " + CARD_BORD + "; -fx-border-radius: 16;");
        card.setCursor(javafx.scene.Cursor.HAND);

        StackPane preview = new StackPane();
        preview.setPrefHeight(185);
        preview.setStyle("-fx-background-color: #140F2D; -fx-background-radius: 16 16 0 0;");

        VBox info = new VBox(12);
        info.setPadding(new Insets(14, 16, 16, 16));

        Text name = new Text((String)data[0]);
        name.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        name.setFill(Color.WHITE);

        Text desc = new Text((String)data[1]);
        desc.setFont(Font.font("Inter", 12));
        desc.setFill(Color.web("#8C94AF"));

        HBox dimsRow = new HBox(20);
        dimsRow.getChildren().addAll(
            createDimInfo("Width", data[2] + "m"),
            createDimInfo("Length", data[3] + "m"),
            createDimInfo("Height", data[4] + "m")
        );

        Button useBtn = new Button("Use This Room \u2192");
        useBtn.setMaxWidth(Double.MAX_VALUE);
        useBtn.setStyle("-fx-background-color: " + PURPLE + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 10; -fx-font-weight: bold;");
        useBtn.setOnAction(e -> {
            Room room = new Room();
            room.setWidth((double)data[2]);
            room.setLength((double)data[3]);
            room.setHeight((double)data[4]);
            room.setWallColor((String)data[5]);
            room.setFloorColor((String)data[6]);
            new EditorFX(room).start(stage);
        });

        info.getChildren().addAll(name, desc, dimsRow, useBtn);
        card.getChildren().addAll(preview, info);
        return card;
    }

    private VBox createDimInfo(String label, String value) {
        VBox box = new VBox(2);
        Text l = new Text(label); l.setFont(Font.font("Inter", 10)); l.setFill(Color.web("#5A6282"));
        Text v = new Text(value); v.setFont(Font.font("Inter", FontWeight.BOLD, 13)); v.setFill(Color.WHITE);
        box.getChildren().addAll(l, v);
        return box;
    }
}
