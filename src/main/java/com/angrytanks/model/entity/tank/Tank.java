package com.angrytanks.model.entity.tank;

import com.angrytanks.model.api.DelayScheduler;
import com.angrytanks.model.assets.TankData;
import com.angrytanks.model.entity.Actor;
import com.angrytanks.model.entity.Projectile;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.geometry.PolygonUtil;
import com.angrytanks.model.physics.PhysicsUnits;
import com.angrytanks.model.snapshot.EntitySnapshot;
import com.angrytanks.model.snapshot.GeometrySnapshot;
import com.angrytanks.model.snapshot.TankAppearance;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public final class Tank extends Actor {
  private final TankHull hull;
  private final HullBounds hullBounds;
  private final boolean mirrored;
  private final Consumer<Projectile> projectileSink;
  private final String assetId;
  private final TankAppearance appearance;
  private TankTurret turret;
  private double angle;
  private double cannonAngle;

  public Tank(
      TankData data,
      String assetId,
      double x,
      double y,
      boolean mirrored,
      Consumer<Projectile> projectileSink,
      DelayScheduler scheduler) {
    super(x, y);
    this.assetId = assetId;
    this.mirrored = mirrored;
    this.projectileSink = projectileSink;
    hull = new TankHull(data.hullVertices());
    hullBounds = calculateHullBounds(data.hullVertices());
    appearance = createAppearance(data);
    turret = new TankTurret(data, this, scheduler);
  }

  private static GeometrySnapshot geometry(
      List<Point2> vertices, List<List<Point2>> parts, String fill) {
    return new GeometrySnapshot(0, vertices, parts, fill);
  }

  private static List<Point2> clockwise(List<Point2> vertices) {
    var copy = new ArrayList<>(vertices);
    PolygonUtil.ensureClockwiseOrder(copy);
    return copy;
  }

  private static HullBounds calculateHullBounds(List<Point2> vertices) {
    double minimumX = Double.POSITIVE_INFINITY;
    double maximumX = Double.NEGATIVE_INFINITY;
    for (Point2 vertex : vertices) {
      minimumX = Math.min(minimumX, vertex.x());
      maximumX = Math.max(maximumX, vertex.x());
    }
    return vertices.isEmpty() ? new HullBounds(0, 0) : new HullBounds(minimumX, maximumX);
  }

  private TankAppearance createAppearance(TankData data) {
    return new TankAppearance(
        geometry(data.hullVertices(), hull.getConvexParts(), data.hullColor()),
        geometry(data.turretVertices(), List.of(), data.turretColor()),
        geometry(clockwise(data.cannonVertices()), List.of(), data.cannonColor()),
        geometry(data.trackVertices(), List.of(), data.trackColor()),
        geometry(data.decorVertices(), List.of(), data.decorColor()),
        data.wheelVertices().stream()
            .map(vertices -> geometry(vertices, List.of(), data.wheelColor()))
            .toList(),
        data.cannonAnchor().getFirst());
  }

  @Override
  public void addToPhysics(World world) {
    hull.addToPhysics(world);
    var hullBody = hull.getPhysicsBody();
    hullBody.setUserData(this);
    hullBody.setTransform(
        new Vec2(
            (float) (position.x() / PhysicsUnits.PIXELS_PER_METER),
            (float) (position.y() / PhysicsUnits.PIXELS_PER_METER)),
        hullBody.getAngle());
    turret.addToPhysics(world);
  }

  void addProjectile(Projectile projectile) {
    projectileSink.accept(projectile);
  }

  @Override
  public void updateFromPhysics() {
    var hullBody = hull.getPhysicsBody();
    if (hullBody == null) return;

    SurfaceAlignment.align(this, hullBody.getWorld());
    synchronizePose();
  }

  public void synchronizePose() {
    var hullBody = hull.getPhysicsBody();
    if (hullBody == null) {
      return;
    }

    var physicsPosition = hullBody.getPosition();
    float pixelsPerMeter = (float) PhysicsUnits.PIXELS_PER_METER;
    float x = physicsPosition.x * pixelsPerMeter;
    float y = physicsPosition.y * pixelsPerMeter;

    position = new Point2(x, y);
    angle = hullBody.getAngle();
    if (turret != null) {
      cannonAngle = turret.getCannon().getPhysicsBody().getAngle();
    }
  }

  public Point2 cannonTip() {
    return TankKinematics.tip(appearance, position.x(), position.y(), angle, cannonAngle, mirrored);
  }

  @Override
  public void removeFromPhysics() {
    if (turret != null) turret.removeFromPhysics();
    hull.removeFromPhysics();
  }

  public void teleport(double x, double y) {
    Point2 origin = position;
    var hullBody = hull.getPhysicsBody();
    if (hullBody != null) {
      var bodyPosition = hullBody.getPosition();
      origin =
          new Point2(
              bodyPosition.x * PhysicsUnits.PIXELS_PER_METER,
              bodyPosition.y * PhysicsUnits.PIXELS_PER_METER);
    }
    if (turret != null) {
      double offsetX = x - origin.x();
      double offsetY = y - origin.y();
      turret.translatePhysics(offsetX, offsetY);
    }
    position = new Point2(x, y);
    hull.teleport(x, y);
  }

  @Override
  public EntitySnapshot snapshot() {
    var pivot = TankKinematics.pivot(appearance, cannonAngle, turret != null);
    return EntitySnapshot.forTank(
        getId(),
        assetId,
        position.x(),
        position.y(),
        angle,
        mirrored,
        cannonAngle,
        turret != null,
        pivot.x(),
        pivot.y(),
        appearance);
  }

  public TankHull getHull() {
    return hull;
  }

  public TankTurret getTurret() {
    return turret;
  }

  public boolean isMirrored() {
    return mirrored;
  }

  HullBounds getHullBounds() {
    return hullBounds;
  }

  public void detachTurret() {
    if (turret != null) {
      turret.removeFromPhysics();
      turret = null;
    }
  }

  static final class HullBounds {
    final double width;
    final double centerX;

    private HullBounds(double minX, double maxX) {
      width = maxX - minX;
      centerX = minX + width / 2;
    }
  }
}
