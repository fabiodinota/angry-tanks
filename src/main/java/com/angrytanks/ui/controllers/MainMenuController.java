package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {

    @FXML
    private Button playButton;

    private SceneManager sceneManager;

    @FXML
    private void initialize() {

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
