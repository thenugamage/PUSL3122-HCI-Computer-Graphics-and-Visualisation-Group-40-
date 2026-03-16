package com.capitalcarrier.roomvisualizer.presentation.dashboard;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.config.ThemeConfig;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SettingsViewFX extends VBox {

    public SettingsViewFX() {
        setSpacing(0);
        setPadding(new Insets(20, 60, 40, 60));
        setStyle("-fx-background-color: transparent;");

        VBox header = new VBox(8);
        header.setPadding(new Insets(0, 0, 30, 0));
        Text title = new Text("Settings");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);
        Text sub = new Text("Manage your account preferences and editor configuration");
        sub.setFont(Font.font("Inter", 14));
        sub.setFill(ThemeConfig.TXT_SECONDARY);
        header.getChildren().addAll(title, sub);
        getChildren().add(header);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        
        VBox content = new VBox(32);
        content.setPadding(new Insets(0, 0, 40, 0));
        content.setStyle("-fx-background-color: transparent;");

        content.getChildren().addAll(
            buildAccountSection(),
            buildEditorSection(),
            buildDataSection(),
            buildFooter()
        );

        scroll.setContent(content);
        getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private VBox buildAccountSection() {
        User user = AuthService.getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "user@example.com";
        String userName = user != null ? user.getUsername() : "Username";

        VBox card = createGlassCard("Account Settings", "M12,4c2.21,0,4,1.79,4,4s-1.79,4-4,4s-4-1.79-4-4S9.79,4,12,4z M12,14c4.42,0,8,1.79,8,4v2H4v-2C4,15.79,7.58,14,12,14z");
        VBox rows = new VBox(20);
        
        rows.getChildren().addAll(
            createRow("Email Address", userEmail, "Update", ThemeConfig.BRAND_PURPLE),
            createDivider(),
            createRow("Password", "••••••••••••", "Change", ThemeConfig.BRAND_PURPLE),
            createDivider(),
            createRow("Username", userName, null, null)
        );

        card.getChildren().add(rows);
        return card;
    }

    private VBox buildEditorSection() {
        VBox card = createGlassCard("Editor Preferences", "M3,17h18v2H3V17z M4,15v-3h16v3H4z M3,10h18v2H3V10z M4,8V5h16v3H4z");
        VBox rows = new VBox(24);

        rows.getChildren().addAll(
            createControlRow("Measurement Units", "Meters (m)", "Feet (ft)", "Inches (in)"),
            createDivider(),
            createControlRow("Default Grid View", "Show Grid", "Hide Grid"),
            createDivider(),
            createToggleRow("Enable Auto-Save", "Automatically save changes every 5 minutes", true)
        );

        card.getChildren().add(rows);
        return card;
    }

    private VBox buildDataSection() {
        VBox card = createGlassCard("Data & Privacy", "M12,12c2.21,0,4-1.79,4-4s-1.79-4-4-4S8,5.79,8,8S9.79,12,12,12z M12,14c-2.67,0-8,1.34-8,4v2h16v-2C20,15.34,14.67,14,12,14z");
        VBox rows = new VBox(20);

        rows.getChildren().addAll(
            createRow("Export Library", "Download all your room designs as a portable file", "Export ZIP", ThemeConfig.CYAN),
            createDivider(),
            createRow("Danger Zone", "Delete your account and all associated data permanently", "Delete Data", Color.web("#f43f5e"))
        );

        card.getChildren().add(rows);
        return card;
    }

    private VBox createGlassCard(String title, String svg) {
        VBox card = new VBox(24);
        card.setPadding(new Insets(28, 32, 28, 32));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.04); -fx-background-radius: 24; -fx-border-color: rgba(255, 255, 255, 0.08); -fx-border-radius: 24; -fx-border-width: 1;");
        card.setMaxWidth(900);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        SVGPath icon = new SVGPath();
        icon.setContent(svg);
        icon.setFill(ThemeConfig.BRAND_PURPLE);
        icon.setScaleX(1.1); icon.setScaleY(1.1);
        
        Text titleLabel = new Text(title);
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        titleLabel.setFill(Color.WHITE);
        
        header.getChildren().addAll(icon, titleLabel);
        card.getChildren().add(header);
        return card;
    }

    private HBox createRow(String labelText, String value, String actionText, Color color) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        VBox info = new VBox(4);
        Text lbl = new Text(labelText);
        lbl.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        lbl.setFill(ThemeConfig.TXT_SECONDARY);
        
        Text val = new Text(value);
        val.setFont(Font.font("Inter", 15));
        val.setFill(Color.WHITE);
        
        info.getChildren().addAll(lbl, val);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        row.getChildren().add(info);
        
        if (actionText != null) {
            Button btn = new Button(actionText);
            btn.setStyle(String.format("-fx-background-color: rgba(%d,%d,%d,0.1); -fx-text-fill: #%02X%02X%02X; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 18; -fx-cursor: hand;",
                (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255),
                (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255)));
            row.getChildren().add(btn);
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
        combo.setStyle("-fx-background-color: #1d2440; -fx-text-fill: white; -fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.05); -fx-border-radius: 8;");
        combo.setPrefWidth(160);
        
        row.getChildren().addAll(label, combo);
        return row;
    }

    private HBox createToggleRow(String title, String subtitle, boolean selected) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        VBox textPart = new VBox(4);
        Text t = new Text(title);
        t.setFont(Font.font("Inter", FontWeight.BOLD, 15));
        t.setFill(Color.WHITE);
        Text s = new Text(subtitle);
        s.setFont(Font.font("Inter", 12));
        s.setFill(ThemeConfig.TXT_SECONDARY);
        textPart.getChildren().addAll(t, s);
        HBox.setHgrow(textPart, Priority.ALWAYS);
        
        CheckBox cb = new CheckBox();
        cb.setSelected(selected);
        cb.setStyle("-fx-mark-color: white; -fx-box-color: #ca4bf6;");
        
        row.getChildren().addAll(textPart, cb);
        return row;
    }

    private Separator createDivider() {
        Separator s = new Separator();
        s.setStyle("-fx-opacity: 0.05;");
        return s;
    }

    private VBox buildFooter() {
        VBox footer = new VBox(12);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(40, 0, 0, 0));
        
        Text copy = new Text("Furniture Room Visualizer v1.2.0");
        copy.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        copy.setFill(ThemeConfig.TXT_SECONDARY);
        
        Text rights = new Text("© 2026 Advanced Computer Graphics Group. All rights reserved.");
        rights.setFont(Font.font("Inter", 11));
        rights.setFill(ThemeConfig.TXT_SECONDARY.deriveColor(0, 1, 1, 0.4));
        
        footer.getChildren().addAll(copy, rights);
        return footer;
    }
}
