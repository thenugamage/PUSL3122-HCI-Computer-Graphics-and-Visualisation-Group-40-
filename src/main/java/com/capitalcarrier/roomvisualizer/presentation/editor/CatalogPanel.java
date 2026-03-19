package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.UUID;

public class CatalogPanel extends VBox {

    private Room room;
    private Runnable onRefresh;
    private StackPane contentArea;
    private VBox catalogView;
    private VBox propertiesView;
    private VBox itemsList;
    private String activeCategory = "Seating";
    private String activeTab = "Catalog";
    private FurnitureItem selectedItem;

    private Button catalogTabBtn;
    private Button propertiesTabBtn;

    // name, category, width(x), height(y), depth(z), color
    private static final String[][] CATALOG = {
        {"Modern Sofa",   "Seating", "2.2", "0.8", "0.9", "#6B48C8"},
        {"Armchair",      "Seating", "0.9", "0.9", "0.9", "#3B82F6"},
        {"Dining Chair",  "Seating", "0.5", "0.9", "0.5", "#10B981"},
        {"Ottoman",       "Seating", "0.8", "0.4", "0.8", "#F59E0B"},
        {"Bench",         "Seating", "1.5", "0.5", "0.4", "#8B5CF6"},
        {"Dining Table",  "Tables",  "1.8", "0.8", "0.9", "#92400E"},
        {"Coffee Table",  "Tables",  "1.2", "0.5", "0.6", "#6B7280"},
        {"Side Table",    "Tables",  "0.5", "0.6", "0.5", "#4B5563"},
        {"Study Desk",    "Tables",  "1.4", "0.8", "0.7", "#374151"},
        {"Bookshelf",     "Storage", "1.0", "1.8", "0.3", "#78350F"},
        {"Wardrobe",      "Storage", "1.8", "2.0", "0.6", "#374151"},
        {"Cabinet",       "Storage", "0.8", "1.0", "0.4", "#4B5563"},
        {"TV Stand",      "Storage", "1.5", "0.6", "0.4", "#1F2937"},
        {"Single Bed",    "Beds",    "1.2", "0.6", "2.0", "#93C5FD"},
        {"Double Bed",    "Beds",    "1.8", "0.6", "2.0", "#C4B5FD"},
        {"Bunk Bed",      "Beds",    "1.0", "1.6", "2.0", "#6EE7B7"},
        {"Vase",          "Decor",   "0.3", "0.5", "0.3", "#FCA5A5"},
        {"Plant",         "Decor",   "0.4", "1.0", "0.4", "#34D399"},
        {"Mirror",        "Decor",   "0.6", "1.5", "0.1", "#E2E8F0"},
        {"Painting",      "Decor",   "0.8", "0.8", "0.05","#FDE68A"},
        {"Floor Lamp",    "Lighting","0.3", "1.8", "0.3", "#FEF3C7"},
        {"Table Lamp",    "Lighting","0.3", "0.6", "0.3", "#FDE68A"},
        {"Ceiling Light", "Lighting","0.4", "0.1", "0.4", "#FFFBEB"},
    };

    public CatalogPanel(Room room, Runnable onRefresh) {
        this.room = room;
        this.onRefresh = onRefresh;

        setSpacing(0);
        setPrefWidth(320);
        setStyle("-fx-background-color: #0F1437; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 0 0 1;");

        getChildren().add(buildTabSwitcher());

        contentArea = new StackPane();
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        
        catalogView = buildCatalogView();
        propertiesView = new VBox(); // Initially empty

        contentArea.getChildren().addAll(catalogView, propertiesView);
        getChildren().add(contentArea);

        switchTab("Catalog");
    }

    public void setSelectedItem(FurnitureItem item) {
        this.selectedItem = item;
        if (item != null) {
            switchTab("Properties");
        } else {
            switchTab("Catalog");
        }
    }

    private void switchTab(String tab) {
        this.activeTab = tab;
        if ("Catalog".equals(tab)) {
            catalogView.setVisible(true);
            propertiesView.setVisible(false);
            updateTabStyles(catalogTabBtn, true);
            updateTabStyles(propertiesTabBtn, false);
        } else {
            catalogView.setVisible(false);
            propertiesView.setVisible(true);
            updateTabStyles(catalogTabBtn, false);
            updateTabStyles(propertiesTabBtn, true);
            buildPropertiesView();
        }
    }

    private void updateTabStyles(Button btn, boolean active) {
        if (btn == null) return;
        if (active) {
            btn.setStyle("-fx-background-color: rgba(132,76,245,0.1); -fx-text-fill: #ca4bf6; -fx-border-color: #ca4bf6; -fx-border-width: 0 0 2 0; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 0;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8C94AF; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 0;");
        }
    }

    private HBox buildTabSwitcher() {
        HBox tabs = new HBox(0);
        tabs.setPrefHeight(48);
        tabs.setAlignment(Pos.CENTER);
        tabs.setStyle("-fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 0 1 0;");

        catalogTabBtn = new Button("Catalog");
        catalogTabBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(catalogTabBtn, Priority.ALWAYS);
        catalogTabBtn.setPrefHeight(48);
        catalogTabBtn.setOnAction(e -> switchTab("Catalog"));

        propertiesTabBtn = new Button("Properties");
        propertiesTabBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(propertiesTabBtn, Priority.ALWAYS);
        propertiesTabBtn.setPrefHeight(48);
        propertiesTabBtn.setOnAction(e -> switchTab("Properties"));

        tabs.getChildren().addAll(catalogTabBtn, propertiesTabBtn);
        return tabs;
    }

    private VBox buildCatalogView() {
        VBox view = new VBox(0);
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: transparent;");

        content.getChildren().add(buildCategoriesGrid());

        Label resultsLabel = new Label("Results");
        resultsLabel.setTextFill(Color.web("#8C94AF"));
        resultsLabel.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        content.getChildren().add(resultsLabel);

        itemsList = new VBox(10);
        content.getChildren().add(itemsList);

        scroll.setContent(content);
        view.getChildren().add(scroll);
        refreshItems();
        return view;
    }

    private void buildPropertiesView() {
        propertiesView.getChildren().clear();
        propertiesView.setPadding(new Insets(20));
        propertiesView.setSpacing(20);

        if (selectedItem == null) {
            VBox placeholder = new VBox(15);
            placeholder.setAlignment(Pos.CENTER);
            placeholder.setPadding(new Insets(40, 20, 0, 20));
            SVGPath icon = new SVGPath();
            icon.setContent("M12,2C6.48,2,2,6.48,2,12s4.48,10,10,10s10-4.48,10-10S17.52,2,12,2z M13,17h-2v-2h2V17z M13,13h-2V7h2V13z");
            icon.setFill(Color.web("#2D3450")); icon.setScaleX(1.5); icon.setScaleY(1.5);
            Label msg = new Label("Select an item in 3D\nto edit properties");
            msg.setTextFill(Color.web("#8C94AF")); msg.setFont(Font.font("Inter", 13));
            msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            placeholder.getChildren().addAll(icon, msg);
            propertiesView.getChildren().add(placeholder);
            return;
        }

        // Header
        VBox header = new VBox(8);
        Label title = new Label(selectedItem.getName());
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        Label type = new Label(selectedItem.getType());
        type.setTextFill(Color.web("#8B5CF6"));
        type.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        header.getChildren().addAll(type, title);
        propertiesView.getChildren().add(header);

        // Rotation
        VBox rotBox = new VBox(8);
        Label rotLabel = new Label("Rotation");
        rotLabel.setTextFill(Color.web("#8C94AF"));
        rotLabel.setFont(Font.font("Inter", 11));
        Slider rotSlider = new Slider(0, 360, selectedItem.getRotation());
        rotSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedItem.setRotation(newVal.doubleValue());
            onRefresh.run();
        });
        rotBox.getChildren().addAll(rotLabel, rotSlider);
        propertiesView.getChildren().add(rotBox);

        // Scale
        VBox scaleBox = new VBox(8);
        Label scaleLabel = new Label("Scale: " + String.format("%.2fx", selectedItem.getScale()));
        scaleLabel.setTextFill(Color.web("#8C94AF"));
        scaleLabel.setFont(Font.font("Inter", 11));
        Slider scaleSlider = new Slider(0.5, 3.0, selectedItem.getScale());
        scaleSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedItem.setScale(newVal.doubleValue());
            scaleLabel.setText("Scale: " + String.format("%.2fx", newVal.doubleValue()));
            onRefresh.run();
        });
        scaleBox.getChildren().addAll(scaleLabel, scaleSlider);
        propertiesView.getChildren().add(scaleBox);

        // Color group
        propertiesView.getChildren().add(createColorGroup(
            "Item Color",
            new String[]{"#8B5CF6", "#EF4444", "#10B981", "#3B82F6", "#F59E0B", "#FFFFFF", "#3D2B1F", "#735C44"},
            selectedItem.getColor(), c -> {
                selectedItem.setColor(c);
                onRefresh.run();
            }
        ));
        
        // Dimensions Display
        VBox dimBox = new VBox(8);
        Label dimLabel = new Label("Dimensions");
        dimLabel.setTextFill(Color.web("#8C94AF"));
        dimLabel.setFont(Font.font("Inter", 11));
        Label valLabel = new Label(selectedItem.getWidth() + "m \u00d7 " + selectedItem.getDepth() + "m \u00d7 " + selectedItem.getHeight() + "m");
        valLabel.setTextFill(Color.WHITE);
        valLabel.setFont(Font.font("Inter", 13));
        dimBox.getChildren().addAll(dimLabel, valLabel);
        propertiesView.getChildren().add(dimBox);

        // Action Buttons
        Button deleteBtn = new Button("\ud83d\uddd1  Remove Item");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setPrefHeight(42);
        deleteBtn.setStyle("-fx-background-color: rgba(239,68,68,0.1); -fx-text-fill: #EF4444; -fx-border-color: rgba(239,68,68,0.3); -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            room.getFurnitureItems().remove(selectedItem);
            setSelectedItem(null);
            onRefresh.run();
        });
        propertiesView.getChildren().add(deleteBtn);
    }

    private VBox createColorGroup(String labelText, String[] colors, String current, java.util.function.Consumer<String> onSelect) {
        VBox box = new VBox(8);
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(labelText);
        label.setTextFill(Color.web("#8C94AF"));
        label.setFont(Font.font("Inter", 11));
        
        ColorPicker picker = new ColorPicker(Color.web(current));
        picker.setStyle("-fx-background-color: #1d2440; -fx-background-radius: 6; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 6; -fx-color-label-visible: false;");
        picker.setPrefWidth(40);
        picker.setPrefHeight(24);
        picker.setOnAction(e -> {
            String hex = "#" + picker.getValue().toString().substring(2, 8).toUpperCase();
            onSelect.accept(hex);
            onRefresh.run();
        });
        
        header.getChildren().addAll(label, picker);
        box.getChildren().add(header);

        FlowPane swatches = new FlowPane(8, 8);
        for (String hex : colors) {
            Rectangle rect = new Rectangle(24, 24);
            try { rect.setFill(Color.web(hex)); } catch (Exception ignored) {}
            rect.setArcWidth(6); rect.setArcHeight(6);
            rect.setCursor(javafx.scene.Cursor.HAND);

            if (hex.equalsIgnoreCase(current)) {
                rect.setStroke(Color.web("#ca4bf6"));
                rect.setStrokeWidth(2);
            }

            rect.setOnMouseClicked(e -> {
                onSelect.accept(hex);
                picker.setValue(Color.web(hex));
                onRefresh.run();
                swatches.getChildren().forEach(n -> {
                    if (n instanceof Rectangle) {
                        ((Rectangle) n).setStroke(Color.TRANSPARENT);
                    }
                });
                rect.setStroke(Color.web("#ca4bf6"));
                rect.setStrokeWidth(2);
            });
            swatches.getChildren().add(rect);
        }
        box.getChildren().add(swatches);
        return box;
    }

    private GridPane buildCategoriesGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        String[] names = {"Seating", "Tables", "Storage", "Beds", "Decor", "Lighting"};
        String[] svgs = {
            "M4,18v3h3v-3h10v3h3v-3h1V2h-1H4z",
            "M4,18h16v-2H4z M2,14h20l-1-8H3z",
            "M3,2h18v20H3z M5,4v16h14V4z M7,6h10v2H7z",
            "M2,18h20v-4H2z M2,12l1-4h18l1,4z",
            "M12,2L4.5,20.29L5.21,21L12,18l6.79,3l0.71-0.71z",
            "M12,2C8.13,2,5,5.13,5,9c0,2.38,1.19,4.47,3,5.74V17c0,0.55,0.45,1,1,1h6c0.55,0,1-0.45,1-1v-2.26C16.81,13.47,19,11.38,19,9C19,5.13,15.87,2,12,2z"
        };

        for (int i = 0; i < names.length; i++) {
            final String cat = names[i];
            final String svg = svgs[i];
            VBox box = makeCategoryBox(cat, svg, cat.equals(activeCategory));
            box.setOnMouseClicked(e -> {
                activeCategory = cat;
                grid.getChildren().forEach(n -> {
                    if (n instanceof VBox) {
                        VBox b = (VBox) n;
                        Label lbl = (Label) b.getChildren().get(1);
                        boolean active = lbl.getText().equals(activeCategory);
                        b.setStyle("-fx-background-color: " + (active
                            ? "linear-gradient(to bottom right, #ca4bf6, #844cf5)"
                            : "#1d2440") + "; -fx-background-radius: 12; -fx-cursor: hand;");
                        lbl.setTextFill(active ? Color.WHITE : Color.web("#8C94AF"));
                        if (b.getChildren().get(0) instanceof SVGPath) {
                            ((SVGPath) b.getChildren().get(0)).setFill(active ? Color.WHITE : Color.web("#8C94AF"));
                        }
                    }
                });
                refreshItems();
            });
            grid.add(box, i % 3, i / 3);
        }
        return grid;
    }

    private VBox makeCategoryBox(String name, String svg, boolean active) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(85, 85);
        box.setStyle("-fx-background-color: " + (active
            ? "linear-gradient(to bottom right, #ca4bf6, #844cf5)"
            : "#1d2440") + "; -fx-background-radius: 12; -fx-cursor: hand;");

        SVGPath icon = new SVGPath();
        icon.setContent(svg);
        icon.setFill(active ? Color.WHITE : Color.web("#8C94AF"));
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        Label label = new Label(name);
        label.setTextFill(active ? Color.WHITE : Color.web("#8C94AF"));
        label.setFont(Font.font("Inter", 11));

        box.getChildren().addAll(icon, label);
        return box;
    }

    private void refreshItems() {
        itemsList.getChildren().clear();
        for (String[] item : CATALOG) {
            if (item[1].equals(activeCategory)) {
                itemsList.getChildren().add(createFurnitureItem(item[0], item[2], item[3], item[4], item[5]));
            }
        }
    }

    private HBox createFurnitureItem(String name, String w, String h, String d, String color) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color: #1d2440; -fx-background-radius: 12; -fx-cursor: hand;");

        Rectangle thumb = new Rectangle(40, 40);
        try { thumb.setFill(Color.web(color, 0.75)); } catch (Exception ex) { thumb.setFill(Color.web("#2D3450")); }
        thumb.setArcWidth(8);
        thumb.setArcHeight(8);

        VBox info = new VBox(3);
        Label nameLabel = new Label(name);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        Label dimLabel = new Label(w + "m × " + d + "m × " + h + "m");
        dimLabel.setTextFill(Color.web("#8C94AF"));
        dimLabel.setFont(Font.font("Inter", 11));
        info.getChildren().addAll(nameLabel, dimLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button addBtn = new Button("+");
        addBtn.setStyle("-fx-background-color: rgba(132,76,245,0.2); -fx-text-fill: #ca4bf6; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        addBtn.setPrefSize(32, 32);
        addBtn.setOnAction(e -> {
            FurnitureItem fi = new FurnitureItem();
            fi.setId(UUID.randomUUID().toString());
            fi.setName(name);
            fi.setType(activeCategory);
            fi.setWidth(Double.parseDouble(w));
            fi.setHeight(Double.parseDouble(h));
            fi.setDepth(Double.parseDouble(d));
            fi.setColor(color);
            fi.setX(Math.max(0, (room.getWidth() - fi.getWidth()) / 2.0));
            fi.setZ(Math.max(0, (room.getLength() - fi.getDepth()) / 2.0));
            fi.setY(0);
            room.addFurnitureItem(fi);
            onRefresh.run();
        });

        row.getChildren().addAll(thumb, info, addBtn);
        
        row.setOnDragDetected(e -> {
            javafx.scene.input.Dragboard db = row.startDragAndDrop(javafx.scene.input.TransferMode.COPY);
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString("FURNITURE:" + name + ":" + activeCategory + ":" + w + ":" + h + ":" + d + ":" + color);
            db.setContent(content);
            e.consume();
        });
        
        return row;
    }
}
