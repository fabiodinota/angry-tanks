package com.angrytanks;

import com.angrytanks.app.AppBootstrap;
import com.angrytanks.app.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage stage) {
    SceneManager sceneManager = AppBootstrap.create(stage);
    sceneManager.showMainMenu();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
