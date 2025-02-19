package com.angrytanks.hud;

import com.angrytanks.core.GameEngine;
import com.angrytanks.hud.custom.HealthBar;
import com.angrytanks.hud.custom.MainMenuComponent;
import com.angrytanks.hud.custom.ShootBar;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GameHud {
    private Pane hudPane;
    private Stage stage;
    private GameEngine engine;
    private HealthBar healthBar;
    private ShootBar shootBar;

    public GameHud(Stage stage, GameEngine engine) {
        this.stage = stage;
        this.engine = engine;
        this.hudPane = new Pane();
        showMainMenu();
    }


    public void showMainMenu() {
        MainMenuComponent menu = new MainMenuComponent(stage, engine, this);
    }

    public void showGameHUD() {

    }

    public void render() {
            //not done yet
    }

    public Pane getHudPane() {
        return hudPane;
    }
}
