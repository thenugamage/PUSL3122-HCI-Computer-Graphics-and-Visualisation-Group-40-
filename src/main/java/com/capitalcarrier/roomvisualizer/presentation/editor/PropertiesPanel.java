package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.application.design.DesignService;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PropertiesPanel extends VBox {
    private Room room;
    private Runnable onRefresh;
    private Stage stage;

    public PropertiesPanel(Room room, Runnable onRefresh, Stage stage) {
        this.room = room;
        this.onRefresh = onRefresh;
        this.stage = stage;

        setSpacing(20);
        setPadding(new Insets(20));
        setPrefWidth(280);
        setStyle("-fx-background-color: #0F1437; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 1 0 0;");

        buildUI();
    }

    public void setSelectedItem(com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem item) {
        // No longer handled here, moved to CatalogPanel's Properties tab
    }

    private void buildUI() {
        getChildren().clear();
        TextField nameField = new TextField(room.getName() != null ? room.getName() : "New Design");
        nameField.setStyle(
            "-fx-background-color: #1d2440; -fx-text-fill: white; -fx-background-radius: 8; " +
            "-fx-border-radius: 8; -fx-border-color: rgba(255,255,255,0.05); -fx-padding: 8 12;"
        );
        nameField.textProperty().addListener((obs, oldVal, newVal) -> room.setName(newVal));
        getChildren().add(nameField);

        getChildren().add(buildDimensionsSection());
        getChildren().add(buildColorsSection());
        getChildren().add(buildLightingSection());

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);

        getChildren().add(buildActionButtons());
    }

    private Node buildSectionHeader(String titleText, String svgIcon) {
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        if (svgIcon != null) {
            SVGPath icon = new SVGPath();
            icon.setContent(svgIcon);
            icon.setFill(Color.WHITE);
            icon.setScaleX(0.8);
            icon.setScaleY(0.8);
            header.getChildren().add(icon);
        }

        Text text = new Text(titleText);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        header.getChildren().add(text);
        return header;
    }

    private VBox buildDimensionsSection() {
        VBox section = new VBox(10);
        section.getChildren().add(buildSectionHeader("Dimensions",
            "M19,13H5V11H19V13Z M3,19V5H21V19H3Z"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(createDimensionField("Width (m)",  room.getWidth(),  v -> room.setWidth(v)),  0, 0);
        grid.add(createDimensionField("Length (m)", room.getLength(), v -> room.setLength(v)), 1, 0);
        grid.add(createDimensionField("Height (m)", room.getHeight(), v -> room.setHeight(v)), 0, 1);

        section.getChildren().add(grid);
        return section;
    }

    private VBox createDimensionField(String labelText, double value,
                                      java.util.function.Consumer<Double> onAction) {
        VBox box = new VBox(5);
        Label label = new Label(labelText);
        label.setTextFill(Color.web("#8C94AF"));
        label.setFont(Font.font("Inter", 11));

        TextField field = new TextField(String.valueOf((int) value));
        field.setPrefWidth(110);
        field.setStyle(
            "-fx-background-color: #1d2440; -fx-text-fill: white; -fx-background-radius: 8; " +
            "-fx-border-radius: 8; -fx-border-color: rgba(255,255,255,0.05); -fx-padding: 8 12;"
        );
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double parsed = Double.parseDouble(newVal);
                if (parsed > 0) {
                    onAction.accept(parsed);
                    onRefresh.run();
                }
            } catch (NumberFormatException ignored) {}
        });

        box.getChildren().addAll(label, field);
        return box;
    }

    private VBox buildColorsSection() {
        VBox section = new VBox(14);
        section.getChildren().add(buildSectionHeader("Colors",
            "M12,22C6.48,22,2,17.52,2,12S6.48,2,12,2s10,4.48,10,10S17.52,22,12,22z"));

        section.getChildren().add(createColorGroup(
            "Wall Color",
            new String[]{"#2E325A", "#4C51BF", "#A0AEC0", "#E2E8F0", "#F7FAFC", "#FFFFFF"},
            room.getWallColor(), c -> room.setWallColor(c)
        ));
        section.getChildren().add(createColorGroup(
            "Floor Color",
            new String[]{"#3D2B1F", "#735C44", "#524F4D", "#D9C9B4", "#BFAA8A", "#8C715D"},
            room.getFloorColor(), c -> room.setFloorColor(c)
        ));

        return section;
    }

    private VBox createColorGroup(String labelText, String[] colors,
                                   String current,
                                   java.util.function.Consumer<String> onSelect) {
        VBox box = new VBox(8);
        Label label = new Label(labelText);
        label.setTextFill(Color.web("#8C94AF"));
        label.setFont(Font.font("Inter", 11));

        HBox swatches = new HBox(8);
        for (String hex : colors) {
            Rectangle rect = new Rectangle(22, 22);
            try { rect.setFill(Color.web(hex)); } catch (Exception ignored) {}
            rect.setArcWidth(4);
            rect.setArcHeight(4);
            rect.getStyleClass().add("color-swatch");

            if (hex.equalsIgnoreCase(current)) {
                rect.setStroke(Color.web("#ca4bf6"));
                rect.setStrokeWidth(2);
            }

            rect.setOnMouseClicked(e -> {
                onSelect.accept(hex);
                onRefresh.run();
                swatches.getChildren().forEach(n -> {
                    if (n instanceof Rectangle) {
                        ((Rectangle) n).setStroke(Color.TRANSPARENT);
                        ((Rectangle) n).setStrokeWidth(0);
                    }
                });
                rect.setStroke(Color.web("#ca4bf6"));
                rect.setStrokeWidth(2);
            });
            swatches.getChildren().add(rect);
        }

        box.getChildren().addAll(label, swatches);
        return box;
    }

    private VBox buildLightingSection() {
        VBox section = new VBox(14);
        section.setPadding(new Insets(10, 0, 10, 0));
        section.getChildren().add(buildSectionHeader("Lighting & Shading",
            "M12,7c-2.76,0-5,2.24-5,5s2.24,5,5,5s5-2.24,5-5S14.76,7,12,7L12,7z M2,13L5,13L5,11L2,11L2,13z M19,13L22,13L22,11L19,11L19,13z M11,2L11,5L13,5L13,2L11,2z M11,19L11,22L13,22L13,19L11,19z M5.64,4.22L3.51,6.34L4.93,7.76L7.05,5.64L5.64,4.22z M16.24,14.83L14.83,16.24L16.95,18.36L18.36,16.24L16.24,14.83z M16.24,4.22L14.83,5.64L16.95,7.76L18.36,5.64L16.24,4.22z M5.64,14.83L3.51,16.95L4.93,18.36L7.05,16.24L5.64,14.83L5.64,14.83z"));

        section.getChildren().add(createLightingSlider("Brightness", 0, 100, (double)room.getBrightness(), v -> room.setBrightness(v.intValue())));
        section.getChildren().add(createLightingSlider("Light X", -500, 500, (double)room.getLightX(), v -> room.setLightX(v.intValue())));
        section.getChildren().add(createLightingSlider("Light Y (Height)", -1000, 200, (double)room.getLightY(), v -> room.setLightY(v.intValue())));
        section.getChildren().add(createLightingSlider("Light Z", -500, 500, (double)room.getLightZ(), v -> room.setLightZ(v.intValue())));

        return section;
    }

    private VBox createLightingSlider(String labelPrefix, double min, double max, double value,
                                        java.util.function.Consumer<Double> onAction) {
        VBox box = new VBox(5);
        Label label = new Label(labelPrefix + ": " + (int)value);
        label.setTextFill(Color.web("#8C94AF"));
        label.setFont(Font.font("Inter", 11));

        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(false);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(labelPrefix + ": " + newVal.intValue());
            onAction.accept(newVal.doubleValue());
            onRefresh.run();
        });

        box.getChildren().addAll(label, slider);
        return box;
    }

    private VBox buildActionButtons() {
        VBox box = new VBox(12);

        Button saveBtn = new Button("       Save Design");
        // Using a manual HBox or StackPane to match the mockup's icon positioning
        HBox saveContent = new HBox(8);
        saveContent.setAlignment(Pos.CENTER);
        SVGPath saveIcon = new SVGPath();
        saveIcon.setContent("M4,15V19H8L18.5,8.5L14.5,4.5L4,15M23,5.5L19.5,2L17.5,4L21,7.5L23,5.5Z"); // A pen/edit icon or similar
        saveIcon.setFill(Color.WHITE);
        saveIcon.setScaleX(0.8);
        saveIcon.setScaleY(0.8);
        
        Label saveLabel = new Label("Save Design");
        saveLabel.setTextFill(Color.WHITE);
        saveLabel.setStyle("-fx-font-weight: bold;");
        
        saveBtn.setGraphic(new HBox(10, saveIcon, saveLabel));
        saveBtn.setAlignment(Pos.CENTER);
        
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(48);
        saveBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #B24EF1, #8B5CF6); " +
            "-fx-text-fill: white; -fx-background-radius: 12; -fx-cursor: hand;"
        );
        saveBtn.setOnAction(e -> {
            try {
                DesignService.saveDesign(room.getName() != null ? room.getName() : "My Design", room);
                showInfo("Saved", "Design \"" + room.getName() + "\" saved successfully!");
            } catch (Exception ex) {
                showError("Save Error", ex.getMessage());
            }
        });

        Button backBtn = new Button("Back to Designs");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setPrefHeight(48);
        backBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.15); " +
            "-fx-border-radius: 12; -fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> new DashboardFX().start(stage));

        box.getChildren().addAll(saveBtn, backBtn);
        return box;
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
