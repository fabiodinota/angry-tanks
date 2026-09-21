package com.angrytanks.view.fx.game;

import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.snapshot.GeometrySnapshot;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

final class Shapes {
  private Shapes() {}

  private static Color color(String fill, Color missingFillColor) {
    if (fill == null) return missingFillColor;
    try {
      return Color.web(fill);
    } catch (IllegalArgumentException invalidColor) {
      return Color.GRAY;
    }
  }

  static Polygon polygon(List<Point2> points, String fill, Color missingFillColor) {
    Polygon polygon = new Polygon();
    for (Point2 point : points) {
      polygon.getPoints().addAll(point.x(), point.y());
    }
    polygon.setFill(color(fill, missingFillColor));
    return polygon;
  }

  static Group outlines(List<List<Point2>> parts) {
    var group = new Group();
    for (var part : parts) {
      var outlinePolygon = polygon(part, null, Color.TRANSPARENT);
      outlinePolygon.setStroke(Color.RED);
      outlinePolygon.setStrokeWidth(2);
      group.getChildren().add(outlinePolygon);
    }
    return group;
  }

  static Shape merge(GeometrySnapshot geometry) {
    Shape result = null;
    for (var part : geometry.parts()) {
      Shape partShape = polygon(part, geometry.fill(), Color.GRAY);
      if (result == null) result = partShape;
      else {
        result = Shape.union(result, partShape);
        result.setFill(color(geometry.fill(), Color.GRAY));
      }
    }
    return result == null ? new Polygon() : result;
  }
}
