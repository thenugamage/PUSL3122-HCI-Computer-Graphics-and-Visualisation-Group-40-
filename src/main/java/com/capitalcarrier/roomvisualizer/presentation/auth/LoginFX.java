package com.capitalcarrier.roomvisualizer.presentation.auth;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginFX {

    public void start(Stage stage) {
        stage.setTitle("Furniture Room Design Visualizers - Login");

        StackPane root = new StackPane();
        root.setPrefSize(1400, 1000);
        root.setStyle("-fx-background-color: #091C61;");

        try {
            root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load style.css: " + e.getMessage());
        }

        setupImageBackground(root);

        VBox contentWrapper = new VBox(30);
        contentWrapper.setAlignment(Pos.TOP_CENTER);
        contentWrapper.setPadding(new Insets(64, 24, 40, 24));

        VBox titleBox = new VBox(8);
        titleBox.setAlignment(Pos.CENTER);

        Text title = new Text("Furniture Room Design Visualizers");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 48));
        title.setFill(Color.WHITE);

        DropShadow titleShadow = new DropShadow();
        titleShadow.setRadius(8);
        titleShadow.setOffsetX(3);
        titleShadow.setOffsetY(6);
        titleShadow.setColor(Color.color(0, 0, 0, 0.35));
        title.setEffect(titleShadow);

        Text subTitle = new Text("Professional Interior Room Designer");
        subTitle.setFont(Font.font("Inter", 18));
        subTitle.setFill(Color.web("#FFFFFF", 0.72));

        titleBox.getChildren().addAll(title, subTitle);

        VBox card = new VBox(12);
        card.getStyleClass().add("auth-card");
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.18), 32, 0, 0, 10);"
        );
        card.setPrefWidth(500);
        card.setMaxWidth(500);
        card.setPadding(new Insets(28, 44, 24, 44));
        card.setAlignment(Pos.TOP_LEFT);

        Text cardHeader = new Text("Sign in Account");
        cardHeader.setFont(Font.font("Inter", FontWeight.MEDIUM, 32));
        cardHeader.setFill(Color.web("#2f3a58"));

        Text cardSubHeader = new Text("Sign in to start shopping");
        cardSubHeader.setFont(Font.font("Inter", 16));
        cardSubHeader.setFill(Color.web("#7b8699"));

        VBox usernameBox = createInputWithIcon(
            "Username",
            "Enter your username",
            "M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8",
            false
        );
        TextField usernameField = (TextField) ((StackPane) usernameBox.getChildren().get(1)).getChildren().get(0);

        VBox passwordBox = createInputWithIcon(
            "Password",
            "Password",
            "M12 15c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm6-6h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v3H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V11c0-1.1-.9-2-2-2zM9 9V6c0-1.66 1.34-3 3-3s3 1.34 3 3v3H9z",
            true
        );
        PasswordField passwordField = (PasswordField) ((StackPane) passwordBox.getChildren().get(1)).getChildren().get(0);

        Button signInBtn = new Button("Sign in Account");
        signInBtn.setMaxWidth(Double.MAX_VALUE);
        signInBtn.setPrefHeight(50);
        signInBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #ca4bf6, #844cf5);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 18;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        );
        signInBtn.setOnAction(e -> {
            try {
                AuthService.loginLocalUser(usernameField.getText(), passwordField.getText());
                openDashboard(stage);
            } catch (Exception ex) {
                showAlert("Login Error", ex.getMessage());
            }
        });

        VBox.setMargin(signInBtn, new Insets(10, 0, 18, 0));

        VBox divider = createDivider("or continue with");

        Text googleIcon = new Text("G");
        googleIcon.setFill(Color.web("#4285F4"));
        googleIcon.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Text googleText = new Text("Sign in with Google");
        googleText.setFont(Font.font("Inter", 16));
        googleText.setFill(Color.web("#576074"));

        javafx.scene.layout.HBox googleContent = new javafx.scene.layout.HBox(12, googleIcon, googleText);
        googleContent.setAlignment(Pos.CENTER);

        Button googleBtn = new Button();
        googleBtn.setGraphic(googleContent);
        googleBtn.setMaxWidth(Double.MAX_VALUE);
        googleBtn.setPrefHeight(50);
        googleBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #d9deea;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        );

        javafx.scene.layout.HBox bottomBox = new javafx.scene.layout.HBox(20);
        bottomBox.setAlignment(Pos.CENTER);

        Text noAccountText = new Text("Already have an account?");
        noAccountText.setFill(Color.web("#6f7b8f"));
        noAccountText.setFont(Font.font("Inter", 14));
        noAccountText.setTextAlignment(TextAlignment.CENTER);

        Hyperlink createAccountLink = new Hyperlink("Create Account");
        createAccountLink.setStyle(
            "-fx-text-fill: #8B5CF6;" +
            "-fx-font-weight: 500;" +
            "-fx-underline: false;" +
            "-fx-padding: 0;" +
            "-fx-font-size: 14;"
        );
        createAccountLink.setOnAction(e -> new RegisterFX().start(stage));

        bottomBox.getChildren().addAll(noAccountText, createAccountLink);
        VBox.setMargin(bottomBox, new Insets(14, 0, 0, 0));

        card.getChildren().addAll(cardHeader, cardSubHeader, usernameBox, passwordBox, signInBtn, divider, googleBtn, bottomBox);

        contentWrapper.getChildren().addAll(titleBox, card);
        root.getChildren().add(contentWrapper);

        Scene scene = new Scene(root, 1400, 1000);
        stage.setScene(scene);
        stage.show();
    }

    private void setupImageBackground(StackPane root) {
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/auth_bg.png"));
            ImageView bgView = new ImageView(bgImage);
            
            // Bind size to root to ensure it covers the entire area
            bgView.fitWidthProperty().bind(root.widthProperty());
            bgView.fitHeightProperty().bind(root.heightProperty());
            bgView.setPreserveRatio(false);
            
            root.getChildren().add(0, bgView);
        } catch (Exception e) {
            System.err.println("Could not load background image: " + e.getMessage());
            // Fallback to solid color if image fails
            root.setStyle("-fx-background-color: #091C61;");
        }
    }

    private VBox createInputWithIcon(String labelStr, String prompt, String svgPath, boolean isPassword) {
        VBox box = new VBox(6);
        Label label = new Label(labelStr);
        label.setStyle("-fx-font-weight: 500; -fx-text-fill: #4a556a; -fx-font-size: 14;");

        StackPane inputStack = new StackPane();
        TextInputControl field = isPassword ? new PasswordField() : new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(48);
        field.setStyle(
            "-fx-padding: 0 15 0 45;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: #edf0f6;" +
            "-fx-background-color: #f5f5f7;" +
            "-fx-font-size: 16;" +
            "-fx-text-fill: #465266;" +
            "-fx-prompt-text-fill: #8f949d;"
        );

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setStroke(Color.web("#9aa3b1"));
        icon.setStrokeWidth(1.6);
        icon.setFill(Color.TRANSPARENT);
        icon.setScaleX(0.85);
        icon.setScaleY(0.85);

        StackPane iconWrapper = new StackPane(icon);
        iconWrapper.setAlignment(Pos.CENTER_LEFT);
        iconWrapper.setPadding(new Insets(0, 0, 0, 15));
        iconWrapper.setMouseTransparent(true);

        inputStack.getChildren().addAll(field, iconWrapper);
        box.getChildren().addAll(label, inputStack);
        return box;
    }

    private VBox createDivider(String text) {
        VBox box = new VBox();
        javafx.scene.layout.HBox inner = new javafx.scene.layout.HBox(16);
        inner.setAlignment(Pos.CENTER);

        Line l1 = new Line(0, 0, 120, 0);
        l1.setStroke(Color.web("#d8deea"));

        Text t = new Text(text);
        t.setFill(Color.web("#808ba0"));
        t.setFont(Font.font("Inter", 14));

        Line l2 = new Line(0, 0, 120, 0);
        l2.setStroke(Color.web("#d8deea"));

        inner.getChildren().addAll(l1, t, l2);
        box.getChildren().add(inner);
        return box;
    }

    private void openDashboard(Stage stage) {
        DashboardFX dashboard = new DashboardFX();
        dashboard.start(stage);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
