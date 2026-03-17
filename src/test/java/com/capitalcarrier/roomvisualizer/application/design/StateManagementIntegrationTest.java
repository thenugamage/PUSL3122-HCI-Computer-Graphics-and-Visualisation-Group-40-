package com.capitalcarrier.roomvisualizer.application.design;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StateManagementIntegrationTest {

    @Test
    void testRoomAndFurnitureStateIntegration() {
        // Feature interaction verification
        Room room = new Room();
        room.setName("Integration Test Room");
        room.setWidth(10);
        room.setLength(12);
        
        FurnitureItem table = new FurnitureItem();
        table.setId("table-1");
        table.setType("Table");
        table.setName("Dining Table");
        table.setX(5);
        table.setZ(5);
        table.setScale(1.2);
        
        FurnitureItem chair = new FurnitureItem();
        chair.setId("chair-1");
        chair.setType("Seating");
        chair.setName("Dining Chair");
        chair.setX(5);
        chair.setZ(4);
        chair.setRotation(180);
        
        room.addFurnitureItem(table);
        room.addFurnitureItem(chair);
        
        // Assert full tree structure and interaction
        assertNotNull(room.getFurnitureItems());
        assertEquals(2, room.getFurnitureItems().size());
        
        assertEquals("Dining Table", room.getFurnitureItems().get(0).getName());
        assertEquals(1.2, room.getFurnitureItems().get(0).getScale());
        
        assertEquals("Dining Chair", room.getFurnitureItems().get(1).getName());
        assertEquals(180, room.getFurnitureItems().get(1).getRotation());
    }
}
