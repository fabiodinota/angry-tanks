package com.angrytanks.model.world;

import com.angrytanks.model.assets.MapElement;
import com.angrytanks.model.entity.Actor;
import com.angrytanks.model.entity.Decoration;
import com.angrytanks.model.geometry.GeometryException;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.terrain.DestructibleTerrain;
import com.angrytanks.model.terrain.Grass;
import com.angrytanks.model.terrain.Landscape;
import com.angrytanks.model.terrain.Sand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MapLayout {
  private final List<Landscape> groundSegments = new ArrayList<>();
  private final List<Decoration> staticDecorations = new ArrayList<>();
  private final List<DestructibleTerrain> destructibleDecorations = new ArrayList<>();
  private String backgroundName;

  public void load(List<MapElement> elements, String background) {
    backgroundName = null;
    groundSegments.clear();
    staticDecorations.clear();
    destructibleDecorations.clear();
    if (elements.isEmpty()) throw new IllegalArgumentException("Map contains no supported shapes");
    backgroundName = background;
    for (var element : elements) {
      try {
        addElement(element);
      } catch (GeometryException failure) {
        throw failure.withContext("map shape '" + element.type() + "'");
      }
    }
  }

  private void addElement(MapElement element) {
    String type = element.type();
    if (type == null) return;
    List<Point2> vertices = element.vertices();
    String fill = element.fillColor();

    if (type.contains("ground")) {
      groundSegments.add(new Landscape(vertices, fill));
    } else if (type.contains("grass")) {
      destructibleDecorations.add(new Grass(vertices, fill));
    } else if (type.contains("sand")) {
      destructibleDecorations.add(new Sand(vertices, fill));
    } else if (type.contains("decor")) {
      Point2 origin = vertices.getFirst();
      staticDecorations.add(new Decoration(origin.x(), origin.y(), vertices, fill));
    }
  }

  public String getBackgroundName() {
    return backgroundName;
  }

  public List<Landscape> getGroundSegments() {
    return Collections.unmodifiableList(groundSegments);
  }

  public List<Decoration> getStaticDecorations() {
    return Collections.unmodifiableList(staticDecorations);
  }

  public List<DestructibleTerrain> getDestructibleDecorations() {
    return Collections.unmodifiableList(destructibleDecorations);
  }

  public List<Actor> getAllMapActors() {
    List<Actor> actors = new ArrayList<>();
    actors.addAll(groundSegments);
    actors.addAll(staticDecorations);
    actors.addAll(destructibleDecorations);
    return actors;
  }
}
