package com.angrytanks.view.fx.screen;

import com.angrytanks.view.contract.SelectionView;
import com.angrytanks.view.fx.support.BackgroundUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public final class FxSelectionView implements SelectionView {
  @FXML private StackPane stackPane;
  @FXML private ImageView backgroundImage;
  @FXML private ImageView leftTankFrame;
  @FXML private ImageView rightTankFrame;
  @FXML private ImageView leftTankPreview;
  @FXML private ImageView rightTankPreview;
  @FXML private Button leftTankNextButton;
  @FXML private Button leftTankPrevButton;
  @FXML private Button rightTankNextButton;
  @FXML private Button rightTankPrevButton;
  @FXML private TextField leftPlayerNameField;
  @FXML private TextField rightPlayerNameField;
  private Actions actions;

  @FXML
  private void initialize() {
    BackgroundUtils.setupBackground(stackPane, backgroundImage, "/com/angrytanks/images/bg2.png");
    var frame = image("/com/angrytanks/images/frame.png");
    leftTankFrame.setImage(frame);
    rightTankFrame.setImage(frame);
  }

  private Image image(String resource) {
    return new Image(getClass().getResource(resource).toExternalForm());
  }

  @Override
  public void bind(Actions actions) {
    this.actions = actions;
  }

  @Override
  public void display(State state) {
    leftTankPreview.setImage(
        image("/com/angrytanks/images/tankSelection/" + state.leftTank() + ".png"));
    rightTankPreview.setImage(
        image("/com/angrytanks/images/tankSelection/" + state.rightTank() + ".png"));
    leftTankPrevButton.setDisable(!state.leftPrev());
    leftTankNextButton.setDisable(!state.leftNext());
    rightTankPrevButton.setDisable(!state.rightPrev());
    rightTankNextButton.setDisable(!state.rightNext());
  }

  @Override
  public void invalidNames() {
    System.out.println("Please enter names for both tanks!");
  }

  @FXML
  private void onTankSelected() {
    actions.start(leftPlayerNameField.getText(), rightPlayerNameField.getText());
  }

  @FXML
  private void onBackClicked() {
    actions.back();
  }

  @FXML
  private void onLeftTankNext() {
    actions.changeLeft(1);
  }

  @FXML
  private void onLeftTankPrev() {
    actions.changeLeft(-1);
  }

  @FXML
  private void onRightTankNext() {
    actions.changeRight(1);
  }

  @FXML
  private void onRightTankPrev() {
    actions.changeRight(-1);
  }
}
