package com.angrytanks.ui;

import com.angrytanks.ui.controllers.GameUIController;
import com.angrytanks.ui.controllers.MainMenuController;
import com.angrytanks.ui.controllers.TankSelectionController;
import com.angrytanks.ui.controllers.WinLoseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private Stage primaryStage;

    private Scene mainMenuScene;
    private MainMenuController mainMenuController;

    private Scene tankSelectionScene;
    private TankSelectionController tankSelectionController;

    private Scene gameScene;
    private GameUIController gameUIController;

    private Scene winLoseScene;
    private WinLoseController winLoseController;

    public SceneManager(Stage stage) {
        this.primaryStage = stage;
        initScenes();
    }

    private void initScenes() {
        try {
            // Main Menu
            FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/MainMenu.fxml"));
            Parent mainMenuRoot = mainMenuLoader.load();
            mainMenuController = mainMenuLoader.getController();
            mainMenuController.setSceneManager(this);
            mainMenuScene = new Scene(mainMenuRoot, 800, 600);

            // Tank Selection
            FXMLLoader tankSelectionLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/TankSelection.fxml"));
            Parent tankSelectionRoot = tankSelectionLoader.load();
            tankSelectionController = tankSelectionLoader.getController();
            tankSelectionController.setSceneManager(this);
            tankSelectionScene = new Scene(tankSelectionRoot, 800, 600);

            // Game UI
            FXMLLoader gameUILoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/GameUI.fxml"));
            Parent gameUIRoot = gameUILoader.load();
            gameUIController = gameUILoader.getController();
            gameUIController.setSceneManager(this);
            gameScene = new Scene(gameUIRoot, 1980, 1080);

            // Win/Lose
            FXMLLoader winLoseLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/WinLose.fxml"));
            Parent winLoseRoot = winLoseLoader.load();
            winLoseController = winLoseLoader.getController();
            winLoseController.setSceneManager(this);
            winLoseScene = new Scene(winLoseRoot, 800, 600);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Show each scene:

    public void showMainMenu() {
        primaryStage.setTitle("Angry Tanks - Main Menu");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public void showTankSelection() {
        primaryStage.setTitle("Angry Tanks - Tank Selection");
        primaryStage.setScene(tankSelectionScene);
        primaryStage.show();
    }

    public void showShop() {
        primaryStage.setTitle("Angry Tanks - Shop");
        // primaryStage.setScene(shopScene);
        primaryStage.show();
    }

    public void showLeaderboard() {
        primaryStage.setTitle("Angry Tanks - Leaderboard");
        // primaryStage.setScene(leaderboardScene);
        primaryStage.show();
    }

    public void showGameScene() {
        primaryStage.setTitle("Angry Tanks - In Game");
        primaryStage.setScene(gameScene);

        gameUIController.startEngine();


        primaryStage.show();
    }

    public void showWinLoseScene() {
        primaryStage.setTitle("Angry Tanks - Results");
        primaryStage.setScene(winLoseScene);
        primaryStage.show();
    }
}
