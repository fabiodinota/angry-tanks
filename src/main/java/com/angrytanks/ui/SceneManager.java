package com.angrytanks.ui;

import com.angrytanks.ui.controllers.GameUIController;
import com.angrytanks.ui.controllers.LeaderboardController;
import com.angrytanks.ui.controllers.MainMenuController;
import com.angrytanks.ui.controllers.ShopController;
import com.angrytanks.ui.controllers.TankSelectionController;
import com.angrytanks.ui.controllers.WinLoseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private Stage primaryStage;

    private Scene mainMenuScene;
    private Scene tankSelectionScene;
    private Scene gameScene;
    private Scene winLoseScene;
    private Scene shopScene;
    private Scene leaderboardScene;

    private GameUIController gameUIController;

    public SceneManager(Stage stage) {
        stage.setResizable(false);
        this.primaryStage = stage;
        initScenes();
    }

    private void initScenes() {
        try {
            // Main Menu
            FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/MainMenu.fxml"));
            Parent mainMenuRoot = mainMenuLoader.load();
            MainMenuController mainMenuController = mainMenuLoader.getController();
            mainMenuController.setSceneManager(this);
            mainMenuScene = new Scene(mainMenuRoot, 1980, 1080);
            mainMenuScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/angrytanks/css/mainmenu.css")).toExternalForm());

            // Tank Selection
            FXMLLoader tankSelectionLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/TankSelection.fxml"));
            Parent tankSelectionRoot = tankSelectionLoader.load();
            TankSelectionController tankSelectionController = tankSelectionLoader.getController();
            tankSelectionController.setSceneManager(this);
            tankSelectionScene = new Scene(tankSelectionRoot, 1980, 1080);
            tankSelectionScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/angrytanks/css/tankselection.css")).toExternalForm());

            // Game UI
            FXMLLoader gameUILoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/GameUI.fxml"));
            Parent gameUIRoot = gameUILoader.load();
            gameUIController = gameUILoader.getController();
            gameUIController.setSceneManager(this);
            gameScene = new Scene(gameUIRoot, 1980, 1080);

            // Win/Lose
            FXMLLoader winLoseLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/WinLose.fxml"));
            Parent winLoseRoot = winLoseLoader.load();
            WinLoseController winLoseController = winLoseLoader.getController();
            winLoseController.setSceneManager(this);
            winLoseScene = new Scene(winLoseRoot, 1980, 1080);
            winLoseScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/angrytanks/css/winlose.css")).toExternalForm());

            // Shop Scene
            FXMLLoader shopLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/ShopScreen.fxml"));
            Parent shopRoot = shopLoader.load();
            ShopController shopController = shopLoader.getController();
            shopController.setSceneManager(this);
            shopScene = new Scene(shopRoot, 1980, 1080);
            shopScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/angrytanks/css/shop.css")).toExternalForm());

            // Leaderboard Scene
            FXMLLoader leaderboardLoader = new FXMLLoader(getClass().getResource("/com/angrytanks/fxml/LeaderboardScreen.fxml"));
            Parent leaderboardRoot = leaderboardLoader.load();
            LeaderboardController leaderboardController = leaderboardLoader.getController();
            leaderboardController.setSceneManager(this);
            leaderboardScene = new Scene(leaderboardRoot, 1980, 1080);
            leaderboardScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/angrytanks/css/leaderboard.css")).toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Scene switching methods:

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
        primaryStage.setScene(shopScene);
        primaryStage.show();
    }

    public void showLeaderboard() {
        primaryStage.setTitle("Angry Tanks - Leaderboard");
        primaryStage.setScene(leaderboardScene);
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
