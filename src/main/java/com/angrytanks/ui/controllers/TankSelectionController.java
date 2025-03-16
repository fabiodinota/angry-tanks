package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import com.angrytanks.util.BackgroundUtils;
import com.angrytanks.util.TankSelectionUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import static com.angrytanks.util.TankSelectionUtil.*;

public class TankSelectionController {

    @FXML private StackPane stackPane;
    @FXML private ImageView backgroundImage;
    @FXML private ImageView leftTankFrame;
    @FXML private ImageView rightTankFrame;
    @FXML private ImageView leftTankFrameInner;
    @FXML private ImageView rightTankFrameInner;
    @FXML private Button leftTankNext;
    @FXML private Button leftTankPrev;
    @FXML private Button rightTankNext;
    @FXML private Button rightTankPrev;
    @FXML private TextField nameField;   // Left tank name
    @FXML private TextField nameField2;  // Right tank name

    private TankSelectionUtil tankSelectionUtil;


    private SceneManager sceneManager;

    @FXML
    private void initialize() {
        // Set up the background and tank frame images.
        BackgroundUtils.setupBackground(stackPane, backgroundImage, "/com/angrytanks/images/bg2.png");

        Image frameImage = new Image(getClass().getResource("/com/angrytanks/images/frame.png").toExternalForm());

        updateTankImages();

        leftTankFrame.setImage(frameImage);
        rightTankFrame.setImage(frameImage);

        updateButtonsState();
    }

    private void updateTankImages() {
        Image innerImageLeft = new Image(getClass().getResource("/com/angrytanks/images/tankSelection/" + tankSelectionUtil.getPlayer1Tank() + ".png").toExternalForm());
        Image innerImageRight = new Image(getClass().getResource("/com/angrytanks/images/tankSelection/" + tankSelectionUtil.getPlayer2Tank() + ".png").toExternalForm());

        leftTankFrameInner.setImage(innerImageLeft);
        rightTankFrameInner.setImage(innerImageRight);
    }

    @FXML
    private void onTankSelected() {
        String leftName = nameField.getText();
        String rightName = nameField2.getText();

        if (sceneManager != null && !leftName.isEmpty() && !rightName.isEmpty()) {
            sceneManager.showGameScene();
        } else {
            System.out.println("Please enter names for both tanks!");
        }
    }

    @FXML
    private void onBackClicked() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    // --- Tank Navigation ---

    @FXML
    private void leftTankNext() {
        player1Tank = getNextTankIndex(player1Tank, player2Tank);
        updateButtonsState();
        updateTankImages();
        System.out.println("Player 1 Tank: " + player1Tank);
    }

    @FXML
    private void leftTankPrev() {
        player1Tank = getPrevTankIndex(player1Tank, player2Tank);
        updateButtonsState();
        updateTankImages();
        System.out.println("Player 1 Tank: " + player1Tank);
    }

    @FXML
    private void rightTankNext() {
        player2Tank = getNextTankIndex(player2Tank, player1Tank);
        updateButtonsState();
        updateTankImages();
        System.out.println("Player 2 Tank: " + player2Tank);
    }

    @FXML
    private void rightTankPrev() {
        player2Tank = getPrevTankIndex(player2Tank, player1Tank);
        updateButtonsState();
        updateTankImages();
        System.out.println("Player 2 Tank: " + player2Tank);
    }

    /**
     * Advances the tank index for a player while ensuring it doesn’t collide with the other player’s selection.
     */
    private int getNextTankIndex(int current, int other) {
        if (current < tankNames.length - 1) {
            int newIndex = current + 1;
            // Skip over the tank if it matches the other player's selection, provided it's not already at the end.
            if (newIndex == other && newIndex < tankNames.length - 1) {
                newIndex++;
            }
            return newIndex;
        }
        return current;
    }

    /**
     * Decrements the tank index for a player while ensuring it doesn’t collide with the other player’s selection.
     */
    private int getPrevTankIndex(int current, int other) {
        if (current > 0) {
            int newIndex = current - 1;
            // Skip over the tank if it matches the other player's selection, provided it's not already at the start.
            if (newIndex == other && newIndex > 0) {
                newIndex--;
            }
            return newIndex;
        }
        return current;
    }

    /**
     * Updates the enabled/disabled state of the next and previous buttons based on the current tank indices.
     */
    private void updateButtonsState() {
        leftTankPrev.setDisable(player1Tank <= 0);
        leftTankNext.setDisable(player1Tank >= tankNames.length - 1);
        rightTankPrev.setDisable(player2Tank <= 0);
        rightTankNext.setDisable(player2Tank >= tankNames.length - 1);
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
