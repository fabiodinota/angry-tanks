package com.angrytanks.hud;

import com.angrytanks.core.GameEngine;
import com.angrytanks.hud.custom.InGame.InGameHud;
import javafx.scene.layout.Pane;

public class GameHud {

    private final GameEngine engine;
    private final Pane rootPane;
    private InGameHud inGameHud;

    public GameHud(GameEngine engine) {
        this.engine = engine;
        this.rootPane = new Pane();
    }

    public void showGameHUD() {
        inGameHud = new InGameHud();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(inGameHud.getHudPane());

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
