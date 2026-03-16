package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.scene.PerspectiveCamera;
import javafx.scene.AmbientLight;
import javafx.scene.PointLight;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class RoomViewport3DFX extends Group {
    private Room room;
    private Group roomGroup;
    private PerspectiveCamera camera;
    private Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(-30, Rotate.Y_AXIS);
    private double mousePosX, mousePosY;

    public RoomViewport3DFX(Room room) {
        this.room = room;
        roomGroup = new Group();
        getChildren().add(roomGroup);
        
        setupContent();
        setupLights();
    }

    private void setupContent() {
        double w = room.getWidth();
        double h = room.getHeight();
        double l = room.getLength();

        // Floor
        Box floor = new Box(w, 0.01, l);
        floor.setTranslateX(w / 2);
        floor.setTranslateZ(l / 2);
        PhongMaterial floorMat = new PhongMaterial();
        floorMat.setDiffuseColor(Color.web(room.getFloorColor() != null ? room.getFloorColor() : "#A08764"));
        floor.setMaterial(floorMat);
        roomGroup.getChildren().add(floor);

        // Back Wall
        Box backWall = new Box(w, h, 0.01);
        backWall.setTranslateX(w / 2);
        backWall.setTranslateY(h / 2);
        PhongMaterial wallMat = new PhongMaterial();
        wallMat.setDiffuseColor(Color.web(room.getWallColor() != null ? room.getWallColor() : "#DCD2BE"));
        backWall.setMaterial(wallMat);
        roomGroup.getChildren().add(backWall);

        // Left Wall
        Box leftWall = new Box(0.01, h, l);
        leftWall.setTranslateZ(l / 2);
        leftWall.setTranslateY(h / 2);
        leftWall.setMaterial(wallMat);
        roomGroup.getChildren().add(leftWall);

        // Ceiling
        Box ceiling = new Box(w, 0.01, l);
        ceiling.setTranslateX(w / 2);
        ceiling.setTranslateY(h);
        ceiling.setTranslateZ(l / 2);
        PhongMaterial ceilingMat = new PhongMaterial();
        ceilingMat.setDiffuseColor(Color.WHITE);
        ceiling.setMaterial(ceilingMat);
        roomGroup.getChildren().add(ceiling);

        // Furniture
        if (room.getFurnitureItems() != null) {
            for (FurnitureItem item : room.getFurnitureItems()) {
                addFurniture(item);
            }
        }
    }

    private void addFurniture(FurnitureItem item) {
        Group furnitureGroup = new Group();
        Box base = new Box(item.getWidth(), item.getHeight(), item.getDepth());
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(Color.web(item.getColor() != null ? item.getColor() : "#FFFFFF"));
        base.setMaterial(mat);
        
        furnitureGroup.getChildren().add(base);
        furnitureGroup.setTranslateX(item.getX() + item.getWidth() / 2);
        furnitureGroup.setTranslateY(item.getY() + item.getHeight() / 2);
        furnitureGroup.setTranslateZ(item.getZ() + item.getDepth() / 2);
        furnitureGroup.getTransforms().add(new Rotate(item.getRotation(), Rotate.Y_AXIS));
        
        roomGroup.getChildren().add(furnitureGroup);
    }

    private void setupLights() {
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(room.getLightX() / 100.0 * room.getWidth() + room.getWidth() / 2);
        light.setTranslateY(room.getLightY() / 100.0 * room.getHeight() + room.getHeight() / 2);
        light.setTranslateZ(room.getLightZ() / 100.0 * room.getLength() + room.getLength() / 2);
        getChildren().add(light);

        AmbientLight ambient = new AmbientLight(Color.web("#222222"));
        getChildren().add(ambient);
    }
}
