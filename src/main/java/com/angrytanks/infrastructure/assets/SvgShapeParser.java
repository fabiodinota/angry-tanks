package com.angrytanks.infrastructure.assets;

import com.angrytanks.model.geometry.Point2;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.Element;

final class SvgShapeParser {
  private static final double CLOSURE_TOLERANCE = 0.5;

  private SvgShapeParser() {}

  static List<Point2> vertices(Element shape) {
    return switch (shape.getTagName()) {
      case "path" -> parsePath(shape.getAttribute("d"));
      case "polygon", "polyline" ->
          closePolygonInPlace(parsePoints(shape.getAttribute("points").trim()));
      case "rect" -> rectangleVertices(shape);
      default -> throw new IllegalArgumentException("Unsupported SVG shape: " + shape.getTagName());
    };
  }

  private static List<Point2> parsePath(String data) {
    List<Point2> vertices = SvgPathParser.parsePathData(data);
    return data.trim().toLowerCase(Locale.ROOT).endsWith("z")
        ? closePolygonInPlace(vertices)
        : vertices;
  }

  private static List<Point2> rectangleVertices(Element shape) {
    double x = Double.parseDouble(shape.getAttribute("x"));
    double y = Double.parseDouble(shape.getAttribute("y"));
    double width = Double.parseDouble(shape.getAttribute("width"));
    double height = Double.parseDouble(shape.getAttribute("height"));
    List<Point2> vertices = new ArrayList<>();
    vertices.add(new Point2(x, y));
    vertices.add(new Point2(x + width, y));
    vertices.add(new Point2(x + width, y + height));
    vertices.add(new Point2(x, y + height));
    return closePolygonInPlace(vertices);
  }

  static List<Point2> closePolygonInPlace(List<Point2> points) {
    if (!points.isEmpty() && points.getFirst().distance(points.getLast()) > CLOSURE_TOLERANCE)
      points.add(points.getFirst());
    return points;
  }

  private static List<Point2> parsePoints(String data) {
    List<Point2> vertices = new ArrayList<>();

    for (String pair : data.split("\\s+")) {
      String[] coordinates = pair.split(",");
      vertices.add(
          new Point2(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])));
    }
    return vertices;
  }
}
