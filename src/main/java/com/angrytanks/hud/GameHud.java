package com.angrytanks.hud;

import com.angrytanks.core.GameEngine;
import com.angrytanks.hud.custom.InGame.InGameHud;
import com.angrytanks.hud.custom.MainMenuComponent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class GameHud {

    private final Stage stage;
    private final GameEngine engine;
    private final Pane rootPane;
    private InGameHud inGameHud;
    private MainMenuComponent menu;

    public GameHud(Stage stage, GameEngine engine) {
        this.stage = stage;
        this.engine = engine;
        this.rootPane = new Pane();
        showMainMenu();
    }


    public void showMainMenu() {
        menu = new MainMenuComponent(stage, this, engine);
        Scene menuScene = new Scene(menu.getMenuPane(), 800, 600);
        stage.setScene(menuScene);
        stage.show();
    }


    public void showGameHUD() {
        inGameHud = new InGameHud(stage);
        Scene gameScene = new Scene(inGameHud.getHudPane(), 800, 600);
        stage.setScene(gameScene);
        stage.show();

        inGameHud.showGameHUD();
    }


    public void render() {
        if (inGameHud != null) {
            inGameHud.render();
        }
    }


    public Pane getRootPane() {
        return rootPane;
    }
}
