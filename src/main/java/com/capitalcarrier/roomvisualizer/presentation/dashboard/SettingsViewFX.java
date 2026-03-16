package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.config.ThemeConfig;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SettingsViewFX extends VBox {

    public SettingsViewFX() {
        setSpacing(30);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: transparent;");

        // Header
        Text title = new Text("Settings");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 36));
        title.setFill(Color.WHITE);
        getChildren().add(title);

        // Content Scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        
        VBox content = new VBox(30);
        content.setPadding(new Insets(0, 0, 40, 0));
        content.setStyle("-fx-background-color: transparent;");

        content.getChildren().addAll(
            buildAccountSection(),
            buildEditorSection(),
            buildDataSection(),
            buildFooter()
        );

        scrollPane.setContent(content);
        getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    private VBox buildAccountSection() {
        User user = AuthService.getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "user@example.com";
        String userName = user != null ? user.getUsername() : "Username";

        VBox card = createGlassCard("Account Settings");
        VBox rows = new VBox(15);
        
        rows.getChildren().addAll(
            createRow("Email Address", userEmail, "Change Email", ThemeConfig.BRAND_PURPLE),
            createDivider(),
            createRow("Password", "••••••••••••", "Change Password", ThemeConfig.BRAND_PURPLE),
            createDivider(),
            createRow("Username", userName, null, null)
        );

        card.getChildren().add(rows);
        return card;
    }

    private VBox buildEditorSection() {
        VBox card = createGlassCard("Editor Preferences");
        VBox rows = new VBox(20);

        rows.getChildren().addAll(
            createControlRow("Measurement Units", "Meters (m)", "Feet (ft)", "Centimeters (cm)"),
            createDivider(),
            createControlRow("Grid Spacing", "0.1m", "0.5m", "1.0m"),
            createDivider(),
            createToggleRow("Auto-save Designs", true)
        );

        card.getChildren().add(rows);
        return card;
    }

    private VBox buildDataSection() {
        VBox card = createGlassCard("Data Management");
        VBox rows = new VBox(20);

        rows.getChildren().addAll(
            createRow("Export Data", "Download a local backup of all designs.", "Download ZIP", ThemeConfig.CYAN),
            createDivider(),
            createRow("Clear All Data", "Permanently delete all saved designs.", "Delete All", Color.valueOf("#ff4646"))
        );

        card.getChildren().add(rows);
        return card;
    }

    private VBox createGlassCard(String title) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30, 35, 30, 35));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                     "-fx-background-radius: 28; " +
                     "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                     "-fx-border-radius: 28; " +
                     "-fx-border-width: 1.2;");
        card.setMaxWidth(1000);

        Text titleLabel = new Text(title);
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        titleLabel.setFill(Color.WHITE);
        
        card.getChildren().add(titleLabel);
        return card;
    }

    private HBox createRow(String labelText, String value, String actionText, Color color) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        VBox info = new VBox(4);
        Text lbl = new Text(labelText);
        lbl.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        lbl.setFill(ThemeConfig.TXT_SECONDARY);
        
        Text val = new Text(value);
        val.setFont(Font.font("Inter", 16));
        val.setFill(Color.WHITE);
        
        info.getChildren().addAll(lbl, val);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        row.getChildren().add(info);
        
        if (actionText != null) {
            Button actionBtn = new Button(actionText);
            String hex = String.format("#%02x%02x%02x", 
                (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
            actionBtn.setStyle("-fx-background-color: " + hex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
            row.getChildren().add(actionBtn);
        }
        
        return row;
    }

    private HBox createControlRow(String labelText, String... options) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        Text label = new Text(labelText);
        label.setFont(Font.font("Inter", FontWeight.BOLD, 15));
        label.setFill(Color.WHITE);
        HBox.setHgrow(label, Priority.ALWAYS);
        
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(options);
        combo.getSelectionModel().selectFirst();
        combo.setStyle("-fx-background-color: #0c1222; -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 8;");
        combo.setPrefWidth(180);
        
        row.getChildren().addAll(label, combo);
        return row;
    }

    private HBox createToggleRow(String labelText, boolean selected) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        Text label = new Text(labelText);
        label.setFont(Font.font("Inter", 15));
        label.setFill(Color.WHITE);
        HBox.setHgrow(label, Priority.ALWAYS);
        
        CheckBox cb = new CheckBox();
        cb.setSelected(selected);
        
        row.getChildren().addAll(label, cb);
        return row;
    }

    private Separator createDivider() {
        Separator s = new Separator();
        s.setStyle("-fx-opacity: 0.1;");
        return s;
    }

    private VBox buildFooter() {
        VBox footer = new VBox(8);
        footer.setAlignment(Pos.CENTER);
        
        Text copy = new Text("Furniture Room Visualizer v1.0.0");
        copy.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        copy.setFill(ThemeConfig.TXT_SECONDARY);
        
        Text rights = new Text("© 2026 Professional Interior Design Suite. All rights reserved.");
        rights.setFont(Font.font("Inter", 12));
        rights.setFill(ThemeConfig.TXT_SECONDARY.deriveColor(0, 1, 1, 0.6));
        
        footer.getChildren().addAll(copy, rights);
        return footer;
    }
}
