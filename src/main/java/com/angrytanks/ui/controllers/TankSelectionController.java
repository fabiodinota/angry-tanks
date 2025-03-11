package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TankSelectionController {

    @FXML
    private TextField nameField;

    private SceneManager sceneManager;

    @FXML
    private void initialize() {
    }

    @FXML
    private void onTankSelected() {
        String username = nameField.getText();
        System.out.println("Name chosen: " + username);

        if (sceneManager != null) {
            sceneManager.showGameScene();
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
