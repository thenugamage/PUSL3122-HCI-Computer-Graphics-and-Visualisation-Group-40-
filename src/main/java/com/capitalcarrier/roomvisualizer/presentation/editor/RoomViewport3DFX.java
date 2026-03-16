package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX Y-DOWN convention:
 *   Y=0 → ceiling/top, Y=h → floor/bottom.
 *   Within a furniture Group centered at its bounding-box mid:
 *     Y = +h/2 → floor side, Y = -h/2 → ceiling side.
 */
public class RoomViewport3DFX extends Group {

    private final Room room;
    private final Group roomGroup;
    private PerspectiveCamera camera;

    // Default angles chosen so the room sits level (floor horizontal, walls vertical).
    // rotateX must be 0 at rest — the camera's own −30° tilt already provides the
    // top-down perspective.  Adding a room X rotation on top creates a compound roll
    // that makes the room appear tilted sideways on screen.
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-30, Rotate.Y_AXIS);

    private Translate cameraTranslate;
    private Rotate cameraTiltRotate;
    private double baseZ;
    private double baseY;

    private AmbientLight ambientLight;
    private PointLight   keyLight;
    private PointLight   fillLight;
    private PointLight   topLight;

    private Translate centeringTranslate;
    private final Group gridGroup = new Group();

    private Box floorBox, backWallBox, leftWallBox, rightWallBox;
    private PhongMaterial gridMaterial;

    private final Map<Group, FurnitureItem> furnitureMap = new HashMap<>();
    private Group selectedGroup = null;
    private FurnitureItem selectedData;
    private java.util.function.Consumer<FurnitureItem> onSelectionChanged;

    // ── Constructor ────────────────────────────────────────────────────────────

    public RoomViewport3DFX(Room room, java.util.function.Consumer<FurnitureItem> onSelectionChanged) {
        this.room = room;
        this.onSelectionChanged = onSelectionChanged;
        roomGroup = new Group();

        double w = room.getWidth();
        double h = room.getHeight();
        double l = room.getLength();

        // Centering translate must be the LAST entry in getTransforms() so it is
        // applied FIRST (innermost), moving the room centre to world origin BEFORE
        // the orbit rotations are applied.  Using setTranslate() instead would apply
        // it AFTER the rotations, which makes the room orbit around its corner.
        centeringTranslate = new Translate(-w / 2.0, -h / 2.0, -l / 2.0);
        roomGroup.getTransforms().addAll(
                rotateY,
                rotateX,
                centeringTranslate);

        getChildren().add(roomGroup);

        buildRoom();
        buildCamera();
        buildLights();
    }

    // ── Room geometry ──────────────────────────────────────────────────────────

    private void buildRoom() {
        double w = room.getWidth();
        double h = room.getHeight();
        double l = room.getLength();

        String fHex = room.getFloorColor() != null ? room.getFloorColor() : "#6d573f";
        String wHex = room.getWallColor()  != null ? room.getWallColor()  : "#DCD2BE";

        // Floor slab — sits at Y=h (bottom in Y-down)
        floorBox = new Box(w, 0.08, l);
        floorBox.setCullFace(CullFace.NONE);
        floorBox.setMaterial(floorMaterial(fHex));
        floorBox.setTranslateX(w / 2);
        floorBox.setTranslateY(h);
        floorBox.setTranslateZ(l / 2);

        // Back wall (at Z=L) — this ensures we look INTO the room
        backWallBox = new Box(w, h, 0.08);
        backWallBox.setCullFace(CullFace.NONE);
        backWallBox.setMaterial(wallMaterial(wHex));
        backWallBox.setTranslateX(w / 2);
        backWallBox.setTranslateY(h / 2);
        backWallBox.setTranslateZ(l);

        // Left wall (at X=0)
        leftWallBox = new Box(0.08, h, l);
        leftWallBox.setCullFace(CullFace.NONE);
        leftWallBox.setMaterial(wallMaterial(wHex));
        leftWallBox.setTranslateY(h / 2);
        leftWallBox.setTranslateZ(l / 2);

        // Right wall (at X=w)
        rightWallBox = new Box(0.08, h, l);
        rightWallBox.setCullFace(CullFace.NONE);
        rightWallBox.setMaterial(wallMaterial(wHex));
        rightWallBox.setTranslateX(w);
        rightWallBox.setTranslateY(h / 2);
        rightWallBox.setTranslateZ(l / 2);

        roomGroup.getChildren().addAll(floorBox, backWallBox, leftWallBox, rightWallBox, gridGroup);

        addFloorGrid(fHex);

        if (room.getFurnitureItems() != null)
            room.getFurnitureItems().forEach(this::addFurniture);
    }

    /** Subtle floor grid — darkened version of the floor colour, not bright cyan. */
    private void addFloorGrid(String floorHex) {
        double w  = room.getWidth();
        double h  = room.getHeight();
        double l  = room.getLength();
        double gY = h + 0.015;

        gridMaterial = new PhongMaterial(Color.web("#00FBFF", 0.45));
        gridMaterial.setSpecularColor(Color.TRANSPARENT);

        gridGroup.getChildren().clear();

        for (double x = 0; x <= w + 0.01; x += 1.0) {
            Box line = new Box(0.018, 0.008, l + 0.1);
            line.setTranslateX(Math.min(x, w));
            line.setTranslateY(gY);
            line.setTranslateZ(l / 2);
            line.setCullFace(CullFace.NONE);
            line.setMaterial(gridMaterial);
            gridGroup.getChildren().add(line);
        }
        for (double z = 0; z <= l + 0.01; z += 1.0) {
            Box line = new Box(w + 0.1, 0.008, 0.018);
            line.setTranslateX(w / 2);
            line.setTranslateY(gY);
            line.setTranslateZ(Math.min(z, l));
            line.setCullFace(CullFace.NONE);
            line.setMaterial(gridMaterial);
            gridGroup.getChildren().add(line);
        }
    }


    // ── Materials ──────────────────────────────────────────────────────────────

    /** Matte wall-paint material. */
    private PhongMaterial wallMaterial(String hex) {
        PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(parseColor(hex, "#DCD2BE"));
        m.setSpecularColor(Color.gray(0.07));
        m.setSpecularPower(90);
        return m;
    }

    /** Slightly glossy floor material (wood / tile). */
    private PhongMaterial floorMaterial(String hex) {
        PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(parseColor(hex, "#6d573f"));
        m.setSpecularColor(Color.gray(0.22));
        m.setSpecularPower(18);
        return m;
    }

    private PhongMaterial furnitureMat(Color diffuse) {
        PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(diffuse);
        m.setSpecularColor(Color.gray(0.28));
        m.setSpecularPower(22);
        return m;
    }

    private PhongMaterial furnitureMat(Color diffuse, double specPower) {
        PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(diffuse);
        m.setSpecularColor(Color.gray(0.38));
        m.setSpecularPower(specPower);
        return m;
    }

    // ── Camera ─────────────────────────────────────────────────────────────────

    private void buildCamera() {
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.01);
        camera.setFarClip(2000);
        camera.setFieldOfView(55);   // wide enough for the room to fill the viewport

        double span = Math.max(room.getWidth(), room.getLength());
        baseZ = -span * 1.2;   // closer than before so the room fills the frame

        // Place the camera just 0.3 m above the room ceiling so wall tops are NOT
        // visible and we get an immersive interior-corner view like the reference UI.
        // In JavaFX Y-down: room ceiling sits at world Y = −h/2 (negative = upward).
        double ceilingY  = -(room.getHeight() / 2.0);
        baseY = ceilingY - 0.3;          // 0.3 m above the ceiling

        // Aim the camera at the floor centre, not world origin, so the floor fills
        // the lower portion of the viewport and the back wall fills the upper portion.
        double floorY    = room.getHeight() / 2.0;   // world Y of the floor
        double tiltDeg   = Math.toDegrees(
                Math.atan2(floorY - baseY, -baseZ));  // positive = look downward

        cameraTranslate = new Translate(0, baseY, baseZ);
        cameraTiltRotate = new Rotate(-tiltDeg, Rotate.X_AXIS);

        // CRITICAL ORDER: Translate FIRST so the camera is positioned correctly,
        // then the tilt rotation orients the look direction downward.
        camera.getTransforms().addAll(cameraTranslate, cameraTiltRotate);
    }

    // ── Lighting ───────────────────────────────────────────────────────────────

    private void buildLights() {
        // Warm ambient fill — prevents pitch-black shadows
        ambientLight = new AmbientLight(Color.rgb(175, 165, 148));

        // Key light — white, from camera-front-top
        keyLight  = new PointLight(Color.WHITE);

        // Fill light — cool blue-white from the right side
        fillLight = new PointLight(Color.rgb(180, 195, 220));

        // Top ceiling fill — warm neutral from above room centre
        topLight  = new PointLight(Color.rgb(255, 248, 235));

        getChildren().addAll(ambientLight, keyLight, fillLight, topLight);
        positionLights();
    }

    public void updateLighting() {
        positionLights();
        applyBrightness();
    }

    private void positionLights() {
        if (keyLight == null) return;

        // Use room's lighting properties (scaled from cm to meters if needed)
        // Mockup values: Light X: 82, Light Y: -600, Light Z: 0
        // We'll treat these as offsets or direct positions. 
        // For consistency with mockup feel, we use direct positions scaled down.
        
        keyLight.setTranslateX(room.getLightX() / 100.0);
        keyLight.setTranslateY(room.getLightY() / 100.0);
        keyLight.setTranslateZ(room.getLightZ() / 100.0);

        // Fill and Top lights remain auxiliary but follow brightness
        double h = room.getHeight();
        double span = Math.max(room.getWidth(), room.getLength());
        double aboveCeiling = -(h / 2.0 + 1.0);

        fillLight.setTranslateX(span * 1.5);
        fillLight.setTranslateY(aboveCeiling);
        fillLight.setTranslateZ(0);

        topLight.setTranslateX(0);
        topLight.setTranslateY(aboveCeiling);
        topLight.setTranslateZ(0);

        applyBrightness();
    }

    private void applyBrightness() {
        if (keyLight == null) return;
        double b = Math.max(0.1, Math.min(1.0, room.getBrightness() / 100.0));
        keyLight.setColor(Color.gray(b));
        fillLight.setColor(Color.rgb(
            (int)(180 * b * 0.55),
            (int)(195 * b * 0.55),
            (int)(220 * b * 0.55)));
        topLight.setColor(Color.gray(b * 0.38));
        ambientLight.setColor(Color.gray(0.32 + b * 0.22));
    }

    // ── Furniture dispatch ─────────────────────────────────────────────────────

    private void addFurniture(FurnitureItem item) {
        double roomH = room.getHeight();
        Group fg = buildFurnitureGroup(item);

        double scale = item.getScale();
        fg.setTranslateX(item.getX() + (item.getWidth() * scale) / 2.0);
        fg.setTranslateY(roomH - (item.getHeight() * scale) / 2.0 - item.getY());
        fg.setTranslateZ(item.getZ() + (item.getDepth() * scale) / 2.0);

        if (item.getRotation() != 0)
            fg.getTransforms().add(new Rotate(item.getRotation(), Rotate.Y_AXIS));
        if (scale != 1.0)
            fg.getTransforms().add(new javafx.scene.transform.Scale(scale, scale, scale));

        // Apply item's custom color to its boxes
        for (Node node : fg.getChildren()) {
            if (node instanceof Box) {
                Box b = (Box) node;
                if (b.getMaterial() instanceof PhongMaterial) {
                    PhongMaterial m = (PhongMaterial) b.getMaterial();
                    m.setDiffuseColor(parseColor(item.getColor(), "#6d573f"));
                }
            }
        }

        setupFurnitureDragging(fg, item);
        
        fg.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                selectNode(fg);
                e.consume();
            }
        });

        furnitureMap.put(fg, item);
        roomGroup.getChildren().add(fg);
    }

    /** Real-time update for a furniture item's visual state. */
    public void updateFurniture(com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem item) {
        Group fg = null;
        for (Map.Entry<Group, com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem> entry : furnitureMap.entrySet()) {
            if (entry.getValue() == item) {
                fg = entry.getKey();
                break;
            }
        }
        if (fg == null) return;

        double roomH = room.getHeight();
        double scale = item.getScale();
        fg.setTranslateX(item.getX() + (item.getWidth() * scale) / 2.0);
        fg.setTranslateY(roomH - (item.getHeight() * scale) / 2.0 - item.getY());
        fg.setTranslateZ(item.getZ() + (item.getDepth() * scale) / 2.0);

        fg.getTransforms().removeIf(t -> t instanceof Rotate || t instanceof javafx.scene.transform.Scale);
        if (item.getRotation() != 0)
            fg.getTransforms().add(new Rotate(item.getRotation(), Rotate.Y_AXIS));
        if (scale != 1.0)
            fg.getTransforms().add(new javafx.scene.transform.Scale(scale, scale, scale));

        // Update colors
        for (Node node : fg.getChildren()) {
            if (node instanceof Box) {
                Box b = (Box) node;
                if (b.getMaterial() instanceof PhongMaterial) {
                    PhongMaterial m = (PhongMaterial) b.getMaterial();
                    m.setDiffuseColor(parseColor(item.getColor(), "#6d573f"));
                }
            }
        }
    }

    private void setupFurnitureDragging(Group fg, FurnitureItem item) {
        final double[] startPos = new double[2];

        fg.setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown()) {
                startPos[0] = e.getSceneX();
                startPos[1] = e.getSceneY();
                e.consume(); // Prevent camera from starting a drag
            }
        });

        fg.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                double dx = e.getSceneX() - startPos[0];
                double dy = e.getSceneY() - startPos[1];

                // Simple projection mapping: we use a scale factor to move the item on the floor
                // 0.05 is a rough heuristic for the current camera zoom/distance mapping
                double moveScale = 0.05;
                double newX = item.getX() + dx * moveScale;
                double newZ = item.getZ() - dy * moveScale;

                double scale = item.getScale();
                double limitX = room.getWidth() - item.getWidth() * scale;
                double limitZ = room.getLength() - item.getDepth() * scale;
                item.setX(Math.max(0, Math.min(limitX, newX)));
                item.setZ(Math.max(0, Math.min(limitZ, newZ)));

                // Apply transform
                fg.setTranslateX(item.getX() + (item.getWidth() * scale) / 2.0);
                fg.setTranslateZ(item.getZ() + (item.getDepth() * scale) / 2.0);

                startPos[0] = e.getSceneX();
                startPos[1] = e.getSceneY();
                e.consume();
            }
        });
    }

    private Group buildFurnitureGroup(FurnitureItem item) {
        String name = item.getName() != null ? item.getName() : "";
        double w = item.getWidth();
        double h = item.getHeight();
        double d = item.getDepth();
        Color  c = parseColor(item.getColor(), "#8B5CF6");

        switch (name) {
            case "Modern Sofa":   return buildSofa(w, h, d, c);
            case "Armchair":      return buildArmchair(w, h, d, c);
            case "Dining Chair":  return buildDiningChair(w, h, d, c);
            case "Ottoman":       return buildOttoman(w, h, d, c);
            case "Bench":         return buildBench(w, h, d, c);
            case "Dining Table":
            case "Coffee Table":
            case "Side Table":
            case "Study Desk":    return buildTable(w, h, d, c);
            case "Bookshelf":     return buildBookshelf(w, h, d, c);
            case "Wardrobe":      return buildWardrobe(w, h, d, c);
            case "Cabinet":
            case "TV Stand":      return buildCabinet(w, h, d, c);
            case "Single Bed":
            case "Double Bed":    return buildBed(w, h, d, c);
            case "Bunk Bed":      return buildBunkBed(w, h, d, c);
            case "Floor Lamp":    return buildFloorLamp(w, h, d, c);
            case "Table Lamp":    return buildTableLamp(w, h, d, c);
            case "Ceiling Light": return buildCeilingLight(w, h, d, c);
            case "Vase":          return buildVase(w, h, d, c);
            case "Plant":         return buildPlant(w, h, d, c);
            case "Mirror":        return buildMirror(w, h, d, c);
            case "Painting":      return buildPainting(w, h, d, c);
            default:              return buildGenericBox(w, h, d, c);
        }
    }

    // ── Low-level helpers ──────────────────────────────────────────────────────

    /**
     * Creates a Box positioned at (tx, ty, tz) inside parent group g.
     * Y coordinate convention: +h/2 = floor side, -h/2 = ceiling side.
     */
    private void place(Group g, double w, double h, double d, Color color,
                       double tx, double ty, double tz) {
        Box b = new Box(w, h, d);
        b.setCullFace(CullFace.NONE);
        b.setMaterial(furnitureMat(color));
        b.setTranslateX(tx); b.setTranslateY(ty); b.setTranslateZ(tz);
        g.getChildren().add(b);
    }

    private void place(Group g, double w, double h, double d, Color color,
                       double tx, double ty, double tz, double specPower) {
        Box b = new Box(w, h, d);
        b.setCullFace(CullFace.NONE);
        b.setMaterial(furnitureMat(color, specPower));
        b.setTranslateX(tx); b.setTranslateY(ty); b.setTranslateZ(tz);
        g.getChildren().add(b);
    }

    private void placeWithMat(Group g, double w, double h, double d,
                              PhongMaterial mat, double tx, double ty, double tz) {
        Box b = new Box(w, h, d);
        b.setCullFace(CullFace.NONE);
        b.setMaterial(mat);
        b.setTranslateX(tx); b.setTranslateY(ty); b.setTranslateZ(tz);
        g.getChildren().add(b);
    }

    private PhongMaterial metalMat() {
        PhongMaterial m = new PhongMaterial(Color.rgb(190, 190, 195));
        m.setSpecularColor(Color.WHITE);
        m.setSpecularPower(80);
        return m;
    }

    private PhongMaterial silverMat() {
        PhongMaterial m = new PhongMaterial(Color.SILVER);
        m.setSpecularColor(Color.WHITE);
        m.setSpecularPower(90);
        return m;
    }

    // ── Seating ────────────────────────────────────────────────────────────────

    private Group buildSofa(double w, double h, double d, Color c) {
        Group g = new Group();
        double seatH = h * 0.42;
        double backD  = d * 0.22;
        double armW   = Math.min(0.15, w * 0.09);
        double armH   = h * 0.72;
        Color dark    = c.darker();
        Color darker  = dark.darker();

        // Seat cushion
        place(g, w, seatH, d, c,    0,              h/2 - seatH/2,   0);
        // Back rest (full height, rear)
        place(g, w, h, backD, dark, 0,              0,               d/2 - backD/2);
        // Arms
        place(g, armW, armH, d, darker, -w/2 + armW/2, h/2 - armH/2, 0);
        place(g, armW, armH, d, darker,  w/2 - armW/2, h/2 - armH/2, 0);
        return g;
    }

    private Group buildArmchair(double w, double h, double d, Color c) {
        Group g = new Group();
        double seatH = h * 0.40;
        double backD  = d * 0.25;
        double armW   = Math.min(0.12, w * 0.14);
        double armH   = h * 0.68;
        Color dark    = c.darker();

        place(g, w, seatH, d, c,    0,              h/2 - seatH/2,   0);
        place(g, w, h, backD, dark, 0,              0,               d/2 - backD/2);
        place(g, armW, armH, d, dark.darker(), -w/2 + armW/2, h/2 - armH/2, 0);
        place(g, armW, armH, d, dark.darker(),  w/2 - armW/2, h/2 - armH/2, 0);
        return g;
    }

    private Group buildDiningChair(double w, double h, double d, Color c) {
        Group g = new Group();
        double seatFloor = h * 0.48;   // height of seat panel top from floor
        double seatT     = 0.04;
        double backH     = h - seatFloor;
        double backT     = 0.04;
        double legW      = 0.04;
        Color dark = c.darker();

        // Seat panel
        place(g, w, seatT, d, c, 0, h/2 - seatFloor, 0);
        // Back rail
        place(g, w, backH, backT, dark, 0,
              h/2 - seatFloor - backH / 2, d/2 - backT / 2);
        // 4 legs
        double lx = w / 2 - legW;
        double lz = d / 2 - legW;
        double lY = h / 2 - seatFloor / 2;
        place(g, legW, seatFloor, legW, dark.darker(), -lx, lY,  lz);
        place(g, legW, seatFloor, legW, dark.darker(),  lx, lY,  lz);
        place(g, legW, seatFloor, legW, dark.darker(), -lx, lY, -lz);
        place(g, legW, seatFloor, legW, dark.darker(),  lx, lY, -lz);
        return g;
    }

    private Group buildOttoman(double w, double h, double d, Color c) {
        Group g = new Group();
        double bodyH = h * 0.75;
        double topH  = h * 0.25;

        place(g, w, bodyH, d, c,            0, h/2 - bodyH/2, 0);
        place(g, w * 0.86, topH, d * 0.86, c.brighter(), 0, -h/2 + topH/2, 0);
        return g;
    }

    private Group buildBench(double w, double h, double d, Color c) {
        Group g = new Group();
        double seatT = h * 0.32;
        double legH  = h - seatT;
        double legW  = 0.06;
        Color dark = c.darker();

        place(g, w, seatT, d, c, 0, -h/2 + seatT/2, 0);
        place(g, legW, legH, d * 0.7, dark, -w/2 + legW/2 + 0.05, h/2 - legH/2, 0);
        place(g, legW, legH, d * 0.7, dark,  w/2 - legW/2 - 0.05, h/2 - legH/2, 0);
        return g;
    }

    // ── Tables ─────────────────────────────────────────────────────────────────

    private Group buildTable(double w, double h, double d, Color c) {
        Group g = new Group();
        double topT  = Math.min(0.05, h * 0.08);
        double legH  = h - topT;
        double legW  = Math.min(0.06, w * 0.06);
        Color dark = c.darker();

        // Table top
        place(g, w, topT, d, c, 0, -h/2 + topT/2, 0);
        // 4 legs
        double lx = w/2 - legW;
        double lz = d/2 - legW;
        double lY = h/2 - legH/2;
        place(g, legW, legH, legW, dark, -lx, lY,  lz);
        place(g, legW, legH, legW, dark,  lx, lY,  lz);
        place(g, legW, legH, legW, dark, -lx, lY, -lz);
        place(g, legW, legH, legW, dark,  lx, lY, -lz);
        return g;
    }

    // ── Storage ────────────────────────────────────────────────────────────────

    private Group buildBookshelf(double w, double h, double d, Color c) {
        Group g = new Group();
        double wallT = 0.04;
        Color dark   = c.darker();
        Color shelf  = c.brighter();

        // Side panels
        place(g, wallT, h, d, dark, -w/2 + wallT/2, 0, 0);
        place(g, wallT, h, d, dark,  w/2 - wallT/2, 0, 0);
        // Top and bottom panels
        place(g, w, wallT, d, dark, 0, -h/2 + wallT/2, 0);
        place(g, w, wallT, d, dark, 0,  h/2 - wallT/2, 0);
        // Back panel
        place(g, w, h, wallT * 0.5, dark.darker(), 0, 0, d/2 - wallT * 0.25);
        // 3 equally-spaced shelves
        for (int i = 1; i <= 3; i++) {
            double yFrac = (double) i / 4.0;
            place(g, w - wallT * 2, wallT * 0.75, d, shelf, 0, h/2 - yFrac * h, 0);
        }
        return g;
    }

    private Group buildWardrobe(double w, double h, double d, Color c) {
        Group g = new Group();
        double wallT = 0.03;
        double baseH = 0.08;
        Color dark = c.darker();
        Color frame = dark.darker();

        // Base pedestal
        place(g, w * 0.96, baseH, d * 0.96, frame, 0, h/2 - baseH/2, 0);
        
        // Main carcass
        place(g, w, h - baseH, d, c, 0, -baseH/2, 0);
        
        // Two door panels (inset slightly)
        double doorW = (w - wallT * 3) / 2;
        double doorH = h - baseH - wallT * 2;
        place(g, doorW, doorH, 0.015, dark, -doorW/2 - wallT/2, -baseH/2, -d/2 + 0.01);
        place(g, doorW, doorH, 0.015, dark,  doorW/2 + wallT/2, -baseH/2, -d/2 + 0.01);
        
        // Vertical Handles
        placeWithMat(g, 0.02, h * 0.25, 0.03, silverMat(), -0.05, -baseH/2, -d/2 - 0.01);
        placeWithMat(g, 0.02, h * 0.25, 0.03, silverMat(),  0.05, -baseH/2, -d/2 - 0.01);
        
        return g;
    }

    private Group buildCabinet(double w, double h, double d, Color c) {
        Group g = new Group();
        double topT = 0.04;
        double baseH = 0.06;
        Color dark = c.darker();
        
        // Overhanging top
        place(g, w + 0.04, topT, d + 0.04, c.brighter(), 0, -h/2 + topT/2, 0);
        
        // Body
        place(g, w, h - topT - baseH, d, c, 0, (topT - baseH)/2, 0);
        
        // Base
        place(g, w * 0.92, baseH, d * 0.92, dark.darker(), 0, h/2 - baseH/2, 0);
        
        // Drawer dividers (horizontal grooves)
        place(g, w * 0.95, 0.01, 0.02, dark, 0, -0.05, -d/2 + 0.01);
        place(g, w * 0.95, 0.01, 0.02, dark, 0,  0.15, -d/2 + 0.01);
        
        // Handles
        placeWithMat(g, 0.15, 0.02, 0.02, silverMat(), 0, -0.15, -d/2 - 0.01);
        placeWithMat(g, 0.15, 0.02, 0.02, silverMat(), 0,  0.05, -d/2 - 0.01);
        
        return g;
    }

    // ── Beds ───────────────────────────────────────────────────────────────────

    private Group buildBed(double w, double h, double d, Color c) {
        Group g = new Group();
        double frameH    = h * 0.35;
        double mattressH = h * 0.30;
        double headH     = h;
        double headT     = 0.10;
        double footH     = h * 0.55;
        double footT     = 0.08;
        Color frame    = c.darker();
        Color mattress = Color.rgb(240, 237, 232);
        Color pillow   = Color.WHITE;

        // Bed frame base
        place(g, w, frameH, d, frame, 0, h/2 - frameH/2, 0);
        // Mattress on top of frame
        place(g, w * 0.92, mattressH, d * 0.85, mattress,
              0, h/2 - frameH - mattressH/2, 0);
        // Headboard at -Z end
        place(g, w, headH, headT, frame, 0, 0, -d/2 + headT/2);
        // Footboard at +Z end
        place(g, w, footH, footT, frame, 0, h/2 - footH/2, d/2 - footT/2);
        // Pillow(s) — one for single, two for double
        int pillows  = w > 1.5 ? 2 : 1;
        double pW    = w * (pillows == 2 ? 0.38 : 0.60);
        double pZpos = -d/2 + headT + d * 0.12;
        double pY    = h/2 - frameH - mattressH - 0.03;
        if (pillows == 1) {
            place(g, pW, 0.06, d * 0.22, pillow, 0, pY, pZpos);
        } else {
            place(g, pW, 0.06, d * 0.22, pillow, -w * 0.24, pY, pZpos);
            place(g, pW, 0.06, d * 0.22, pillow,  w * 0.24, pY, pZpos);
        }
        return g;
    }

    private Group buildBunkBed(double w, double h, double d, Color c) {
        Group g = new Group();
        Color frame    = c.darker();
        Color mattress = Color.rgb(240, 237, 232);
        double postW   = 0.07;
        double bedT    = 0.16;

        // Bottom bunk
        place(g, w, bedT, d, frame, 0, h/2 - bedT/2, 0);
        place(g, w * 0.88, 0.11, d * 0.84, mattress, 0, h/2 - bedT - 0.055, 0);
        // Top bunk
        double topY = -h/2 + bedT/2;
        place(g, w, bedT, d, frame, 0, topY, 0);
        place(g, w * 0.88, 0.11, d * 0.84, mattress, 0, topY - bedT/2 - 0.055, 0);
        // 4 corner posts full height
        place(g, postW, h, postW, frame, -w/2 + postW/2, 0,  d/2 - postW/2);
        place(g, postW, h, postW, frame,  w/2 - postW/2, 0,  d/2 - postW/2);
        place(g, postW, h, postW, frame, -w/2 + postW/2, 0, -d/2 + postW/2);
        place(g, postW, h, postW, frame,  w/2 - postW/2, 0, -d/2 + postW/2);
        // Ladder rungs on right side
        for (int r = 0; r < 3; r++) {
            double ry = h/2 - (r + 0.8) * h / 3.5;
            place(g, postW, postW, d * 0.22, frame, w/2 - postW/2, ry, 0);
        }
        return g;
    }

    // ── Lighting fixtures ──────────────────────────────────────────────────────

    private Group buildFloorLamp(double w, double h, double d, Color c) {
        Group g = new Group();
        double baseH  = 0.05;
        double poleW  = 0.03;
        double shadeH = h * 0.22;
        double poleH  = h - baseH - shadeH;
        
        // Base
        placeWithMat(g, w, baseH, d, metalMat(), 0, h/2 - baseH/2, 0);
        
        // Slim vertical pole
        placeWithMat(g, poleW, poleH, poleW, metalMat(), 0, h/2 - baseH - poleH/2, 0);
        
        // Shade with glow
        Color shadeColor = Color.web("#FFF9E5");
        place(g, w * 1.2, shadeH, d * 1.2, shadeColor, 0, -h/2 + shadeH/2, 0, 15);
        
        // Internal glow sphere (bulb)
        javafx.scene.shape.Sphere bulb = new javafx.scene.shape.Sphere(0.04);
        PhongMaterial bulbMat = new PhongMaterial(Color.WHITE);
        bulbMat.setSelfIlluminationMap(null); // High specular 
        bulbMat.setDiffuseColor(Color.WHITE);
        bulb.setMaterial(bulbMat);
        bulb.setTranslateY(-h/2 + shadeH/2);
        g.getChildren().add(bulb);
        
        return g;
    }

    private Group buildTableLamp(double w, double h, double d, Color c) {
        Group g = new Group();
        double baseH  = h * 0.20;
        double shadeH = h * 0.50;
        double poleH  = h - baseH - shadeH;

        // Base
        placeWithMat(g, w * 0.8, baseH, d * 0.8, silverMat(), 0, h/2 - baseH/2, 0);
        
        // Supporting neck
        placeWithMat(g, 0.02, poleH, 0.02, silverMat(), 0, -h/2 + shadeH + poleH/2, 0);
        
        // Shade
        place(g, w * 1.3, shadeH, d * 1.3, Color.web("#FFF9E5"), 0, -h/2 + shadeH/2, 0, 20);
        
        return g;
    }

    private Group buildCeilingLight(double w, double h, double d, Color c) {
        Group g = new Group();
        placeWithMat(g, w * 1.8, h, d * 1.8,
                     makeMat(Color.web("#FFFDE7"), Color.WHITE, 20), 0, 0, 0);
        placeWithMat(g, w * 1.2, h * 0.45, d * 1.2, metalMat(), 0, -h * 0.25, 0);
        return g;
    }

    // ── Decor ──────────────────────────────────────────────────────────────────

    private Group buildVase(double w, double h, double d, Color c) {
        Group g = new Group();
        double bodyH = h * 0.72;
        double neckH = h - bodyH;
        place(g, w, bodyH, d, c, 0, h/2 - bodyH/2, 0);
        place(g, w * 0.55, neckH, d * 0.55, c.brighter(), 0, -h/2 + neckH/2, 0);
        return g;
    }

    private Group buildPlant(double w, double h, double d, Color c) {
        Group g = new Group();
        double potH = h * 0.30;
        double folH = h - potH;
        Color potC  = Color.web("#7B4E2D");

        place(g, w, potH, d, potC, 0, h/2 - potH/2, 0);
        // Layered foliage for depth
        place(g, w * 1.3,  folH * 0.60, d * 1.3,  c,               0, h/2 - potH - folH * 0.30, 0);
        place(g, w,        folH * 0.52, d,         c.brighter(),    0, h/2 - potH - folH * 0.56, 0);
        place(g, w * 0.70, folH * 0.42, d * 0.70, c.brighter().brighter(), 0, -h/2 + folH * 0.20, 0);
        return g;
    }

    private Group buildMirror(double w, double h, double d, Color c) {
        Group g = new Group();
        double fT = 0.05;
        Color frameColor = Color.web("#BFAA8A"); // Champagne gold/silver frame
        
        // Outer frame (beveled look)
        place(g, w, h, d, frameColor.darker(), 0, 0, 0, 60);
        
        // Inner reflective surface
        PhongMaterial mirrorMat = new PhongMaterial(Color.rgb(220, 235, 250, 0.8));
        mirrorMat.setSpecularColor(Color.WHITE);
        mirrorMat.setSpecularPower(128);
        placeWithMat(g, w - fT*2, h - fT*2, d * 0.5, mirrorMat, 0, 0, -d/2 + d*0.2);
        
        // Subtle detail on frame corners
        place(g, fT, fT, d + 0.01, frameColor.brighter(), -w/2 + fT/2, -h/2 + fT/2, 0);
        place(g, fT, fT, d + 0.01, frameColor.brighter(),  w/2 - fT/2, -h/2 + fT/2, 0);
        
        return g;
    }

    private Group buildPainting(double w, double h, double d, Color c) {
        Group g = new Group();
        double fT    = 0.028;
        Color frame  = Color.web("#5D4037");

        place(g, w, h, d, frame, 0, 0, 0);
        place(g, w - fT*2, h - fT*2, d * 0.25, c, 0, 0, -d * 0.37);
        return g;
    }

    private Group buildGenericBox(double w, double h, double d, Color c) {
        Group g = new Group();
        place(g, w, h, d, c, 0, 0, 0);
        return g;
    }

    // ── Utility ────────────────────────────────────────────────────────────────

    private Color parseColor(String hex, String fallback) {
        try {
            if (hex != null && !hex.isEmpty()) return Color.web(hex);
        } catch (Exception ignored) {}
        return Color.web(fallback);
    }

    private PhongMaterial makeMat(Color diffuse, Color specular, double specPower) {
        PhongMaterial m = new PhongMaterial(diffuse);
        m.setSpecularColor(specular);
        m.setSpecularPower(specPower);
        return m;
    }

    // ── Selection ──────────────────────────────────────────────────────────────

    public FurnitureItem selectNode(Node clicked) {
        Group newSelectedGroup = null;
        Node n = clicked;
        while (n != null) {
            if (furnitureMap.containsKey(n)) { newSelectedGroup = (Group) n; break; }
            n = n.getParent();
        }

        if (selectedGroup != null) applyHighlight(selectedGroup, false);
        selectedGroup = newSelectedGroup;
        selectedData = (selectedGroup != null) ? furnitureMap.get(selectedGroup) : null;

        if (selectedGroup != null) {
            applyHighlight(selectedGroup, true);
        }

        if (onSelectionChanged != null) {
            onSelectionChanged.accept(selectedData);
        }
        return selectedData;
    }

    public void setSelectedItem(FurnitureItem item) {
        if (selectedGroup != null) applyHighlight(selectedGroup, false);
        
        selectedData = item;
        selectedGroup = null;
        
        if (item != null) {
            for (Map.Entry<Group, FurnitureItem> entry : furnitureMap.entrySet()) {
                if (entry.getValue() == item) {
                    selectedGroup = entry.getKey();
                    applyHighlight(selectedGroup, true);
                    break;
                }
            }
        }
        
        if (onSelectionChanged != null) {
            onSelectionChanged.accept(selectedData);
        }
    }

    public void clearSelection() {
        if (selectedGroup != null) applyHighlight(selectedGroup, false);
        selectedGroup = null;
        selectedData = null;
        if (onSelectionChanged != null) {
            onSelectionChanged.accept(null);
        }
    }

    private void applyHighlight(Group fg, boolean on) {
        fg.getChildren().stream().filter(c -> c instanceof Box).forEach(c -> {
            Box b = (Box) c;
            if (!(b.getMaterial() instanceof PhongMaterial)) return;
            PhongMaterial m = (PhongMaterial) b.getMaterial();
            if (on) {
                m.setSpecularColor(Color.web("#8B5CF6"));
                m.setSpecularPower(2);
                m.setSelfIlluminationMap(null); // Just use a strong specular/color
                b.setScaleX(1.05); b.setScaleY(1.05); b.setScaleZ(1.05);
            } else {
                m.setSpecularColor(Color.gray(0.28));
                m.setSpecularPower(22);
                b.setScaleX(1.0); b.setScaleY(1.0); b.setScaleZ(1.0);
            }
        });
    }

    // ── Public refresh API ─────────────────────────────────────────────────────

    public void updateFromRoom() {
        double w = room.getWidth();
        double h = room.getHeight();
        double l = room.getLength();

        if (centeringTranslate != null) {
            centeringTranslate.setX(-w / 2.0);
            centeringTranslate.setY(-h / 2.0);
            centeringTranslate.setZ(-l / 2.0);
        }

        if (floorBox != null) {
            floorBox.setWidth(w);
            floorBox.setHeight(0.08);
            floorBox.setDepth(l);
            floorBox.setTranslateX(w / 2);
            floorBox.setTranslateY(h);
            floorBox.setTranslateZ(l / 2);

            if (room.getFloorColor() != null) {
                Color fc = parseColor(room.getFloorColor(), "#6d573f");
                ((PhongMaterial) floorBox.getMaterial()).setDiffuseColor(fc);
                if (gridMaterial != null) gridMaterial.setDiffuseColor(fc.darker());
            }
        }

        if (backWallBox != null) {
            backWallBox.setWidth(w);
            backWallBox.setHeight(h);
            backWallBox.setTranslateX(w / 2);
            backWallBox.setTranslateY(h / 2);
            backWallBox.setTranslateZ(l);
        }

        if (leftWallBox != null) {
            leftWallBox.setHeight(h);
            leftWallBox.setDepth(l);
            leftWallBox.setTranslateY(h / 2);
            leftWallBox.setTranslateZ(l / 2);
        }

        if (rightWallBox != null) {
            rightWallBox.setHeight(h);
            rightWallBox.setDepth(l);
            rightWallBox.setTranslateX(w);
            rightWallBox.setTranslateY(h / 2);
            rightWallBox.setTranslateZ(l / 2);
        }

        if (backWallBox != null && room.getWallColor() != null) {
            PhongMaterial wm = wallMaterial(room.getWallColor());
            backWallBox.setMaterial(wm);
            leftWallBox.setMaterial(wm);
            rightWallBox.setMaterial(wm);
        }

        addFloorGrid(room.getFloorColor() != null ? room.getFloorColor() : "#6d573f");

        // Update camera position parameters and re-apply
        double span = Math.max(room.getWidth(), room.getLength());
        baseZ = -span * 1.5;   // slightly further back for a better overview
        double ceilingY = -(room.getHeight() / 2.0);
        baseY = ceilingY - 0.4;

        double floorY = room.getHeight() / 2.0;
        double tiltDeg = Math.toDegrees(Math.atan2(floorY - baseY, -baseZ));

        cameraTranslate.setX(0);
        cameraTranslate.setY(baseY);
        cameraTranslate.setZ(baseZ);
        if (cameraTiltRotate != null) {
            cameraTiltRotate.setAngle(-tiltDeg);
        }

        refreshFurniture();
        positionLights();
    }

    private void refreshFurniture() {
        roomGroup.getChildren().removeIf(furnitureMap::containsKey);
        furnitureMap.clear();

        selectedGroup = null; // Re-established by addFurniture loop if found
        if (room.getFurnitureItems() != null) {
            for (FurnitureItem item : room.getFurnitureItems()) {
                addFurniture(item);
                // Check if this newly added item was the one previously selected
                if (selectedData != null && selectedData == item) {
                    // Find the group we just added
                    for (Map.Entry<Group, FurnitureItem> entry : furnitureMap.entrySet()) {
                        if (entry.getValue() == item) {
                            selectedGroup = entry.getKey();
                            applyHighlight(selectedGroup, true);
                            break;
                        }
                    }
                }
            }
        }
    }

    // ── Camera controls ────────────────────────────────────────────────────────

    public void zoom(double delta) {
        double z = cameraTranslate.getZ() + delta;
        cameraTranslate.setZ(Math.max(baseZ * 4, Math.min(-1, z)));
    }

    public void pan(double dx, double dy) {
        cameraTranslate.setX(cameraTranslate.getX() + dx * 0.01);
        cameraTranslate.setY(cameraTranslate.getY() + dy * 0.01);
    }

    public void resetView() {
        rotateX.setAngle(0); rotateY.setAngle(-30);
        cameraTranslate.setX(0);
        cameraTranslate.setY(baseY);
        cameraTranslate.setZ(baseZ);
    }

    public void setBrightness(int pct) {
        room.setBrightness(pct);
        applyBrightness();
    }

    // ── Accessors ──────────────────────────────────────────────────────────────

    public PerspectiveCamera getCamera() { return camera; }
    public Rotate getRotateX()           { return rotateX; }
    public Rotate getRotateY()           { return rotateY; }
}
