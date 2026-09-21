package com.angrytanks.model.geometry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.operation.union.UnaryUnionOp;

final class DecompositionValidator {
  static final int MAX_VERTICES = 8;

  private DecompositionValidator() {}

  static double areaTolerance(Geometry source) {
    return Math.max(1e-5, source.getArea() * 1e-10);
  }

  static boolean validPart(List<Point2> part) {
    int vertexCount = part.size();
    if (vertexCount < 3 || vertexCount > MAX_VERTICES) return false;
    if (new HashSet<>(part).size() != vertexCount) return false;

    boolean containsInvalidCoordinate =
        part.stream()
            .anyMatch(
                point ->
                    point == null || !Double.isFinite(point.x()) || !Double.isFinite(point.y()));
    if (containsInvalidCoordinate) return false;
    var polygon = PolygonRing.polygon(part);
    return polygon.isValid() && polygon.getArea() > 0 && PolygonRing.convex(part);
  }

  static void validate(Geometry source, List<List<Point2>> parts) {
    if (source.isEmpty() && parts.isEmpty()) return;
    if (parts.isEmpty()) throw new GeometryException("validation", "nonempty polygon has no parts");
    var polygons = new ArrayList<Geometry>();
    double totalPartsArea = 0;
    for (var part : parts) {
      if (!validPart(part))
        throw new GeometryException("validation", "invalid convex fixture: " + part);
      var polygon = PolygonRing.polygon(part);
      polygons.add(polygon);
      totalPartsArea += polygon.getArea();
    }
    try {
      Geometry union = UnaryUnionOp.union(polygons);
      double tolerance = areaTolerance(source);
      if (totalPartsArea - union.getArea() > tolerance)
        throw new GeometryException("validation", "overlapping parts");
      if (source.symDifference(union).getArea() > tolerance)
        throw new GeometryException("validation", "parts do not cover the intended polygon");
    } catch (TopologyException failure) {
      throw new GeometryException("validation", "coverage operation failed", failure);
    }
  }

  static List<List<Point2>> immutable(List<List<Point2>> parts) {
    return parts.stream().map(List::copyOf).toList();
  }

  static void validateDisjoint(List<List<Point2>> parts) {
    if (parts.isEmpty()) return;
    var polygons = parts.stream().map(PolygonRing::polygon).toList();
    try {
      var union = UnaryUnionOp.union(polygons);
      double totalPartsArea = polygons.stream().mapToDouble(Geometry::getArea).sum();
      if (totalPartsArea - union.getArea() > areaTolerance(union))
        throw new GeometryException("validation", "overlapping polygon components");
    } catch (TopologyException failure) {
      throw new GeometryException("validation", "component overlap check failed", failure);
    }
  }
}
