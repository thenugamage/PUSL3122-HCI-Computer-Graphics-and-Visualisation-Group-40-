package com.capitalcarrier.roomvisualizer.presentation.auth;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class RegisterFX {

    public void start(Stage stage) {
        stage.setTitle("Furniture Room Design Visualizers - Create Account");

        StackPane root = new StackPane();
        root.setPrefSize(1200, 800);
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

        Text cardHeader = new Text("Create Account");
        cardHeader.setFont(Font.font("Inter", FontWeight.MEDIUM, 32));
        cardHeader.setFill(Color.web("#2f3a58"));

        Text cardSubHeader = new Text("Sign up to start shopping");
        cardSubHeader.setFont(Font.font("Inter", 16));
        cardSubHeader.setFill(Color.web("#7b8699"));

        VBox usernameBox = createInputWithIcon(
            "Username",
            "Enter your username",
            "M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8",
            false
        );
        TextField usernameField = (TextField) ((StackPane) usernameBox.getChildren().get(1)).getChildren().get(0);

        VBox emailBox = createInputWithIcon(
            "Email Address",
            "Enter your email",
            "M3 8L10.89 13.26C11.57 13.71 12.43 13.71 13.11 13.26L21 8M5 19H19C20.1 19 21 18.1 21 17V7C21 5.9 20.1 5 19 5H5C3.9 5 3 5.9 3 7V17C3 18.1 3.9 19 5 19Z",
            false
        );
        TextField emailField = (TextField) ((StackPane) emailBox.getChildren().get(1)).getChildren().get(0);

        VBox passwordBox = createInputWithIcon(
            "Password",
            "Create a password",
            "M12 15c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm6-6h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v3H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V11c0-1.1-.9-2-2-2zM9 9V6c0-1.66 1.34-3 3-3s3 1.34 3 3v3H9z",
            true
        );
        PasswordField passwordField = (PasswordField) ((StackPane) passwordBox.getChildren().get(1)).getChildren().get(0);

        Button registerBtn = new Button("Create Account");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(50);
        registerBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #ca4bf6, #844cf5);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 18;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        );
        registerBtn.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                AuthService.registerLocalUser(
                    username,
                    username,
                    emailField.getText(),
                    passwordField.getText()
                );
                showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);
                new LoginFX().start(stage);
            } catch (Exception ex) {
                showAlert("Registration Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        VBox.setMargin(registerBtn, new Insets(8, 0, 10, 0));

        VBox divider = createDivider("or continue with");

        Text googleIcon = new Text("G");
        googleIcon.setFill(Color.web("#4285F4"));
        googleIcon.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Text googleText = new Text("Sign up with Google");
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
        googleBtn.setOnAction(e -> {
            try {
                AuthService.loginWithGoogle();
                new com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFX().start(stage);
            } catch (Exception ex) {
                showAlert("Google Sign Up Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        javafx.scene.layout.HBox bottomBox = new javafx.scene.layout.HBox(20);
        bottomBox.setAlignment(Pos.CENTER);

        Text hasAccountText = new Text("Already have an account?");
        hasAccountText.setFill(Color.web("#6f7b8f"));
        hasAccountText.setFont(Font.font("Inter", 14));
        hasAccountText.setTextAlignment(TextAlignment.CENTER);

        Hyperlink loginLink = new Hyperlink("Sign In");
        loginLink.setStyle(
            "-fx-text-fill: #8B5CF6;" +
            "-fx-font-weight: 500;" +
            "-fx-underline: false;" +
            "-fx-padding: 0;" +
            "-fx-font-size: 14;"
        );
        loginLink.setOnAction(e -> new LoginFX().start(stage));

        bottomBox.getChildren().addAll(hasAccountText, loginLink);
        VBox.setMargin(bottomBox, new Insets(10, 0, 0, 0));

        card.getChildren().addAll(
            cardHeader,
            cardSubHeader,
            usernameBox,
            emailBox,
            passwordBox,
            registerBtn,
            divider,
            googleBtn,
            bottomBox
        );

        contentWrapper.getChildren().addAll(titleBox, card);
        
        ScrollPane scrollPane = new ScrollPane(contentWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        
        root.getChildren().add(scrollPane);

        Scene scene = new Scene(root, 1200, 800);
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

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
