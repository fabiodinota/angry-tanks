package com.angrytanks.ui.controllers;

import com.angrytanks.core.GameEngine;
import com.angrytanks.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


public class GameUIController {

    @FXML
    private AnchorPane gameContainer;

    private SceneManager sceneManager;

    @FXML
    private Pane gameRoot;

    private GameEngine gameEngine;

    @FXML
    private void initialize() {

    }

    @FXML
    private void onEndGameClicked() {
        if (sceneManager != null) {
            sceneManager.showWinLoseScene();
        }
        if (gameEngine != null) {
            gameEngine.stopGame();
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    public void startEngine() {
        if (gameEngine == null) {
            gameEngine = new GameEngine();
            gameEngine.startGame(gameContainer);
        }
    }
}
