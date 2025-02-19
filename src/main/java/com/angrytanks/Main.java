package com.angrytanks;

import com.angrytanks.core.GameEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        GameEngine engine = new GameEngine(stage);

    }

    public static void main(String[] args) {
        launch(args);
    }
}