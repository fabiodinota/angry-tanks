package com.angrytanks.infrastructure.assets;

import com.angrytanks.model.assets.MapElement;
import com.angrytanks.model.assets.TankData;
import com.angrytanks.model.geometry.Point2;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SvgTankLoader {
  private SvgTankLoader() {}

  public static TankData loadTankData(String resourcePath, boolean shiftToLocalZero) {
    List<MapElement> elements = SvgAssetLoader.load(resourcePath).elements();
    Point2 origin = shiftToLocalZero ? minimumCoordinates(elements) : Point2.ZERO;
    var builder = new TankData.Builder();
    for (MapElement element : elements) {
      String part = element.type().toLowerCase(Locale.ROOT).replaceAll("(_\\d+)+_?$", "");
      List<Point2> vertices =
          shiftToLocalZero ? shiftVertices(element.vertices(), origin) : element.vertices();
      String fill = element.fillColor();
      switch (part) {
        case "tank_hull" -> builder.hull(vertices, fill);
        case "tank_track" -> builder.tracks(vertices, fill);
        case "tank_wheel" -> builder.wheel(vertices, fill);
        case "tank_decor" -> builder.decor(vertices, fill);
        case "tank_turret" -> builder.turret(vertices, fill);
        case "tank_cannon" -> builder.cannon(vertices, fill);
        case "cannon_anchor" -> builder.cannonAnchor(vertices);
        default -> {}
      }
    }
    TankData data = builder.build();
    if (data.hullVertices().size() < 3
        || data.turretVertices().size() < 3
        || data.cannonVertices().size() < 3
        || data.cannonAnchor().isEmpty())
      throw new IllegalArgumentException(
          "Tank asset is missing required geometry: " + resourcePath);
    return data;
  }

  private static Point2 minimumCoordinates(List<MapElement> elements) {
    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;

    for (MapElement element : elements) {
      for (Point2 vertex : element.vertices()) {
        if (vertex.x() < minX) {
          minX = vertex.x();
        }
        if (vertex.y() < minY) {
          minY = vertex.y();
        }
      }
    }
    return new Point2(minX, minY);
  }

  private static List<Point2> shiftVertices(List<Point2> vertices, Point2 origin) {
    List<Point2> shifted = new ArrayList<>();
    for (Point2 vertex : vertices)
      shifted.add(new Point2(vertex.x() - origin.x(), vertex.y() - origin.y()));
    return shifted;
  }
}
