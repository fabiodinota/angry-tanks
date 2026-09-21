package com.angrytanks.model.geometry;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;

public final class TerrainGeometry {
  private static final double TRIANGULATION_TOLERANCE = 1e-8;

  private TerrainGeometry() {}

  public static Geometry polygon(List<Point2> vertices) {
    return PolygonRing.polygon(PolygonRing.prepare(vertices));
  }

  public static List<Point2> points(Coordinate[] coordinates) {
    var result = new ArrayList<Point2>();
    for (var coordinate : coordinates) {
      result.add(new Point2(coordinate.x, coordinate.y));
    }
    return ConvexDecomposer.normalizePolygon(result);
  }

  public static List<List<Point2>> bodyParts(List<Point2> vertices) {
    return convexParts(polygon(vertices));
  }

  public static List<List<Point2>> convexParts(Geometry geometry) {
    if (geometry == null) throw new GeometryException("input", "null geometry");
    var result = new ArrayList<List<Point2>>();
    appendParts(geometry, result);
    if (geometry instanceof GeometryCollection) DecompositionValidator.validateDisjoint(result);
    return DecompositionValidator.immutable(result);
  }

  private static void appendParts(Geometry geometry, List<List<Point2>> result) {
    if (geometry.isEmpty()) return;
    if (geometry instanceof GeometryCollection collection) {
      for (int i = 0; i < collection.getNumGeometries(); i++) {
        appendParts(collection.getGeometryN(i), result);
      }
      return;
    }
    if (!(geometry instanceof Polygon polygon)) {
      return;
    }
    var error = new IsValidOp(polygon).getValidationError();
    double area = polygon.getArea();
    if (error != null || !(area > 0) || !Double.isFinite(area))
      throw new GeometryException("input", "invalid polygon: " + error);
    for (var coordinate : polygon.getCoordinates()) {
      if (!Double.isFinite(coordinate.x) || !Double.isFinite(coordinate.y))
        throw new GeometryException("input", "nonfinite vertex");
    }
    result.addAll(decomposePolygon(polygon));
  }

  private static List<List<Point2>> decomposePolygon(Polygon polygon) {
    if (polygon.getNumInteriorRing() > 0) {
      return triangulate(polygon);
    }

    try {
      return ConvexDecomposer.decompose(points(polygon.getExteriorRing().getCoordinates()));
    } catch (GeometryException customFailure) {
      try {
        return triangulate(polygon);
      } catch (GeometryException fallbackFailure) {
        fallbackFailure.addSuppressed(customFailure);
        throw fallbackFailure;
      }
    }
  }

  private static List<List<Point2>> triangulate(Polygon polygon) {
    try {
      var builder = new ConformingDelaunayTriangulationBuilder();
      builder.setSites(polygon);
      builder.setConstraints(polygon.getBoundary());
      builder.setTolerance(TRIANGULATION_TOLERANCE);
      var triangles = builder.getTriangles(PolygonRing.FACTORY);
      var parts = new ArrayList<List<Point2>>();
      double tolerance = DecompositionValidator.areaTolerance(polygon);
      for (int i = 0; i < triangles.getNumGeometries(); i++) {
        var triangle = triangles.getGeometryN(i);
        double triangleArea = triangle.getArea();
        if (!(triangleArea > 0)) {
          continue;
        }

        double allowedAreaError = Math.min(tolerance, triangleArea * 1e-10);
        if (polygon.covers(triangle)
            || triangle.difference(polygon).getArea() <= allowedAreaError) {
          parts.add(PolygonRing.prepare(points(triangle.getCoordinates())));
        } else if (triangle.intersection(polygon).getArea() > allowedAreaError) {
          throw new GeometryException("fallback", "triangle crosses a polygon boundary");
        }
      }
      var merged = ConvexMerger.merge(parts);
      DecompositionValidator.validate(polygon, merged);
      return merged;
    } catch (GeometryException error) {
      throw error;
    } catch (RuntimeException error) {
      throw new GeometryException("fallback", "triangulation failed", error);
    }
  }
}
