package com.capitalcarrier.roomvisualizer.presentation.components;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class RoomIllustrationBuilder {

    public static Node buildIllustration(String type, String wallColor, String floorColor) {
        StackPane container = new StackPane();
        container.setPrefSize(350, 200);
        
        Pane roomBox = new Pane();
        double w = 350;
        double h = 200;
        double inset = 40;
        
        // Ensure colors are not null
        String wColor = (wallColor != null) ? wallColor : "#E0D2C8";
        String fColor = (floorColor != null) ? floorColor : "#A08764";
        
        try { Color.web(wColor); } catch (Exception e) { wColor = "#E0D2C8"; }
        try { Color.web(fColor); } catch (Exception e) { fColor = "#A08764"; }

        // Floor
        Polygon floor = new Polygon(
            0, h,
            w, h,
            w - inset, h - inset,
            inset, h - inset
        );
        floor.setFill(Color.web(fColor));
        floor.setOpacity(0.8);
        
        // Back Wall
        Rectangle backWall = new Rectangle(inset, inset, w - 2 * inset, h - 2 * inset);
        backWall.setFill(Color.web(wColor));
        
        // Left Wall
        Polygon leftWall = new Polygon(
            0, 0,
            inset, inset,
            inset, h - inset,
            0, h
        );
        leftWall.setFill(Color.web(wColor));
        leftWall.setOpacity(0.6);
        
        // Right Wall
        Polygon rightWall = new Polygon(
            w, 0,
            w - inset, inset,
            w - inset, h - inset,
            w, h
        );
        rightWall.setFill(Color.web(wColor));
        rightWall.setOpacity(0.6);
        
        // Ceiling
        Polygon ceiling = new Polygon(
            0, 0,
            w, 0,
            w - inset, inset,
            inset, inset
        );
        ceiling.setFill(Color.web(wColor));
        ceiling.setOpacity(0.4);
        
        roomBox.getChildren().addAll(floor, backWall, leftWall, rightWall, ceiling);

        // Room-specific furniture based on keywords
        Group furniture = new Group();
        String t = (type != null) ? type.toLowerCase() : "";
        
        if (t.contains("living") || t.contains("lounge")) {
            Rectangle sofa = new Rectangle(60, 120, 160, 35);
            sofa.setFill(Color.web("#5E35B1"));
            sofa.setArcWidth(10); sofa.setArcHeight(10);
            
            Rectangle sofaBack = new Rectangle(60, 105, 160, 20);
            sofaBack.setFill(Color.web("#4527A0"));
            sofaBack.setArcWidth(10); sofaBack.setArcHeight(10);
            
            Rectangle pot = new Rectangle(240, 130, 12, 15);
            pot.setFill(Color.web("#5D4037"));
            Circle plant = new Circle(246, 120, 10);
            plant.setFill(Color.web("#2E7D32"));
            
            furniture.getChildren().addAll(sofaBack, sofa, pot, plant);
        } else if (t.contains("bed") || t.contains("sleep")) {
            Rectangle bedFrame = new Rectangle(100, 100, 150, 60);
            bedFrame.setFill(Color.web("#4527A0"));
            bedFrame.setArcWidth(10); bedFrame.setArcHeight(10);
            
            Rectangle sheet = new Rectangle(110, 110, 130, 50);
            sheet.setFill(Color.web("#BDBDBD"));
            
            Rectangle pillow1 = new Rectangle(120, 105, 40, 15);
            Rectangle pillow2 = new Rectangle(190, 105, 40, 15);
            pillow1.setFill(Color.web("#EEEEEE")); pillow2.setFill(Color.web("#EEEEEE"));
            
            Rectangle stand = new Rectangle(70, 130, 20, 20);
            stand.setFill(Color.web("#795548"));
            
            furniture.getChildren().addAll(bedFrame, sheet, pillow1, pillow2, stand);
        } else if (t.contains("office") || t.contains("study") || t.contains("work")) {
            Rectangle deskTop = new Rectangle(80, 120, 190, 10);
            deskTop.setFill(Color.web("#5D4037"));
            Rectangle leg1 = new Rectangle(90, 130, 4, 30);
            Rectangle leg2 = new Rectangle(256, 130, 4, 30);
            leg1.setFill(Color.web("#3E2723")); leg2.setFill(Color.web("#3E2723"));
            
            Rectangle monitor = new Rectangle(140, 85, 70, 40);
            monitor.setFill(Color.web("#263238"));
            Rectangle stand_m = new Rectangle(170, 125, 10, 5);
            stand_m.setFill(Color.web("#455A64"));
            
            VBox shelf = new VBox(2);
            shelf.setLayoutX(280); shelf.setLayoutY(70);
            for (String c : new String[]{"#E53935", "#43A047", "#1E88E5", "#FDD835", "#8E24AA"}) {
                Rectangle r = new Rectangle(15, 8); r.setFill(Color.web(c));
                shelf.getChildren().add(r);
            }
            
            furniture.getChildren().addAll(leg1, leg2, deskTop, monitor, stand_m, shelf);
        } else if (t.contains("dining") || t.contains("breakfast")) {
            Ellipse table = new Ellipse(w/2, 140, 80, 40);
            table.setFill(Color.web("#795548"));
            
            for (int i=0; i<4; i++) {
                double angle = i * Math.PI / 2 + Math.PI/4;
                Circle chair = new Circle(w/2 + Math.cos(angle)*70, 140 + Math.sin(angle)*30, 12);
                chair.setFill(Color.web("#C62828"));
                furniture.getChildren().add(chair);
            }
            furniture.getChildren().add(table);
        } else if (t.contains("kitchen") || t.contains("cook")) {
            HBox cabinets = new HBox(2);
            cabinets.setLayoutX(80); cabinets.setLayoutY(140);
            for(int i=0; i<4; i++) {
                Rectangle c = new Rectangle(40, 40); c.setFill(Color.web("#8D6E63"));
                c.setStroke(Color.web("#5D4037"));
                cabinets.getChildren().add(c);
            }
            Rectangle counter = new Rectangle(inset, 135, w-2*inset, 5);
            counter.setFill(Color.web("#EEEEEE"));
            
            Rectangle upper = new Rectangle(inset, inset, w-2*inset, 60);
            upper.setFill(Color.web("#A1887F"));
            
            furniture.getChildren().addAll(upper, counter, cabinets);
        } else if (t.contains("bath") || t.contains("wash") || t.contains("toilet")) {
            Rectangle tub = new Rectangle(100, 130, 180, 40);
            tub.setFill(Color.web("#CFD8DC"));
            tub.setArcWidth(40); tub.setArcHeight(40);
            
            Rectangle water = new Rectangle(110, 135, 150, 20);
            water.setFill(Color.web("#81D4FA", 0.6));
            water.setArcWidth(30); water.setArcHeight(30);
            
            furniture.getChildren().addAll(tub, water);
        }
        
        container.getChildren().addAll(roomBox, furniture);
        return container;
    }
}
