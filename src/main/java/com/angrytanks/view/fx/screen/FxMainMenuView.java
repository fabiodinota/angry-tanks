package com.angrytanks.view.fx.screen;

import com.angrytanks.view.contract.MainMenuView;
import com.angrytanks.view.fx.support.BackgroundUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FxMainMenuView implements MainMenuView {
  private static final Logger LOG = Logger.getLogger(FxMainMenuView.class.getName());

  @FXML private StackPane stackPane;
  @FXML private ImageView backgroundImage;
  @FXML private VBox menuBox;
  @FXML private Button playButton;
  @FXML private Button leaderboardButton;
  @FXML private Button exitButton;

  private int selectedIndex = -1;

  private Actions actions;

  @FXML
  private void initialize() {
    BackgroundUtils.setupBackground(stackPane, backgroundImage, "/com/angrytanks/images/bg2.png");

    Font font =
        Font.loadFont(getClass().getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 16);

    if (font == null) {
      LOG.warning("Failed to load the TTF; it may be invalid or unsupported by JavaFX");
    } else {
      LOG.log(Level.FINE, "Loaded font successfully: {0}", font.getName());
    }

    playButton.setFocusTraversable(true);
    leaderboardButton.setFocusTraversable(true);
    exitButton.setFocusTraversable(true);

    stackPane.setFocusTraversable(true);
    stackPane.requestFocus();
    focusButton(0);

    stackPane.setOnKeyPressed(this::handleKeyPress);

    playButton.setOnAction(event -> actions.play());
    leaderboardButton.setOnAction(event -> actions.leaderboard());
    exitButton.setOnAction(event -> actions.exit());
  }

  private void handleKeyPress(KeyEvent event) {
    switch (event.getCode()) {
      case UP, W -> moveFocus(-1);
      case DOWN, TAB, S -> moveFocus(1);
      case ENTER, SPACE -> ((Button) menuBox.getChildren().get(selectedIndex)).fire();
      default -> {}
    }
  }

  private void moveFocus(int direction) {
    int buttonCount = menuBox.getChildren().size();
    int newIndex = (selectedIndex + direction + buttonCount) % buttonCount;
    focusButton(newIndex);
  }

  private void focusButton(int newIndex) {
    var children = menuBox.getChildren();
    if (selectedIndex >= 0 && selectedIndex < children.size()) {
      Node oldNode = children.get(selectedIndex);
      if (oldNode instanceof Button button) {
        String text = button.getText();
        if (text.startsWith("> ")) {
          button.setText(text.substring(2));
        }
      }
    }

    selectedIndex = newIndex;

    Node newNode = children.get(selectedIndex);
    if (newNode instanceof Button button) {
      String text = button.getText();
      if (!text.startsWith("> ")) {
        button.setText("> " + text);
      }
    }
  }

  @Override
  public void bind(Actions actions) {
    this.actions = actions;
  }
}
