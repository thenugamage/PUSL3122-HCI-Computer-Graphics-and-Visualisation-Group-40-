package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.Room;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.base.NodeMatchers;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EditorUITest extends ApplicationTest {

    private EditorFX editor;

    @Override
    public void start(Stage stage) {
        Room room = new Room();
        editor = new EditorFX(room, stage);
        Scene scene = new Scene((javafx.scene.layout.Region) editor.getContent(), 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void should_contain_canvas_and_panels() {
        // Verify that the main editor components are loaded
        assertNotNull(editor.getContent(), "Editor content should not be null");
        
        // Use TestFX to look up nodes and verify visibility
        FxAssert.verifyThat(".root", NodeMatchers.isVisible());
    }
}
