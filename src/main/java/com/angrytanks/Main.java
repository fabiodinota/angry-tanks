package com.angrytanks;

import com.angrytanks.core.GameEngine;
import com.angrytanks.ui.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GameEngine engine = new GameEngine();

        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
