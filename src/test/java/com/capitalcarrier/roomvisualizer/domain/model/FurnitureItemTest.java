package com.capitalcarrier.roomvisualizer.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FurnitureItemTest {

    @Test
    void getScale_whenPositive_returnsActualScale() {
        FurnitureItem item = new FurnitureItem();
        item.setScale(1.5);
        assertEquals(1.5, item.getScale());
    }

    @Test
    void getScale_whenZeroOrNegative_returnsOne() {
        FurnitureItem item = new FurnitureItem();
        
        item.setScale(0);
        assertEquals(1.0, item.getScale());
        
        item.setScale(-0.5);
        assertEquals(1.0, item.getScale());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        FurnitureItem item = new FurnitureItem();
        
        item.setId("item-1");
        item.setName("Sofa");
        item.setType("Seating");
        item.setX(10);
        item.setY(20);
        item.setZ(30);
        item.setRotation(90);
        item.setWidth(2);
        item.setHeight(1);
        item.setDepth(1.5);
        item.setColor("#FFFFFF");
        
        assertEquals("item-1", item.getId());
        assertEquals("Sofa", item.getName());
        assertEquals("Seating", item.getType());
        assertEquals(10, item.getX());
        assertEquals(20, item.getY());
        assertEquals(30, item.getZ());
        assertEquals(90, item.getRotation());
        assertEquals(2, item.getWidth());
        assertEquals(1, item.getHeight());
        assertEquals(1.5, item.getDepth());
        assertEquals("#FFFFFF", item.getColor());
    }
}
