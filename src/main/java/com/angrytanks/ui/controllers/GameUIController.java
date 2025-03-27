package com.angrytanks.ui.controllers;

import com.angrytanks.core.GameEngine;
import com.angrytanks.core.GameState;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.entity.custom.tank.TankController;
import com.angrytanks.core.InputActions;
import com.angrytanks.ui.SceneManager;
import com.angrytanks.util.GameOverUtil;
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

    private GameEngine gameEngine;

    private SceneManager sceneManager;


    public void startEngine() {
        gameEngine = new GameEngine();
        gameEngine.startGame(gameContainer);

        Scene scene = gameContainer.getScene();
        if (scene != null) {
            Tank currentTank = GameState.getCurrentPlayer();
            if (currentTank != null) {
                TankController tankController = new TankController(currentTank);
                InputActions inputActions = new InputActions(tankController);
                scene.addEventHandler(KeyEvent.ANY, inputActions);
                System.out.println("InputActions attached to the scene for the current tank.");
            } else {
                System.out.println("No player tanks found.");
            }
        }

        GameOverUtil.addListener(() -> {
            System.out.println("Game over.");
            if (sceneManager != null) {
                sceneManager.showWinLoseScene();
                GameState.getWorld().clearActors();
            }
        });
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
