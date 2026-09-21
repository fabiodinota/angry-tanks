package com.angrytanks.view.fx.game;

import com.angrytanks.view.contract.GameFrame;
import java.util.function.Consumer;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class InGameHud {
  private static final double HEALTH_BAR_WIDTH = 255;
  private static final double HEALTH_BAR_HEIGHT = 32;
  private static final double MAX_HEALTH = 100;
  private static final double HEALTH_FRAME_WIDTH = 300;
  private static final double HEALTH_FRAME_MARGIN = 20;

  private final Pane pane = new Pane();
  private final WorldRenderer renderer = new WorldRenderer(pane);
  private final Rectangle leftHealth =
      new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.RED);
  private final Rectangle rightHealth =
      new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.RED);
  private final CheckBox outlines = new CheckBox("Show Decomposition");
  private boolean initialized;

  public InGameHud(Consumer<Boolean> toggle) {
    outlines.setLayoutX(350);
    outlines.setLayoutY(130);
    outlines.setOnAction(e -> toggle.accept(outlines.isSelected()));
  }

  public void display(GameFrame frame) {
    var match = frame.match();
    if (!initialized) addBackground(match.background());
    renderer.display(match.entities(), frame.decomposition());
    if (!initialized) {
      pane.getChildren()
          .addAll(
              outlines, createHealthFrame(leftHealth, true), createHealthFrame(rightHealth, false));
      initialized = true;
    }
    outlines.setSelected(frame.decomposition());
    leftHealth.setWidth(match.players().get(0).health() * HEALTH_BAR_WIDTH / MAX_HEALTH);
    rightHealth.setWidth(match.players().get(1).health() * HEALTH_BAR_WIDTH / MAX_HEALTH);
  }

  private void addBackground(String background) {
    if (background == null) return;
    var resource = getClass().getResource("/backgrounds/" + background);
    if (resource == null) return;

    var image = new ImageView(new Image(resource.toExternalForm()));
    image.setFitWidth(1980);
    image.setFitHeight(1080);
    image.setPreserveRatio(true);
    pane.getChildren().add(image);
  }

  private Group createHealthFrame(Rectangle fill, boolean isLeft) {
    String side = isLeft ? "L" : "R";
    String resourcePath = "/com/angrytanks/images/HealthBar" + side + ".png";
    var resource = getClass().getResource(resourcePath);
    var frameImage = new Image(resource.toExternalForm());
    var frame = new ImageView(frameImage);
    frame.setFitWidth(HEALTH_FRAME_WIDTH);
    frame.setFitHeight(50);
    fill.setLayoutX(isLeft ? 40 : 5);
    fill.setLayoutY(10);
    if (!isLeft) fill.setScaleX(-1);
    var group = new Group(fill, frame);
    group.setLayoutX(
        isLeft
            ? HEALTH_FRAME_MARGIN
            : 1920 - HEALTH_FRAME_WIDTH - HEALTH_FRAME_MARGIN);
    group.setLayoutY(20);
    return group;
  }

  public Pane getHudPane() {
    return pane;
  }

  public void clear() {
    outlines.setOnAction(null);
    renderer.clear();
    pane.getChildren().clear();
  }
}
