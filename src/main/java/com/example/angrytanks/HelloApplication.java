package com.example.angrytanks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1980, 1080);
        stage.setTitle("Angry Tanks");
        stage.setScene(scene);
        stage.show();



        //test Jbox2d
        World world = new World(new Vec2(-9, -9));


    }

    public static void main(String[] args) {
        launch();
    }
}