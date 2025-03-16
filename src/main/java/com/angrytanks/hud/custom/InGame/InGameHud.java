package com.angrytanks.hud.custom.InGame;

import com.angrytanks.core.GameState;
import com.angrytanks.entity.Actor;
import com.angrytanks.util.Decomposable;
import com.angrytanks.util.TankSelectionUtil;
import com.angrytanks.ui.SceneManager;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

import static com.angrytanks.hud.custom.InGame.HealthBar.player1Health;
import static com.angrytanks.hud.custom.InGame.HealthBar.player2Health;

public class InGameHud {
    private final Pane hudPane;
    private Canvas canvas;

    private TankSelectionUtil tankSelectionUtil;

    // SceneManager reference
    private SceneManager sceneManager;

    private ImageView leftHBFrame;
    private ImageView rightHBFrame;
    private Rectangle leftHealthFill;
    private Rectangle rightHealthFill;

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

        // Health Bars
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
        rightHBGroup.setLayoutX(1920 - 300 - 20);
        rightHBGroup.setLayoutY(20);
        hudPane.getChildren().add(rightHBGroup);
    }

    public void render() {
        for (Actor actor : GameState.world.getAllActors()) {
            if (!hudPane.getChildren().contains(actor.getVisuals())) {
                hudPane.getChildren().add(actor.getVisuals());
            }
            actor.render();
        }

        double maxHealth = 100.0;
        double p1Width = (player1Health / maxHealth) * 255.0;
        double p2Width = (player2Health / maxHealth) * 255.0;

        if (leftHealthFill != null) {
            leftHealthFill.setWidth(p1Width);
        }
        if (rightHealthFill != null) {
            rightHealthFill.setWidth(p2Width);
        }

        if (!gameOver) {
            if (player1Health <= 0) {
                showWinModal("PLAYER 2 WINS!");
                gameOver = true;
            } else if (player2Health <= 0) {
                showWinModal("PLAYER 1 WINS!");
                gameOver = true;
            }
        }
    }

    private void showWinModal(String winnerText) {
        Pane modal = new Pane();
        modal.setPrefSize(600, 300);
        modal.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        modal.setLayoutX((1920 - 600) / 2.0);
        modal.setLayoutY((1080 - 300) / 2.0);

        VBox contentBox = new VBox(30);
        contentBox.setLayoutX(50);
        contentBox.setLayoutY(50);

        // Large retro label
        Button title = new Button(winnerText);
        title.setDisable(true);
        title.getStyleClass().add("title-label");

        // "Play Again" -> go back to tank selection
        Button playAgainBtn = new Button("PLAY AGAIN");
        playAgainBtn.getStyleClass().add("menu-button");
        playAgainBtn.setOnAction(e -> {
            if (sceneManager != null) {
                // Reset or reload as you wish; here we just go to tank selection
                sceneManager.showTankSelection();
            }
        });

        // "Return to Menu"
        Button returnMenuBtn = new Button("RETURN TO MENU");
        returnMenuBtn.getStyleClass().add("menu-button");
        returnMenuBtn.setOnAction(e -> {
            if (sceneManager != null) {
                sceneManager.showMainMenu();
            }
        });

        contentBox.getChildren().addAll(title, playAgainBtn, returnMenuBtn);
        modal.getChildren().add(contentBox);

        hudPane.getChildren().add(modal);
    }

    // Called by whatever loads this InGameHud to provide the scene manager
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Pane getHudPane() {
        return hudPane;
    }
}
