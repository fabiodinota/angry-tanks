package com.angrytanks.view.fx.game;

import com.angrytanks.model.snapshot.EntitySnapshot;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

final class ProjectileRenderer implements EntityRenderer {
  private static final double IMAGE_WIDTH = 12.5;
  private static final double IMAGE_HEIGHT = 4;
  private final Group root = new Group();
  private final ImageView image;
  private final Rectangle hitbox = new Rectangle(18, 6, Color.TRANSPARENT);

  ProjectileRenderer(EntitySnapshot state) {
    image = new ImageView(new Image(getClass().getResource(state.assetId()).toExternalForm()));
    image.setFitWidth(IMAGE_WIDTH);
    image.setFitHeight(IMAGE_HEIGHT);
    root.getChildren().addAll(image, hitbox);
  }

  @Override
  public Node node() {
    return root;
  }

  @Override
  public void update(EntitySnapshot state, boolean outlines) {
    image.setX(state.x() - IMAGE_WIDTH / 2);
    image.setY(state.y() - IMAGE_HEIGHT / 2);
    image.setRotate(Math.toDegrees(state.angle()));
    hitbox.setX(state.x());
    hitbox.setY(state.y());
  }
}
