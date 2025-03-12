package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MainMenuController {

    @FXML
    private StackPane stackPane;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private VBox menuBox;
    @FXML
    private Button playButton;
    @FXML
    private Button shopButton;
    @FXML
    private Button leaderboardButton;
    @FXML
    private Button exitButton;

    private int previousIndex = -1;
    private int currentIndex = 0;

    private SceneManager sceneManager;

    @FXML
    public void initialize() {
        // Set your background image (replace with actual path/resource)
        backgroundImage.setImage(new Image(
                getClass().getResource("/com/angrytanks/images/angrytanks_background.png").toExternalForm()
        ));

        Font font = Font.loadFont(
                getClass().getResourceAsStream("/fonts/PressStart2P-Regular.ttf"),
                16
        );
        if (font == null) {
            System.out.println("Failed to load the TTF - it may be invalid or not recognized by JavaFX.");
        } else {
            System.out.println("Loaded font successfully: " + font.getName());
        }

        // Make sure each button can be focused and can handle arrow keys
        playButton.setFocusTraversable(true);
        shopButton.setFocusTraversable(true);
        leaderboardButton.setFocusTraversable(true);
        exitButton.setFocusTraversable(true);

        // Optional: Start focused on the first button
        stackPane.requestFocus();

        focusButton(0);

        // Listen for arrow key presses on the root (or scene)
        stackPane.setOnKeyPressed(this::handleKeyPress);

        // Add mouse click handlers
        playButton.setOnAction(event -> onPlayClicked());
        shopButton.setOnAction(event -> onShopClicked());
        leaderboardButton.setOnAction(event -> onLeaderboardClicked());
        exitButton.setOnAction(event -> onExitClicked());
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            currentIndex = (currentIndex - 1 + menuBox.getChildren().size()) % menuBox.getChildren().size();
            focusButton(currentIndex);
        } else if (event.getCode() == KeyCode.DOWN) {
            currentIndex = (currentIndex + 1) % menuBox.getChildren().size();
            focusButton(currentIndex);
        } else if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
            // "Click" the currently focused button
            Button focusedButton = (Button) menuBox.getChildren().get(currentIndex);
            focusedButton.fire();
        }
    }

    private void focusButton(int newIndex) {
        // Remove "> " from the previously focused button
        if (previousIndex >= 0 && previousIndex < menuBox.getChildren().size()) {
            Node oldNode = menuBox.getChildren().get(previousIndex);
            if (oldNode instanceof Button) {
                Button oldButton = (Button) oldNode;
                String oldText = oldButton.getText();
                if (oldText.startsWith("> ")) {
                    oldButton.setText(oldText.substring(2));
                }
            }
        }

        // Update the indexes
        currentIndex = newIndex;
        previousIndex = newIndex;

        // Retrieve the new node
        Node newNode = menuBox.getChildren().get(newIndex);
        if (newNode instanceof Button) {
            // DO NOT requestFocus() here if you want the arrow keys
            // to remain with the parent container
            Button newButton = (Button) newNode;
            String newText = newButton.getText();
            if (!newText.startsWith("> ")) {
                newButton.setText("> " + newText);
            }
        }
    }



    @FXML
    private void onPlayClicked() {
        if (sceneManager != null) {
            sceneManager.showTankSelection();
        } else {
            System.out.println("SceneManager not set in MainMenuController!");
        }
    }

    @FXML
    private void onShopClicked() {
        System.out.println("Shop clicked!");
    }

    @FXML
    private void onLeaderboardClicked() {
        System.out.println("Leaderboard clicked!");
    }

    @FXML
    private void onExitClicked() {
        System.exit(0);
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
