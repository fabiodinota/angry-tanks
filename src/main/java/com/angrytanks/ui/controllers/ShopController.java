package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import com.angrytanks.util.BackgroundUtils;
import com.angrytanks.util.TankSelectionUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class ShopController {

    @FXML
    private VBox tankListVBox;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView backgroundImage;

    private SceneManager sceneManager;

    /**
     * Initializes the shop screen by populating the list of tanks.
     */
    @FXML
    private void initialize() {

        // Use the tank names from TankSelectionUtil as available items.
        String[] tankNames = TankSelectionUtil.tankNames;
        for (String tankName : tankNames) {
            HBox tankItem = new HBox();
            tankItem.getStyleClass().add("hbox-item");

            // Placeholder image for the tank (or coin SVG later)
            ImageView tankImage = new ImageView();
            tankImage.setFitWidth(100);
            tankImage.setFitHeight(100);
            tankImage.setImage(new Image(getClass().getResource("/com/angrytanks/images/tankSelection/" + tankName + ".png").toExternalForm())); // Replace with your SVG/asset

            // VBox for tank information
            VBox tankInfo = new VBox();
            tankInfo.setSpacing(5);
            Label nameLabel = new Label(tankName);
            nameLabel.getStyleClass().add("label");
            Label descriptionLabel = new Label("A powerful " + tankName + " tank with unique abilities.");
            descriptionLabel.getStyleClass().add("label");
            Label priceLabel = new Label("Price: 500 coins"); // All tanks have the same price as a placeholder
            priceLabel.getStyleClass().add("label");
            tankInfo.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);

            // "Buy" button to purchase the tank
            Button buyButton = new Button("Buy");
            buyButton.getStyleClass().add("button");
            buyButton.setOnAction(e -> {
                // Placeholder purchase logic
                System.out.println("Purchasing tank: " + tankName);
            });

            tankItem.getChildren().addAll(tankImage, tankInfo, buyButton);
            tankListVBox.getChildren().add(tankItem);
        }
    }

    /**
     * Returns to the main menu when the back button is clicked.
     */
    @FXML
    private void onBack() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
