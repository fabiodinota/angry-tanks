package com.angrytanks.core;

import com.angrytanks.hud.GameHud;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

public class GameEngine {

    private final GameHud hud;
    private AnimationTimer gameLoop;
    private boolean running;

    public GameEngine(Stage stage) {
        this.hud = new GameHud(stage, this);
        this.running = false;
    }


    public void startGame() {
        GameState.initialize();
        hud.showGameHUD();

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
