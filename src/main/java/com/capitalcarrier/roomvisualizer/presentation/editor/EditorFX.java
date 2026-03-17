package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class EditorFX {
    private Room room;
    private BorderPane root;
    private StackPane centerStack;
    private RoomViewport3DFX view3D;
    private RoomCanvas2DFX view2D;
    private SubScene subScene3D;
    private Label statusLabel;
    private PropertiesPanel propertiesPanel;
    private CatalogPanel catalogPanel;

    // Overlay nodes toggled on mode switch
    private Node snapGridNode;
    private Node lightingNode;
    private Node controls3DNode;
    private Node zoomBoxNode;

    public EditorFX(Room room, Stage stage) {
        this.room = room != null ? room : new Room();
        if (this.room.getWidth()  <= 0) this.room.setWidth(6);
        if (this.room.getLength() <= 0) this.room.setLength(8);
        if (this.room.getHeight() <= 0) this.room.setHeight(3);
        
        initializeUI(stage);
    }

    public void setDesignId(String id) {
        if (propertiesPanel != null) propertiesPanel.setDesignId(id);
    }

    private void initializeUI(Stage stage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #050A1E;");

        // We don't build a navbar here if it's hosted in DashboardFX
        // But for standalone start, it might need one. 
        // We'll let DashboardFX handle the top part.
        
        propertiesPanel = new PropertiesPanel(room, this::refreshViews, stage);
        root.setLeft(propertiesPanel);
        
        catalogPanel = new CatalogPanel(room, this::refreshViews);
        root.setRight(catalogPanel);

        view3D = new RoomViewport3DFX(room, item -> syncSelection(item, view3D));
        view2D = new RoomCanvas2DFX(room, item -> syncSelection(item, view2D), this::refreshViews);

        subScene3D = new SubScene(view3D, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.web("#050A1E"));
        subScene3D.setVisible(false);

        centerStack = new StackPane();
        centerStack.setStyle("-fx-background-color: #050A1E;");
        centerStack.getChildren().addAll(view2D, subScene3D);

        subScene3D.widthProperty().bind(centerStack.widthProperty());
        subScene3D.heightProperty().bind(centerStack.heightProperty());
        subScene3D.setCamera(view3D.getCamera());

        wire3DMouseControls();
        buildOverlay(centerStack);

        BorderPane canvasWrap = new BorderPane(centerStack);
        canvasWrap.setStyle("-fx-border-color: #1e2d5a; -fx-border-width: 1; -fx-border-radius: 4; " +
                            "-fx-background-color: #050A1E; -fx-background-radius: 4;");
        root.setCenter(canvasWrap);
    }

    private void syncSelection(FurnitureItem item, Object source) {
        if (source != view3D && view3D != null) view3D.setSelectedItem(item);
        if (source != view2D && view2D != null) view2D.setSelectedItem(item);
        
        if (catalogPanel != null) catalogPanel.setSelectedItem(item);
        
        if (statusLabel != null) {
            statusLabel.setText(item != null ? item.getName() + " selected" : statusText());
        }
    }

    public Node getContent() {
        return root;
    }

    public void start(Stage stage) {
        // Fallback for standalone boot
        BorderPane topLevel = new BorderPane(root);
        // topLevel.setTop(buildNavbar(stage)); // If needed
        
        Scene scene = new Scene(topLevel, 1440, 900);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ignored) {}
        stage.setScene(scene);
        stage.show();
    }

    // ─── 3D mouse controls ────────────────────────────────────────────────────

    private void wire3DMouseControls() {
        final double[] lastPos   = {0, 0};
        final double[] totalDrag = {0};

        subScene3D.setOnMousePressed(e -> {
            lastPos[0]   = e.getSceneX();
            lastPos[1]   = e.getSceneY();
            totalDrag[0] = 0;
        });

        subScene3D.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - lastPos[0];
            double dy = e.getSceneY() - lastPos[1];
            totalDrag[0] += Math.abs(dx) + Math.abs(dy);

            if (totalDrag[0] > 3) {
                if (e.isSecondaryButtonDown() || e.isMiddleButtonDown()) {
                    // Pan with secondary or middle button
                    view3D.pan(dx, dy);
                } else if (e.isPrimaryButtonDown()) {
                    // Rotate with primary button
                    view3D.getRotateY().setAngle(view3D.getRotateY().getAngle() + dx * 0.25);
                    view3D.getRotateX().setAngle(
                        Math.max(-89, Math.min(89,
                            view3D.getRotateX().getAngle() + dy * 0.20)));
                }
            }
            lastPos[0] = e.getSceneX();
            lastPos[1] = e.getSceneY();
        });

        subScene3D.setOnMouseClicked(e -> {
            if (totalDrag[0] < 5) {
                javafx.scene.input.PickResult pick = e.getPickResult();
                if (pick != null && pick.getIntersectedNode() != null) {
                    view3D.selectNode(pick.getIntersectedNode());
                } else {
                    view3D.clearSelection();
                }
            }
        });

        subScene3D.setOnScroll(e -> view3D.zoom(e.getDeltaY() * 0.06));
    }

    // ─── Navbar ───────────────────────────────────────────────────────────────


    // ─── Center overlay ───────────────────────────────────────────────────────

    private void buildOverlay(StackPane stack) {
        // ── 2D / 3D toggle pill ─────────────────────────────────────────────
        HBox togglePill = new HBox(0);
        togglePill.setStyle("-fx-background-color: #141932; -fx-background-radius: 8; -fx-padding: 4;");

        Button btn2D = new Button("2D View");
        btn2D.setStyle(activeToggle());
        Button btn3D = new Button("3D View");
        btn3D.setStyle(inactiveToggle());
        togglePill.getChildren().addAll(btn2D, btn3D);

        // ── Snap to Grid (2D only) ──────────────────────────────────────────
        CheckBox snapGrid = new CheckBox("Snap to Grid");
        snapGrid.setSelected(true);
        snapGrid.setStyle("-fx-text-fill: white; -fx-font-size: 13;");
        snapGrid.selectedProperty().addListener((obs, o, v) -> view2D.setSnapToGrid(v));
        snapGridNode = snapGrid;

        // ── Lighting button (3D only) ───────────────────────────────────────
        Button lightingBtn = buildLightingBtn();
        lightingBtn.setVisible(false);
        lightingNode = lightingBtn;

        // ── Fullscreen expand icon ──────────────────────────────────────────
        Button fsBtn = new Button();
        SVGPath fsIcon = new SVGPath();
        fsIcon.setContent("M7,14H5v5h5v-2H7V14z M5,10h2V7h3V5H5V10z M17,17h-3v2h5v-5h-2V17z M14,5v2h3v3h2V5H14z");
        fsIcon.setFill(Color.web("#8C94AF")); fsIcon.setScaleX(0.8); fsIcon.setScaleY(0.8);
        fsBtn.setGraphic(fsIcon);
        fsBtn.setStyle("-fx-background-color: rgba(20,25,50,0.7); -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5;");
        fsBtn.setOnAction(e -> {
            Stage s = (Stage) root.getScene().getWindow();
            s.setFullScreen(!s.isFullScreen());
        });

        // ── Top toolbar (inside canvas overlay, pinned to top) ──────────────
        Region tbSpacer = new Region();
        HBox.setHgrow(tbSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(16, togglePill, snapGrid, lightingBtn, tbSpacer, fsBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPrefHeight(46);
        topBar.setMaxHeight(Region.USE_PREF_SIZE);
        topBar.setPickOnBounds(false);
        topBar.setPadding(new Insets(0, 12, 0, 12));
        topBar.setStyle("-fx-background-color: rgba(8,12,40,0.92); " +
                        "-fx-border-color: rgba(255,255,255,0.06); -fx-border-width: 0 0 1 0;");
        StackPane.setAlignment(topBar, Pos.TOP_LEFT);
        stack.getChildren().add(topBar);

        // ── 3D Controls hint (bottom-left, 3D only) ─────────────────────────
        VBox hint = new VBox(4);
        hint.setStyle("-fx-background-color: rgba(10,15,40,0.82); -fx-background-radius: 10; -fx-padding: 12 14;");
        hint.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        Label hTitle = new Label("3D Controls");
        hTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        hint.getChildren().add(hTitle);
        hintLine(hint, "\u2022 Drag to rotate");
        hintLine(hint, "\u2022 Scroll to zoom");
        hintLine(hint, "\u2022 Right-click + drag to pan");
        hint.setVisible(false);
        controls3DNode = hint;
        StackPane.setAlignment(hint, Pos.BOTTOM_LEFT);
        StackPane.setMargin(hint, new Insets(0, 0, 60, 14));
        stack.getChildren().add(hint);

        // ── Zoom controls (2D, bottom-right) ────────────────────────────────
        Label zoomVal = new Label("100%");
        zoomVal.setStyle("-fx-text-fill: white; -fx-font-size: 10;");
        Button zIn  = zoomBtn("+");
        Button zOut = zoomBtn("\u2212");
        zIn.setOnAction(e  -> { view2D.setZoom(view2D.getZoom() + 10); zoomVal.setText(zoomPct()); });
        zOut.setOnAction(e -> { view2D.setZoom(view2D.getZoom() - 10); zoomVal.setText(zoomPct()); });

        VBox zoomBox = new VBox(4, zIn, zoomVal, zOut);
        zoomBox.setAlignment(Pos.CENTER);
        zoomBox.setPrefSize(44, 110);
        zoomBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        zoomBox.setPickOnBounds(false);
        zoomBox.setStyle("-fx-background-color: rgba(20,25,50,0.88); -fx-background-radius: 10; -fx-padding: 6;");
        zoomBoxNode = zoomBox;
        StackPane.setAlignment(zoomBox, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(zoomBox, new Insets(0, 14, 14, 0));
        stack.getChildren().add(zoomBox);

        // ── Status bar (bottom-centre) ───────────────────────────────────────
        statusLabel = new Label(statusText());
        statusLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        statusLabel.setPickOnBounds(false);
        statusLabel.setStyle("-fx-background-color: #141932; -fx-background-radius: 20; " +
            "-fx-padding: 6 18; -fx-text-fill: #8C94AF; -fx-font-size: 12;");
        StackPane.setAlignment(statusLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(statusLabel, new Insets(0, 0, 14, 0));
        stack.getChildren().add(statusLabel);

        // ── Wire toggle buttons ──────────────────────────────────────────────
        btn2D.setOnAction(e -> switchTo2D(btn2D, btn3D));
        btn3D.setOnAction(e -> switchTo3D(btn2D, btn3D));
    }

    private void switchTo2D(Button btn2D, Button btn3D) {
        subScene3D.setVisible(false); view2D.setVisible(true);
        btn2D.setStyle(activeToggle());   btn3D.setStyle(inactiveToggle());
        snapGridNode.setVisible(true);  lightingNode.setVisible(false);
        controls3DNode.setVisible(false); zoomBoxNode.setVisible(true);
    }

    private void switchTo3D(Button btn2D, Button btn3D) {
        subScene3D.setVisible(true); view2D.setVisible(false);
        btn3D.setStyle(activeToggle());   btn2D.setStyle(inactiveToggle());
        snapGridNode.setVisible(false); lightingNode.setVisible(true);
        controls3DNode.setVisible(true); zoomBoxNode.setVisible(false);
    }

    private Button buildLightingBtn() {
        SVGPath star = new SVGPath();
        star.setContent("M12,1L9.5,8.5H2l6,4.5L5.5,21L12,16.5L18.5,21L16,13l6-4.5H14.5L12,1z");
        star.setFill(Color.web("#F6D860")); star.setScaleX(0.7); star.setScaleY(0.7);
        Label txt = new Label("Lighting");
        txt.setTextFill(Color.WHITE); txt.setFont(Font.font("Inter", 13));
        HBox inner = new HBox(6, star, txt); inner.setAlignment(Pos.CENTER_LEFT);
        Button btn = new Button(); btn.setGraphic(inner);
        btn.setStyle("-fx-background-color: rgba(246,216,96,0.15); -fx-border-color: rgba(246,216,96,0.4); " +
                     "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 5 12;");
        btn.setOnAction(e -> showLightingPopup());
        return btn;
    }

    private void showLightingPopup() {
        Stage popup = new Stage();
        popup.setTitle("Lighting");
        VBox box = new VBox(14); box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #0F1437;"); box.setPrefWidth(280);
        Label title = new Label("Brightness");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        Slider slider = new Slider(10, 100, room.getBrightness());
        slider.setShowTickLabels(true); slider.setShowTickMarks(true); slider.setMajorTickUnit(30);
        Label val = new Label(room.getBrightness() + "%");
        val.setStyle("-fx-text-fill: #8C94AF; -fx-font-size: 12;");
        slider.valueProperty().addListener((obs, o, v) -> { val.setText(v.intValue() + "%"); view3D.setBrightness(v.intValue()); });
        box.getChildren().addAll(title, slider, val);
        popup.setScene(new Scene(box)); popup.setResizable(false); popup.show();
    }

    private void hintLine(VBox parent, String text) {
        Label l = new Label(text); l.setStyle("-fx-text-fill: #8C94AF; -fx-font-size: 11;");
        parent.getChildren().add(l);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String activeToggle() {
        return "-fx-background-color: #8B5CF6; -fx-text-fill: white; " +
               "-fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;";
    }

    private String inactiveToggle() {
        return "-fx-background-color: transparent; -fx-text-fill: #8C94AF; " +
               "-fx-font-weight: bold; -fx-cursor: hand;";
    }

    private Button zoomBtn(String text) {
        Button b = new Button(text); b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #8C94AF; " +
                   "-fx-font-size: 17; -fx-cursor: hand; -fx-padding: 0;");
        return b;
    }

    private String zoomPct() { return (int) (view2D.getZoom() / 50.0 * 100) + "%"; }

    private String statusText() {
        int n = room.getFurnitureItems() != null ? room.getFurnitureItems().size() : 0;
        return "Room: " + (int) room.getWidth() + "m \u00d7 " + (int) room.getLength()
               + "m \u00d7 " + (int) room.getHeight() + "m  \u2022  " + n + (n == 1 ? " item" : " items");
    }

    private void refreshViews() {
        if (view3D != null) view3D.updateFromRoom();
        if (view2D != null) view2D.draw();
        if (statusLabel != null) statusLabel.setText(statusText());
    }
}
