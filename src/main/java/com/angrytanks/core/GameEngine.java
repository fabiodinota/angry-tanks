package com.angrytanks.core;

import com.angrytanks.hud.GameHud;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

public class GameEngine {

    private final GameHud hud;
    private AnimationTimer gameLoop;
    private boolean running;

    public GameEngine() {
        this.hud = new GameHud(this);
        this.running = false;
    }

    public void startGame(Pane parentPane) {
        GameState.initialize();

        hud.showGameHUD();
        parentPane.getChildren().add(hud.getRootPane());

        running = true;
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
    }

    private void update() {
        GameState.update();
    }

    private void render() {
        hud.render();
    }

    public Pane getRootPane() {
        return hud.getRootPane();
    }

    public void stopGame() {
        running = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public boolean isRunning() {
        return running;
    }

}
