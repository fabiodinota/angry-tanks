package com.angrytanks.view.fx.support;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public final class BackgroundUtils {
  private BackgroundUtils() {}

  public static void setupBackground(
      StackPane stackPane, ImageView backgroundImage, String imagePath) {
    Image image = new Image(BackgroundUtils.class.getResource(imagePath).toExternalForm());
    backgroundImage.setImage(image);
    backgroundImage.setFitWidth(1980);
    backgroundImage.setFitHeight(1080);
    backgroundImage.setPreserveRatio(false);
    backgroundImage.setSmooth(true);
    backgroundImage.fitWidthProperty().bind(stackPane.widthProperty());
    backgroundImage.fitHeightProperty().bind(stackPane.heightProperty());
  }
}
