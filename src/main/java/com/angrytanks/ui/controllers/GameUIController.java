package com.angrytanks.ui.controllers;

import com.angrytanks.core.GameEngine;
import com.angrytanks.core.GameState;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.entity.custom.tank.TankController;
import com.angrytanks.core.InputActions;
import com.angrytanks.ui.SceneManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class GameUIController {

    @FXML
    private AnchorPane gameContainer;

    private SceneManager sceneManager;
    private GameEngine gameEngine;

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
        gameEngine = new GameEngine();
        gameEngine.startGame(gameContainer);

        Scene scene = gameContainer.getScene();
        if (scene != null) {

            if (!GameState.players.isEmpty()) {
                Tank playerTank = GameState.players.get(0);
                TankController tankController = new TankController(playerTank);
                InputActions inputActions = new InputActions(tankController);
                scene.addEventHandler(KeyEvent.ANY, inputActions);
                System.out.println("InputActions attached to the scene.");
            } else {
                System.out.println("No player tanks found.");
            }
        }
    }
}
