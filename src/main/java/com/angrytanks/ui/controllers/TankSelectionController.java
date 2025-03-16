package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import com.angrytanks.util.BackgroundUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class TankSelectionController {

    @FXML
    private StackPane stackPane;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private ImageView leftTankFrame; // Matches fx:id in FXML
    @FXML
    private ImageView rightTankFrame; // Matches fx:id in FXML
    @FXML
    private TextField nameField; // For left tank
    @FXML
    private TextField nameField2; // For right tank

    private SceneManager sceneManager;

    @FXML
    private void initialize() {
        BackgroundUtils.setupBackground(stackPane, backgroundImage, "/com/angrytanks/images/bg2.png");

        Image leftTankImage = new Image(getClass().getResource("/com/angrytanks/images/frame.png").toExternalForm());
        Image rightTankImage = new Image(getClass().getResource("/com/angrytanks/images/frame.png").toExternalForm());

        leftTankFrame.setImage(leftTankImage);
        rightTankFrame.setImage(rightTankImage);
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

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}