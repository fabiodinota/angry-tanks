package com.angrytanks.hud.custom;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;

import javafx.stage.Stage;

import com.angrytanks.core.GameEngine;
import com.angrytanks.hud.GameHud;


public class MainMenuComponent {
    private Stage stage;
    private GameEngine engine;
    private GameHud hud;
    private Pane root;

    public MainMenuComponent(Stage stage, GameEngine engine, GameHud hud) {
        this.stage = stage;
        this.engine = engine;
        this.hud = hud;
        this.root = new Pane();
        showMenu();
    }

    public void showMenu() {


        Button startButton = new Button("Start Game");
        Button exitButton = new Button("Exit Game");


        startButton.setLayoutX(350);
        root.getChildren().add(startButton);
        root.getChildren().add(exitButton);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

        //will have separate class for this ?? IDK
        exitButton.setOnAction(e -> {
            hideMenu();
        });
    }


    public void hideMenu() {
        root.getChildren().clear();
    }
}
