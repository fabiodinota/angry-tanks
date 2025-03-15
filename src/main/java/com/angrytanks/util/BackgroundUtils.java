package com.angrytanks.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class BackgroundUtils {

    public static void setupBackground(StackPane stackPane, ImageView backgroundImage, String imagePath) {
        // Load the background image
        Image image = new Image(
                BackgroundUtils.class.getResource(imagePath).toExternalForm()
        );

        // Set ImageView properties to make it full screen
        backgroundImage.setImage(image);
        backgroundImage.setFitWidth(1980);  // Force width to match scene
        backgroundImage.setFitHeight(1080); // Force height to match scene
        backgroundImage.setPreserveRatio(false); // Stretch if needed
        backgroundImage.setSmooth(true);  // Improve rendering quality

        // Ensure ImageView resizes with StackPane
        backgroundImage.fitWidthProperty().bind(stackPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(stackPane.heightProperty());
    }
}