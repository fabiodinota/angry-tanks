package com.angrytanks.view.fx.game;

import com.angrytanks.model.snapshot.EntitySnapshot;
import javafx.scene.Node;

interface EntityRenderer {
  Node node();

  void update(EntitySnapshot state, boolean outlines);
}
