package com.angrytanks.model.physics;

import com.angrytanks.model.geometry.ConvexDecomposer;
import com.angrytanks.model.geometry.GeometryException;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.geometry.TerrainGeometry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;

public final class PolygonFixtures {
  private PolygonFixtures() {}

  public static List<PolygonShape> prepare(List<List<Point2>> parts, Point2 origin) {
    var shapes = new ArrayList<PolygonShape>();
    for (var part : parts) {
      shapes.add(prepareShape(part, origin));
    }
    return List.copyOf(shapes);
  }

  private static PolygonShape prepareShape(List<Point2> part, Point2 origin) {
    if (part.size() < 3 || part.size() > 8)
      throw new GeometryException("physics", "invalid vertex count");
    var vertices = new Vec2[part.size()];
    var intendedPoints = new ArrayList<Point2>();
    var rounded = new ArrayList<Point2>();
    for (int i = 0; i < part.size(); i++) {
      var point = part.get(i);
      double physicsX = (point.x() - origin.x()) / PhysicsUnits.PIXELS_PER_METER;
      double physicsY = (point.y() - origin.y()) / PhysicsUnits.PIXELS_PER_METER;
      intendedPoints.add(new Point2(physicsX, physicsY));

      float roundedX = (float) physicsX;
      float roundedY = (float) physicsY;
      vertices[i] = new Vec2(roundedX, roundedY);
      rounded.add(new Point2(roundedX, roundedY));
    }
    if (new HashSet<>(rounded).size() != rounded.size()
        || !ConvexDecomposer.isConvexPolygon(rounded))
      throw new GeometryException("physics", "float conversion collapsed a fixture");
    var intended = TerrainGeometry.polygon(intendedPoints);
    var shape = new PolygonShape();
    try {
      shape.set(vertices, vertices.length);
    } catch (RuntimeException | AssertionError failure) {
      throw new GeometryException("physics", "JBox2D rejected a fixture", failure);
    }
    var actual = new ArrayList<Point2>();
    for (int i = 0; i < shape.getVertexCount(); i++) {
      var vertex = shape.getVertex(i);
      actual.add(new Point2(vertex.x, vertex.y));
    }
    boolean remainsConvex = ConvexDecomposer.isConvexPolygon(actual);
    if (!remainsConvex) {
      throw new GeometryException("physics", "JBox2D changed fixture coverage");
    }
    double coverageDifference = intended.symDifference(TerrainGeometry.polygon(actual)).getArea();
    double allowedDifference = Math.max(1e-8, intended.getArea() * 1e-4);
    if (coverageDifference > allowedDifference) {
      throw new GeometryException("physics", "JBox2D changed fixture coverage");
    }
    return shape;
  }
}
