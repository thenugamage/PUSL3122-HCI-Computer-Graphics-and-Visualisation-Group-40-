package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
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
    private SubScene subScene3D;
    private Label statusLabel;
    private PropertiesPanel propertiesPanel;
    private CatalogPanel catalogPanel;

    // Overlay nodes toggled on mode switch
    private Node snapGridNode;
    private Node lightingNode;
    private Node controls3DNode;
    private Node zoomBoxNode;

    public EditorFX(Room room) {
        this.room = room != null ? room : new Room();
        if (this.room.getWidth()  <= 0) this.room.setWidth(6);
        if (this.room.getLength() <= 0) this.room.setLength(8);
        if (this.room.getHeight() <= 0) this.room.setHeight(3);
    }

    public void start(Stage stage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #050A1E;");

        root.setTop(buildNavbar(stage));
        propertiesPanel = new PropertiesPanel(room, this::refreshViews, stage);
        root.setLeft(propertiesPanel);
        catalogPanel = new CatalogPanel(room, this::refreshViews);
        root.setRight(catalogPanel);

        // Center: bordered canvas wrapper
        view3D = new RoomViewport3DFX(room);
        view2D = new RoomCanvas2DFX(room);

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

        // Wrap in a bordered container matching the reference UI
        BorderPane canvasWrap = new BorderPane(centerStack);
        canvasWrap.setStyle("-fx-border-color: #1e2d5a; -fx-border-width: 1; -fx-border-radius: 4; " +
                            "-fx-background-color: #050A1E; -fx-background-radius: 4;");
        BorderPane.setMargin(centerStack, new Insets(0));

        root.setCenter(canvasWrap);

        Scene scene = new Scene(root, 1440, 900);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.setTitle("Room Visualizer \u2013 " + room.getName());
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
                    com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem sel =
                        view3D.selectNode(pick.getIntersectedNode());
                    if (statusLabel != null)
                        statusLabel.setText(sel != null ? sel.getName() + " selected" : statusText());
                    if (catalogPanel != null)
                        catalogPanel.setSelectedItem(sel);
                } else {
                    view3D.clearSelection();
                    if (statusLabel != null) statusLabel.setText(statusText());
                    if (catalogPanel != null) catalogPanel.setSelectedItem(null);
                }
            }
        });

        subScene3D.setOnScroll(e -> view3D.zoom(e.getDeltaY() * 0.06));
    }

    // ─── Navbar ───────────────────────────────────────────────────────────────

    private Node buildNavbar(Stage stage) {
        HBox nav = new HBox(28);
        nav.setPrefHeight(60);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(0, 24, 0, 24));
        nav.setStyle("-fx-background-color: #0F1437; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 0 1 0;");

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        SVGPath logoIcon = new SVGPath();
        logoIcon.setContent("M12,2L4.5,20.29L5.21,21L12,18l6.79,3l0.71-0.71z");
        logoIcon.setFill(Color.web("#8B5CF6"));
        Text logoText = new Text("Room Visualizer");
        logoText.setFill(Color.WHITE);
        logoText.setFont(Font.font("Inter", FontWeight.BOLD, 17));
        logoBox.getChildren().addAll(logoIcon, logoText);

        HBox menu = new HBox(8);
        menu.setAlignment(Pos.CENTER_LEFT);
        menu.getChildren().addAll(
            navItem("My Designs",  "M3,13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z", false, stage),
            navItem("New Design",  "M19,13H13v6h-2v-6H5v-2h6V5h2v6h6V13z", true,  stage),
            navItem("My Rooms",    "M10,20v-6h4v6h5v-8h3L12,3L2,12h3v8H10z",  false, stage),
            navItem("Settings",
                "M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.68 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z",
                false, stage)
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String username = "User";
        try {
            User u = AuthService.getCurrentUser();
            if (u != null && u.getUsername() != null) username = u.getUsername();
        } catch (Exception ignored) {}

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(5, 12, 5, 12));
        userBox.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 20;");
        SVGPath userIcon = new SVGPath();
        userIcon.setContent("M12,12c2.21,0,4-1.79,4-4s-1.79-4-4-4s-4,1.79-4,4S9.79,12,12,12z M12,14c-2.67,0-8,1.34-8,4v2h16v-2C20,15.34,14.67,14,12,14z");
        userIcon.setFill(Color.web("#8C94AF")); userIcon.setScaleX(0.75); userIcon.setScaleY(0.75);
        Label userLbl = new Label(username);
        userLbl.setTextFill(Color.web("#8C94AF")); userLbl.setFont(Font.font("Inter", 12));
        String init = username.isEmpty() ? "U" : String.valueOf(username.charAt(0)).toUpperCase();
        Circle avCircle = new Circle(14, Color.web("#8B5CF6"));
        Text avText = new Text(init);
        avText.setFill(Color.WHITE); avText.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        StackPane avPane = new StackPane(avCircle, avText);
        userBox.getChildren().addAll(userIcon, userLbl, avPane);

        nav.getChildren().addAll(logoBox, menu, spacer, userBox);
        return nav;
    }

    private Node navItem(String text, String svg, boolean active, Stage stage) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER);
        item.setPadding(new Insets(7, 12, 7, 12));
        item.setCursor(javafx.scene.Cursor.HAND);
        if (active) item.setStyle("-fx-background-color: rgba(139,92,246,0.15); -fx-background-radius: 8;");
        SVGPath icon = new SVGPath();
        icon.setContent(svg);
        icon.setFill(active ? Color.web("#8B5CF6") : Color.web("#8C94AF"));
        icon.setScaleX(0.65); icon.setScaleY(0.65);
        Label lbl = new Label(text);
        lbl.setTextFill(active ? Color.WHITE : Color.web("#8C94AF"));
        lbl.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        item.getChildren().addAll(icon, lbl);
        item.setOnMouseClicked(e -> {
            if (text.equals("My Designs") || text.equals("My Rooms")) new DashboardFX().start(stage);
        });
        return item;
    }

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
