package com.angrytanks.hud.custom;

import com.angrytanks.core.GameEngine;
import com.angrytanks.hud.GameHud;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class MainMenuComponent {

    private final GameHud hud;
    private final GameEngine engine;
    private final Pane menuPane;

    public MainMenuComponent(Stage stage, GameHud hud, GameEngine engine) {
        this.hud = hud;
        this.engine = engine;
        this.menuPane = new Pane();
        createMenu();
    }


    private void createMenu() {
        Button startButton = new Button("Start Game");
        startButton.setLayoutX(350);

        menuPane.getChildren().add(startButton);

        startButton.setOnAction(e -> engine.startGame());
    }



    public Pane getMenuPane() {
        return menuPane;
    }
}
