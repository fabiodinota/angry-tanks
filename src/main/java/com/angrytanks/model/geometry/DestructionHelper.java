package com.angrytanks.model.geometry;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.overlay.snap.SnapIfNeededOverlayOp;

public final class DestructionHelper {
  private static final double TOPOLOGY_RETRY_BUFFER = 0.001;
  private DestructionHelper() {}

  public static Geometry subtractEllipse(
      Geometry terrain, Point2 center, CraterParameters parameters) {
    if (terrain.isEmpty()) return terrain;
    GeometryFactory geometryFactory = terrain.getFactory();
    double radiusX = parameters.radiusX();
    double radiusY = parameters.radiusY();
    double rotation = parameters.rotation();
    int vertexCount = parameters.ellipseVertices();
    double rotationCosine = Math.cos(rotation);
    double rotationSine = Math.sin(rotation);

    Coordinate[] ellipseCoordinates = new Coordinate[vertexCount + 1];
    for (int i = 0; i < vertexCount; i++) {
      double theta = 2 * Math.PI * i / vertexCount;

      double x = radiusX * Math.cos(theta);
      double y = radiusY * Math.sin(theta);

      double rotatedX = x * rotationCosine - y * rotationSine;
      double rotatedY = x * rotationSine + y * rotationCosine;

      ellipseCoordinates[i] = new Coordinate(center.x() + rotatedX, center.y() + rotatedY);
    }

    ellipseCoordinates[vertexCount] = ellipseCoordinates[0];

    Polygon crater = geometryFactory.createPolygon(ellipseCoordinates);

    Geometry difference;
    try {
      difference = SnapIfNeededOverlayOp.overlayOp(terrain, crater, OverlayOp.DIFFERENCE);
    } catch (TopologyException error) {
      Geometry bufferedTerrain = terrain.buffer(TOPOLOGY_RETRY_BUFFER);
      Geometry bufferedCrater = crater.buffer(TOPOLOGY_RETRY_BUFFER);
      difference =
          SnapIfNeededOverlayOp.overlayOp(bufferedTerrain, bufferedCrater, OverlayOp.DIFFERENCE);
    }
    return difference.buffer(0);
  }

  public static List<List<Point2>> subtractEllipseFromPolygon(
      List<Point2> polygon,
      Point2 center,
      double radiusX,
      double radiusY,
      double rotation,
      int vertexCount) {
    Geometry result =
        subtractEllipse(
            TerrainGeometry.polygon(polygon),
            center,
            new CraterParameters(radiusX, radiusY, rotation, vertexCount));
    List<List<Point2>> pieces = new ArrayList<>();
    for (int i = 0; i < result.getNumGeometries(); i++) {
      if (!(result.getGeometryN(i) instanceof Polygon polygonPart) || polygonPart.isEmpty()) {
        continue;
      }
      if (polygonPart.getNumInteriorRing() == 0) {
        pieces.add(TerrainGeometry.points(polygonPart.getExteriorRing().getCoordinates()));
      } else {
        pieces.addAll(TerrainGeometry.convexParts(polygonPart));
      }
    }
    return pieces;
  }
}
