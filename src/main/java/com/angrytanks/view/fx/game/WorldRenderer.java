package com.angrytanks.view.fx.game;

import com.angrytanks.model.snapshot.EntitySnapshot;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.layout.Pane;

final class WorldRenderer {
  private final Pane parent;
  private final Map<Long, EntityRenderer> renderers = new LinkedHashMap<>();

  WorldRenderer(Pane parent) {
    this.parent = parent;
  }

  void display(List<EntitySnapshot> entities, boolean outlines) {
    removeMissingEntities(entities);
    for (var state : entities) {
      var renderer = renderers.get(state.id());
      if (renderer == null) {
        renderer = createRenderer(state);
        renderers.put(state.id(), renderer);
        parent.getChildren().add(renderer.node());
      }
      renderer.update(state, outlines);
    }
  }

  private void removeMissingEntities(List<EntitySnapshot> entities) {
    Set<Long> presentEntityIds = new HashSet<>();
    for (var state : entities) {
      presentEntityIds.add(state.id());
    }
    var iterator = renderers.entrySet().iterator();
    while (iterator.hasNext()) {
      var entry = iterator.next();
      if (presentEntityIds.contains(entry.getKey())) {
        continue;
      }
      parent.getChildren().remove(entry.getValue().node());
      iterator.remove();
    }
  }

  private static EntityRenderer createRenderer(EntitySnapshot state) {
    return switch (state.kind()) {
      case TANK -> new TankRenderer(state);
      case PROJECTILE -> new ProjectileRenderer(state);
      case TERRAIN -> new TerrainRenderer();
      case DECORATION -> new DecorationRenderer();
    };
  }

  void clear() {
    renderers.values().forEach(renderer -> parent.getChildren().remove(renderer.node()));
    renderers.clear();
  }
}
