package com.angrytanks.view.fx.game;

import com.angrytanks.model.snapshot.EntitySnapshot;
import com.angrytanks.model.snapshot.GeometrySnapshot;
import javafx.scene.Group;
import javafx.scene.Node;

final class TerrainRenderer implements EntityRenderer {
  private final Group root = new Group();
  private GeometrySnapshot geometry;
  private boolean outlined;

  @Override
  public Node node() {
    return root;
  }

  @Override
  public void update(EntitySnapshot state, boolean outlines) {
    if (geometry != state.geometry() || outlined != outlines) {
      geometry = state.geometry();
      outlined = outlines;
      root.getChildren()
          .setAll(outlines ? Shapes.outlines(geometry.parts()) : Shapes.merge(geometry));
    }
  }
}
