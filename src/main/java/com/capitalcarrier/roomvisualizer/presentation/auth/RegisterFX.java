package com.capitalcarrier.roomvisualizer.presentation.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import com.capitalcarrier.roomvisualizer.application.auth.AuthService;

public class RegisterFX {

    public void start(Stage stage) {
        stage.setTitle("Furniture Room Design Visualizers - Create Account");

        StackPane root = new StackPane();
        root.setPrefSize(1200, 800);
        // Updated Navy: #091C61
        root.setStyle("-fx-background-color: #091C61;"); 

        // Load global CSS
        try {
            root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load style.css: " + e.getMessage());
        }

        // Background - Same curved split
        drawPreciseBackground(root);

        // Content Wrapper
        VBox contentWrapper = new VBox(35);
        contentWrapper.setAlignment(Pos.CENTER);
        
        // External Title
        VBox titleBox = new VBox(12);
        titleBox.setAlignment(Pos.CENTER);
        
        Text title = new Text("Furniture Room Design Visualizers");
        title.getStyleClass().add("title-text");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 52));
        title.setFill(Color.WHITE);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setRadius(20);
        titleShadow.setOffsetX(0);
        titleShadow.setOffsetY(8);
        titleShadow.setColor(Color.color(0, 0, 0, 0.35));
        title.setEffect(titleShadow);

        Text subTitle = new Text("Professional Interior Room Designer");
        subTitle.getStyleClass().add("sub-title-text");
        subTitle.setFont(Font.font("Inter", 20));
        subTitle.setFill(Color.web("white", 0.9));
        titleBox.getChildren().addAll(title, subTitle);

        // Floating Card
        VBox card = new VBox(20);
        card.getStyleClass().add("auth-card");
        card.setMaxWidth(500);
        card.setPadding(new Insets(40, 55, 40, 55));
        card.setAlignment(Pos.CENTER_LEFT);

        // Header
        Text cardHeader = new Text("Create Account");
        cardHeader.setFont(Font.font("Inter", FontWeight.BOLD, 26));
        cardHeader.setFill(Color.web("#1e293b"));

        Text cardSubHeader = new Text("Sign up to start shopping");
        cardSubHeader.setFont(Font.font("Inter", 15));
        cardSubHeader.setFill(Color.web("#64748b"));

        VBox emailBox = createInputWithIcon("Email Address", "Enter your email", 
            "M3 8L10.89 13.26C11.57 13.71 12.43 13.71 13.11 13.26L21 8M5 19H19C20.1 19 21 18.1 21 17V7C21 5.9 20.1 5 19 5H5C3.9 5 3 5.9 3 7V17C3 18.1 3.9 19 5 19Z", false);
        TextField emailField = (TextField) ((StackPane) emailBox.getChildren().get(1)).getChildren().get(0);

        VBox usernameBox = createInputWithIcon("Username", "Choose a username", 
            "M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8", false);
        TextField usernameField = (TextField) ((StackPane) usernameBox.getChildren().get(1)).getChildren().get(0);
        
        VBox passwordBox = createInputWithIcon("Password", "Create a password", 
            "M12 15c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm6-6h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v3H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V11c0-1.1-.9-2-2-2zM9 9V6c0-1.66 1.34-3 3-3s3 1.34 3 3v3H9z", true);
        PasswordField passwordField = (PasswordField) ((StackPane) passwordBox.getChildren().get(1)).getChildren().get(0);

        // Register Button
        Button registerBtn = new Button("Create Account");
        registerBtn.getStyleClass().add("primary-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(54);
        registerBtn.setStyle("-fx-background-color: linear-gradient(to right, #8B5CF6, #a855f7); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 14; -fx-cursor: hand;");
        registerBtn.setOnAction(e -> {
            try {
                AuthService.registerLocalUser(
                    usernameField.getText(),
                    usernameField.getText(),
                    emailField.getText(),
                    passwordField.getText()
                );
                showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);
                new LoginFX().start(stage);
            } catch (Exception ex) {
                showAlert("Registration Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Divider
        HBox divider = createDivider("or continue with");

        // Google Button
        HBox googleBtnContent = new HBox(12);
        googleBtnContent.setAlignment(Pos.CENTER);
        SVGPath googleIcon = new SVGPath();
        googleIcon.setContent("M12.48 10.92v3.28h7.84c-.24 1.84-.901 3.251-1.921 4.261-1.12 1.13-2.88 2.37-5.919 2.37-4.72 0-8.58-3.86-8.58-8.58s3.86-8.58 8.58-8.58c2.56 0 4.48.99 5.86 2.32L21.09 3.5C19.23 1.77 16.5 0 12.48 0 5.58 0 0 5.58 0 12.48s5.58 12.48 12.48 12.48c3.75 0 6.58-1.24 8.78-3.5 2.15-2.21 2.82-5.3 2.82-7.85 0-.54-.04-1.06-.11-1.57h-11.49v0z");
        googleIcon.setFill(Color.web("#4285F4"));
        googleIcon.setScaleX(0.7);
        googleIcon.setScaleY(0.7);
        Text googleText = new Text("Sign up with Google");
        googleText.setStyle("-fx-font-weight: bold; -fx-fill: #334155; -fx-font-size: 15;");
        googleBtnContent.getChildren().addAll(googleIcon, googleText);

        Button googleBtn = new Button();
        googleBtn.getStyleClass().add("secondary-button");
        googleBtn.setGraphic(googleBtnContent);
        googleBtn.setMaxWidth(Double.MAX_VALUE);
        googleBtn.setPrefHeight(54);
        googleBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e2e8f0; -fx-border-radius: 14; -fx-background-radius: 14; -fx-cursor: hand;");

        // Bottom Link
        HBox bottomBox = new HBox(8);
        bottomBox.setAlignment(Pos.CENTER);
        Text hasAccountText = new Text("Already have an account?");
        hasAccountText.setFill(Color.web("#64748b"));
        hasAccountText.setFont(Font.font(15));
        Hyperlink loginLink = new Hyperlink("Sign In");
        loginLink.setStyle("-fx-text-fill: #8B5CF6; -fx-font-weight: bold; -fx-underline: false; -fx-padding: 0; -fx-font-size: 15;");
        loginLink.setOnAction(e -> {
            new LoginFX().start(stage);
        });
        bottomBox.getChildren().addAll(hasAccountText, loginLink);

        card.getChildren().addAll(cardHeader, cardSubHeader, usernameBox, emailBox, passwordBox, registerBtn, divider, googleBtn, bottomBox);
        
        contentWrapper.getChildren().addAll(titleBox, card);
        root.getChildren().add(contentWrapper);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.show();
    }

    private void drawPreciseBackground(StackPane root) {
        // Shifting circles further left for shallow arc
        Circle purpleCircle = new Circle(1200);
        purpleCircle.setFill(Color.web("#8B5CF6"));
        
        purpleCircle.setTranslateX(-680); 
        purpleCircle.setTranslateY(0);
        
        DropShadow ds = new DropShadow();
        ds.setRadius(60);
        ds.setOffsetX(15);
        ds.setColor(Color.color(0, 0, 0, 0.15));
        purpleCircle.setEffect(ds);

        root.getChildren().add(0, purpleCircle);
        StackPane.setAlignment(purpleCircle, Pos.CENTER_LEFT);
    }

    private VBox createInputWithIcon(String labelStr, String prompt, String svgPath, boolean isPassword) {
        VBox box = new VBox(10);
        Label label = new Label(labelStr);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 14;");
        
        StackPane inputStack = new StackPane();
        TextInputControl field = isPassword ? new PasswordField() : new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(52);
        field.setStyle("-fx-padding: 0 15 0 52; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #f1f5f9; -fx-background-color: #f8fafc; -fx-font-size: 14;");
        
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setStroke(Color.web("#94a3b8"));
        icon.setStrokeWidth(1.5);
        icon.setFill(Color.TRANSPARENT); 
        
        StackPane iconWrapper = new StackPane(icon);
        iconWrapper.setAlignment(Pos.CENTER_LEFT);
        iconWrapper.setPadding(new Insets(0, 0, 0, 18));
        iconWrapper.setMouseTransparent(true);
        
        inputStack.getChildren().addAll(field, iconWrapper);
        box.getChildren().addAll(label, inputStack);
        return box;
    }

    private HBox createDivider(String text) {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);
        Line l1 = new Line(0, 0, 110, 0);
        l1.setStroke(Color.web("#e2e8f0"));
        Text t = new Text(text);
        t.setFill(Color.web("#94a3b8"));
        t.setFont(Font.font(13));
        Line l2 = new Line(0, 0, 110, 0);
        l2.setStroke(Color.web("#e2e8f0"));
        box.getChildren().addAll(l1, t, l2);
        return box;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
