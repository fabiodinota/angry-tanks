package com.angrytanks.hud.custom.InGame;

import com.angrytanks.core.GameState;
import com.angrytanks.entity.Actor;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.util.Decomposable;
import com.angrytanks.util.TankSelectionUtil;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

import static com.angrytanks.hud.custom.InGame.HealthBar.player1Health;
import static com.angrytanks.hud.custom.InGame.HealthBar.player2Health;

public class InGameHud {
    private final Pane hudPane;
    private Canvas canvas;

    private TankSelectionUtil tankSelectionUtil;

    // -------------------------------------------
    // ADDED: References for the health bar frames & fills
    // -------------------------------------------
    private ImageView leftHBFrame;
    private ImageView rightHBFrame;
    private Rectangle leftHealthFill;
    private Rectangle rightHealthFill;

    // ADDED: Track if we've already shown the modal
    private boolean gameOver = false;

    public InGameHud() {
        this.hudPane = new Pane();
        canvas = new Canvas(1920, 1080);
        Group trajectoryLayer;
    }

    public void showGameHUD() {

        System.out.println("In gamehud: " + tankSelectionUtil.getPlayer1Tank() + " " + tankSelectionUtil.getPlayer2Tank());
        GameState.setupPlayers(
                2,
                "/tanks/" + TankSelectionUtil.getPlayer1Tank() + ".svg",
                "/tanks/" + TankSelectionUtil.getPlayer2Tank() + ".svg"
        );

        String bgFile = GameState.getMapLayout().getBackgroundName();
        if (bgFile != null && !bgFile.isEmpty()) {
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

        for (Actor actor : GameState.world.getAllActors()) {
            hudPane.getChildren().add(actor.getVisuals());
        }

        /*
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
        });*/

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

        // ------------------------------------------------------------------
        // ADDED: Create the health bar frames & fill rectangles
        // ------------------------------------------------------------------
        // LEFT BAR
        InputStream leftStream = getClass().getResourceAsStream("/com/angrytanks/images/HealthBarL.png");
        if (leftStream != null) {
            Image leftImg = new Image(leftStream);
            leftHBFrame = new ImageView(leftImg);
            leftHBFrame.setFitHeight(50);
            leftHBFrame.setFitWidth(300);
            leftHBFrame.setLayoutX(0);
            leftHBFrame.setLayoutY(0);
        } else {
            System.err.println("Left health bar image not found!");
            leftHBFrame = new ImageView(); // fallback
        }

        leftHealthFill = new Rectangle(255, 32, Color.RED);
        leftHealthFill.setLayoutX(40);
        leftHealthFill.setLayoutY(10);

        Group leftHBGroup = new Group(leftHealthFill, leftHBFrame);
        leftHBGroup.setLayoutX(20);
        leftHBGroup.setLayoutY(20);
        hudPane.getChildren().add(leftHBGroup);

        // RIGHT BAR
        InputStream rightStream = getClass().getResourceAsStream("/com/angrytanks/images/HealthBarR.png");
        if (rightStream != null) {
            Image rightImg = new Image(rightStream);
            rightHBFrame = new ImageView(rightImg);
            rightHBFrame.setFitHeight(50);
            rightHBFrame.setFitWidth(300);
            rightHBFrame.setLayoutX(0);
            rightHBFrame.setLayoutY(0);

        } else {
            System.err.println("Right health bar image not found!");
            rightHBFrame = new ImageView(); // fallback
        }

        rightHealthFill = new Rectangle(255, 32, Color.RED);
        rightHealthFill.setLayoutX(5);
        rightHealthFill.setLayoutY(10);
        rightHealthFill.setScaleX(-1);

        Group rightHBGroup = new Group(rightHealthFill, rightHBFrame);
        rightHBGroup.setLayoutX(1920 - 300 - 20); // adjust as needed
        rightHBGroup.setLayoutY(20);
        hudPane.getChildren().add(rightHBGroup);
    }

    public void render() {
        for (Actor actor : GameState.world.getAllActors()) {
            if (!hudPane.getChildren().contains(actor.getVisuals())) {
                hudPane.getChildren().add(actor.getVisuals()); // Ensure it's added
            }
            actor.render();
        }

        // ------------------------------------------------------------------
        // ADDED: Update the fill widths based on static player health
        // ------------------------------------------------------------------
        double maxHealth = 100.0;
        double p1Width = (player1Health / maxHealth) * 255.0;
        double p2Width = (player2Health / maxHealth) * 255.0;

        if (leftHealthFill != null) {
            leftHealthFill.setWidth(p1Width);
        }
        if (rightHealthFill != null) {
            rightHealthFill.setWidth(p2Width);
        }

        // ------------------------------------------------------------------
        // ADDED: Check if someone has 0 health => Show "Player X won" modal
        // ------------------------------------------------------------------
        if (!gameOver) {
            if (player1Health <= 0) {
                showWinModal("Player 2 Won!");
                gameOver = true;
            } else if (player2Health <= 0) {
                showWinModal("Player 1 Won!");
                gameOver = true;
            }
        }
    }

    // ------------------------------------------------------------------
    // ADDED: Simple "Game Over" modal
    // ------------------------------------------------------------------
    private void showWinModal(String winnerText) {
        // Create a semi-transparent overlay pane
        Pane modal = new Pane();
        modal.setPrefSize(400, 200);
        modal.setStyle("-fx-background-color: rgba(0,0,0,0.8);");
        // Center the pane (roughly) in a 1920x1080 layout
        modal.setLayoutX((1920 - 400) / 2.0);
        modal.setLayoutY((1080 - 200) / 2.0);

        // Label-like button just to display text
        Button title = new Button(winnerText);
        title.setDisable(true); // Not clickable
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20;");
        title.setLayoutX(100);
        title.setLayoutY(40);

        // "Play Again" button
        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setLayoutX(50);
        playAgainBtn.setLayoutY(120);
        playAgainBtn.setOnAction(e -> {
            // TODO: Reset game state or reload the scene
            System.out.println("Play Again clicked!");
        });

        // "Return to Menu" button
        Button returnMenuBtn = new Button("Return to Menu");
        returnMenuBtn.setLayoutX(200);
        returnMenuBtn.setLayoutY(120);
        returnMenuBtn.setOnAction(e -> {
            // TODO: Use your SceneManager here to go back to main menu
            System.out.println("Return to Menu clicked!");
        });

        modal.getChildren().addAll(title, playAgainBtn, returnMenuBtn);
        hudPane.getChildren().add(modal);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Pane getHudPane() {
        return hudPane;
    }
}
