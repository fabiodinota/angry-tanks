package com.angrytanks.core;

import com.angrytanks.hud.GameHud;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

public class GameEngine {

    private GameState gameState;
    private boolean running;
    private GameHud hud;
    private AnimationTimer gameLoop;



    public GameEngine(Stage stage) {
        this.gameState = new GameState();


        // starting the shitty hud automatically calling constructing with main menu //
        this.hud = new GameHud(stage, this);

        //is inside gama or still mainmenu????
        this.running = false;
    }




    public void startGame() {

        // In-Game logic here, like creating the world, players, etc... //
        // NOT USED IN MAIN MENU FOR PERFORMANCE REASONS (NOT LOADING UNNECESSARY STUFF) //
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

    public void update() {

    }

    public void render() {
        hud.render();
    }

    public void stopGame() {
        running = false;
        gameLoop.stop();
    }



}
