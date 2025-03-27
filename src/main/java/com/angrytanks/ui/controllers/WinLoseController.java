package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import com.angrytanks.util.BackgroundUtils;
import com.angrytanks.util.GameOverUtil;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class WinLoseController {

    @FXML
    private StackPane stackPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView backgroundImage;

    private SceneManager sceneManager;

    @FXML
    private void initialize() {
        BackgroundUtils.setupBackground(stackPane, backgroundImage, "/com/angrytanks/images/bg2.png");
        // Bind the title label so it automatically updates when playerWon changes.
        titleLabel.textProperty().bind(Bindings.concat("Player ", GameOverUtil.playerWonProperty(), " won!"));

        // Optionally, set or update your message label here.
        messageLabel.setText("Congratulations! You have defeated the enemy!");
    }

    @FXML
    private void onPlayAgain() {
        if (sceneManager != null) {
            sceneManager.showTankSelection();
        }
    }

    @FXML
    private void onMainMenu() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
