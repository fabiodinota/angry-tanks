package com.angrytanks.view.fx.screen;

import com.angrytanks.view.contract.ResultsView;
import com.angrytanks.view.fx.support.BackgroundUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public final class FxResultsView implements ResultsView {
  @FXML private StackPane stackPane;
  @FXML private Label titleLabel;
  @FXML private ImageView backgroundImage;
  private Runnable mainMenuAction;

  @FXML
  private void initialize() {
    BackgroundUtils.setupBackground(stackPane, backgroundImage, "/com/angrytanks/images/bg2.png");
  }

  @Override
  public void showWinner(String title) {
    titleLabel.setText(title);
  }

  @FXML
  private void onMainMenu() {
    mainMenuAction.run();
  }

  @Override
  public void bind(Runnable mainMenuAction) {
    this.mainMenuAction = mainMenuAction;
  }
}
