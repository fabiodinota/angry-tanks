package com.angrytanks.model.geometry;

import java.util.List;

public final class ConvexDecomposer {
  private static final double SIMPLIFICATION_TOLERANCE = 0.5;

  private ConvexDecomposer() {}

  public static List<List<Point2>> decompose(List<Point2> polygon) {
    var accepted = simplifyVertices(polygon, SIMPLIFICATION_TOLERANCE);
    if (accepted.isEmpty()) return List.of();
    var triangles = EarClipper.triangulate(accepted);
    var parts = ConvexMerger.merge(triangles);
    DecompositionValidator.validate(PolygonRing.polygon(accepted), parts);
    return parts;
  }

  public static List<Point2> normalizePolygon(List<Point2> polygon) {
    return List.copyOf(PolygonRing.normalize(polygon));
  }

  public static List<Point2> simplifyVertices(List<Point2> points, double tolerance) {
    return RingSimplifier.simplify(PolygonRing.prepare(points), tolerance);
  }

  public static boolean isConvexPolygon(List<Point2> polygon) {
    try {
      var ring = PolygonRing.prepare(polygon);
      return !ring.isEmpty() && PolygonRing.convex(ring);
    } catch (GeometryException invalid) {
      return false;
    }
  }
}
