package com.angrytanks.hud.custom.InGame;

import com.angrytanks.core.GameState;
import com.angrytanks.entity.custom.Tank;
import com.angrytanks.world.Landscape;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.angrytanks.entity.Actor;

public class InGameHud {

    private final Stage stage;
    private final Pane hudPane;

    public InGameHud(Stage stage) {
        this.stage = stage;
        this.hudPane = new Pane();
    }


    public void showGameHUD() {
        GameState.setupPlayers(2);


        for (Actor actor : GameState.world.getAllActors()) {
            hudPane.getChildren().add(actor.getVisuals());
        }



        Button getPosition = new Button("Get Position");
        getPosition.setLayoutX(350);
        getPosition.setLayoutY(10);
        hudPane.getChildren().add(getPosition);
        getPosition.setOnAction(e -> {
            for (Tank tank : GameState.players) {
                System.out.println(tank.getPosition());
            }
        });


        Button randomPosition = new Button("Random Position");
        randomPosition.setLayoutX(350);
        randomPosition.setLayoutY(40);
        hudPane.getChildren().add(randomPosition);
        randomPosition.setOnAction(e -> {
            for (Tank tank : GameState.players) {
                double randX = Math.random() * 800;
                double randY = 50 + Math.random() * 400;
                tank.teleport(randX, randY);
            }
        });

    }


    public void render() {
        for (Actor actor : GameState.world.getAllActors()) {
            actor.render();
        }
    }

    public Pane getHudPane() {
        return hudPane;
    }
}
