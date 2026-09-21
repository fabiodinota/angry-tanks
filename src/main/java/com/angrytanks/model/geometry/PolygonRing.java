package com.angrytanks.model.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.valid.IsValidOp;

final class PolygonRing {
  static final GeometryFactory FACTORY = new GeometryFactory();

  private PolygonRing() {}

  static Coordinate coordinate(Point2 p) {
    return new Coordinate(p.x(), p.y());
  }

  static int turn(Point2 a, Point2 b, Point2 c) {
    return Orientation.index(coordinate(a), coordinate(b), coordinate(c));
  }

  static List<Point2> normalize(List<Point2> points) {
    if (points == null) throw new GeometryException("input", "null ring");
    var ring = new ArrayList<Point2>();
    for (var point : points) {
      if (point == null || !Double.isFinite(point.x()) || !Double.isFinite(point.y()))
        throw new GeometryException("input", "nonfinite or null vertex");

      double normalizedX = point.x() == 0 ? 0 : point.x();
      double normalizedY = point.y() == 0 ? 0 : point.y();
      var normalizedPoint = new Point2(normalizedX, normalizedY);
      if (ring.isEmpty() || !ring.getLast().equals(normalizedPoint)) ring.add(normalizedPoint);
    }
    while (ring.size() > 1 && ring.getFirst().equals(ring.getLast())) ring.removeLast();
    return ring;
  }

  static List<Point2> prepare(List<Point2> points) {
    var ring = normalize(points);
    if (ring.isEmpty()) return List.of();
    if (ring.size() < 3) throw new GeometryException("input", "fewer than three distinct vertices");
    Polygon polygon = polygon(ring);
    var error = new IsValidOp(polygon).getValidationError();
    if (error != null) throw new GeometryException("input", error.toString());
    double area = polygon.getArea();
    if (!(area > 0) || !Double.isFinite(area))
      throw new GeometryException("input", "zero or nonfinite ring area");
    return canonical(ring);
  }

  static List<Point2> canonical(List<Point2> points) {
    var ring = new ArrayList<>(points);
    if (Orientation.isCCW(polygon(ring).getCoordinates())) Collections.reverse(ring);
    int firstIndex = 0;
    for (int i = 1; i < ring.size(); i++) {
      if (compare(ring.get(i), ring.get(firstIndex)) < 0) firstIndex = i;
    }
    Collections.rotate(ring, -firstIndex);
    return List.copyOf(ring);
  }

  private static int compare(Point2 first, Point2 second) {
    int xComparison = Double.compare(first.x(), second.x());
    return xComparison == 0 ? Double.compare(first.y(), second.y()) : xComparison;
  }

  static Polygon polygon(List<Point2> ring) {
    if (ring.isEmpty()) return FACTORY.createPolygon();
    Coordinate[] coordinates = new Coordinate[ring.size() + 1];
    for (int i = 0; i < ring.size(); i++) {
      coordinates[i] = coordinate(ring.get(i));
    }
    coordinates[ring.size()] = coordinates[0].copy();
    return FACTORY.createPolygon(coordinates);
  }

  static boolean convex(List<Point2> ring) {
    if (ring.size() < 3) return false;
    int sign = 0;
    for (int i = 0; i < ring.size(); i++) {
      int turn =
          turn(ring.get(i), ring.get((i + 1) % ring.size()), ring.get((i + 2) % ring.size()));
      if (turn == 0) continue;
      if (sign != 0 && turn != sign) return false;
      sign = turn;
    }
    return sign != 0;
  }
}
