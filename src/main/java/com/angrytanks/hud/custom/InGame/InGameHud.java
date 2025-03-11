package com.angrytanks.hud.custom.InGame;

import com.angrytanks.core.GameState;
import com.angrytanks.entity.custom.Tank;
import com.angrytanks.util.Decomposable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.angrytanks.entity.Actor;

import java.io.InputStream;

public class InGameHud {
    private final Pane hudPane;
    private Canvas canvas;

    public InGameHud() {
        this.hudPane = new Pane();
        canvas = new Canvas(800, 600);
    }


    public void showGameHUD() {
        GameState.setupPlayers(1);


        //please not hate i will change it later
        //used to get the background image from the map layout
        String bgFile = GameState.getMapLayout().getBackgroundName();
        if (bgFile != null && !bgFile.isEmpty()) {
            System.out.println("Loading background image: " + bgFile);
            String resourcePath = "/backgrounds/" + bgFile;
            InputStream bgStream = getClass().getResourceAsStream(resourcePath);
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                ImageView bgView = new ImageView(bgImage);

                bgView.setFitWidth(1980);
                bgView.setFitHeight(1080);
                bgView.setPreserveRatio(true);

                hudPane.getChildren().add(bgView);
            } else {
                System.err.println("Background image not found at: " + resourcePath);
            }
        }
        //please not hate i will change it later




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


        Button randomPosition = new Button("Random Position ( Tanks )");
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

        Button randomPositionAll = new Button("Random Position ( All )");
        randomPositionAll.setLayoutX(350);
        randomPositionAll.setLayoutY(100);
        hudPane.getChildren().add(randomPositionAll);
        randomPositionAll.setOnAction(e -> {
            for (Actor actor : GameState.world.getAllActors()) {
                double randX = Math.random() * 800;
                double randY = 50 + Math.random() * 400;
                actor.teleport(randX, randY);
            }
        });


        Button getActors = new Button("Get Actors ");
        getActors.setLayoutX(350);
        getActors.setLayoutY(70);
        hudPane.getChildren().add(getActors);
        getActors.setOnAction(e -> {
            for (Actor actor : GameState.world.getAllActors()) {
                System.out.println(actor);
            }
        });
        Button getStaticDecorations = new Button("Get Static elements ");
        getStaticDecorations.setLayoutX(500);
        getStaticDecorations.setLayoutY(130);
        hudPane.getChildren().add(getStaticDecorations);
        getStaticDecorations.setOnAction(e -> {
            for (Actor actor : GameState.getMapLayout().getAllMapActors()) {
                System.out.println(actor); // get all map bs elemets
            }
        });


//        Button showDecomposition = new Button("Show Decomposition");
//        showDecomposition.setLayoutX(350);
//        showDecomposition.setLayoutY(130);
//        hudPane.getChildren().add(showDecomposition);
//        showDecomposition.setOnAction(e -> {
//            for (Actor actor : GameState.world.getAllActors()) {
//                if (actor instanceof Landscape) {
//                    ((Landscape) actor).showDecompositionOutline();
//                }
//            }
//        });


        CheckBox showDecomposition = new CheckBox("Show Decomposition");
        showDecomposition.setLayoutX(350);
        showDecomposition.setLayoutY(130);
        hudPane.getChildren().add(showDecomposition);
        showDecomposition.setOnAction(e -> {
            for (Actor actor : GameState.world.getAllActors()) {
                if (actor instanceof Decomposable) {
                    if (showDecomposition.isSelected()) {
                        ((Decomposable) actor).showDecompositionOutline();
                    } else {
                        ((Decomposable) actor).hideDecompositionOutline();
                    }
                }
            }
        });
    }


    public void render() {
        for (Actor actor : GameState.world.getAllActors()) {
            actor.render();
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Pane getHudPane() {
        return hudPane;
    }
}
