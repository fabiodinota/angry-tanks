package com.angrytanks.model.entity.tank;

import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.geometry.PolygonUtil;
import com.angrytanks.model.geometry.TerrainGeometry;
import com.angrytanks.model.physics.PhysicsUnits;
import com.angrytanks.model.physics.PolygonFixtures;
import java.util.ArrayList;
import java.util.List;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public final class TankHull {
  private final double hullHeight;
  private final List<List<Point2>> parts;
  private Body body;

  public TankHull(List<Point2> vertices) {
    var clockwiseVertices = new ArrayList<>(vertices);
    PolygonUtil.ensureClockwiseOrder(clockwiseVertices);
    parts = TerrainGeometry.bodyParts(clockwiseVertices);
    hullHeight =
        clockwiseVertices.stream().mapToDouble(Point2::y).max().orElse(0)
            - clockwiseVertices.stream().mapToDouble(Point2::y).min().orElse(0);
  }

  public List<List<Point2>> getConvexParts() {
    return parts;
  }

  public void addToPhysics(World world) {
    if (body != null) return;
    var shapes = PolygonFixtures.prepare(parts, Point2.ZERO);
    BodyDef bodyDefinition = new BodyDef();
    bodyDefinition.type = BodyType.DYNAMIC;
    bodyDefinition.bullet = true;
    body = world.createBody(bodyDefinition);
    try {
      for (var shape : shapes) {
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 1;
        fixture.friction = 0.5f;
        body.createFixture(fixture);
      }
    } catch (RuntimeException | Error failure) {
      removeFromPhysics();
      throw failure;
    }
  }

  double getHullHeight() {
    return hullHeight;
  }

  public Body getPhysicsBody() {
    return body;
  }

  public void teleport(double x, double y) {
    if (body != null) {
      body.setTransform(
          new Vec2(
              (float) (x / PhysicsUnits.PIXELS_PER_METER),
              (float) (y / PhysicsUnits.PIXELS_PER_METER)),
          body.getAngle());
      body.setAwake(true);
    }
  }

  public void removeFromPhysics() {
    if (body != null) {
      body.getWorld().destroyBody(body);
      body = null;
    }
  }
}
