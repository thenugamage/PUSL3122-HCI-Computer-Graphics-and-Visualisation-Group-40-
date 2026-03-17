package com.capitalcarrier.roomvisualizer.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void newRoom_hasCorrectDefaults() {
        Room room = new Room();
        
        assertEquals("New Design", room.getName());
        assertEquals("#8e9196", room.getWallColor());
        assertEquals("#6d573f", room.getFloorColor());
        assertEquals(50, room.getBrightness());
        assertEquals(82, room.getLightX());
        assertEquals(-600, room.getLightY());
        assertEquals(0, room.getLightZ());
        assertNotNull(room.getFurnitureItems());
        assertTrue(room.getFurnitureItems().isEmpty());
    }

    @Test
    void addFurnitureItem_increasesListSize() {
        Room room = new Room();
        FurnitureItem item = new FurnitureItem();
        item.setId("test-id");
        
        room.addFurnitureItem(item);
        
        assertEquals(1, room.getFurnitureItems().size());
        assertEquals("test-id", room.getFurnitureItems().get(0).getId());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Room room = new Room();
        
        room.setName("My Custom Room");
        room.setWidth(5);
        room.setLength(6);
        room.setHeight(3);
        room.setWallColor("#FFFFFF");
        room.setFloorColor("#000000");
        room.setBrightness(80);
        
        assertEquals("My Custom Room", room.getName());
        assertEquals(5, room.getWidth());
        assertEquals(6, room.getLength());
        assertEquals(3, room.getHeight());
        assertEquals("#FFFFFF", room.getWallColor());
        assertEquals("#000000", room.getFloorColor());
        assertEquals(80, room.getBrightness());
    }
}
