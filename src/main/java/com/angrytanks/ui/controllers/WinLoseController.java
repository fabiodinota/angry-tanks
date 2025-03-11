package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WinLoseController {

    @FXML
    private Label resultLabel;

    private SceneManager sceneManager;

    @FXML
    private void initialize() {
        resultLabel.setText("Placeholder: You Win or Lose");
    }

    @FXML
    private void onPlayAgainClicked() {
        if (sceneManager != null) {
            sceneManager.showTankSelection();
        }
    }

    @FXML
    private void onExitToMenuClicked() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
