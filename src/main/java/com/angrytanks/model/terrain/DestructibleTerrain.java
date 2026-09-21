package com.angrytanks.model.terrain;

import com.angrytanks.model.entity.Actor;
import com.angrytanks.model.geometry.CraterParameters;
import com.angrytanks.model.geometry.DestructionHelper;
import com.angrytanks.model.geometry.GeometryException;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.geometry.TerrainGeometry;
import com.angrytanks.model.physics.PolygonFixtures;
import com.angrytanks.model.snapshot.EntitySnapshot;
import com.angrytanks.model.snapshot.GeometrySnapshot;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;

public abstract class DestructibleTerrain extends Actor {
  private static final Logger LOG = Logger.getLogger(DestructibleTerrain.class.getName());
  private final String fill;
  private final float density;
  private final float friction;
  private Geometry geometry;
  private List<List<Point2>> parts;
  private CraterParameters crater;
  private Body body;
  private long revision;
  private GeometrySnapshot snapshot;

  protected DestructibleTerrain(
      List<Point2> vertices, String fill, CraterParameters crater, float density, float friction) {
    super(vertices.getFirst().x(), vertices.getFirst().y());
    this.fill = fill;
    this.crater = crater;
    this.density = density;
    this.friction = friction;
    geometry = TerrainGeometry.polygon(vertices);
    parts = TerrainGeometry.convexParts(geometry);
    snapshot = new GeometrySnapshot(revision, List.of(), parts, fill);
  }

  public void applyImpact(Point2 point) {
    try {
      boolean invalidPoint =
          point == null || !Double.isFinite(point.x()) || !Double.isFinite(point.y());
      if (invalidPoint) {
        throw new GeometryException("impact", "nonfinite or null impact position");
      }
      Geometry damagedGeometry = DestructionHelper.subtractEllipse(geometry, point, crater);
      replace(damagedGeometry, body == null ? null : body.getWorld(), revision + 1);
    } catch (RuntimeException failure) {
      LOG.log(
          Level.WARNING,
          "Terrain " + getId() + " rejected impact at " + point + ": " + failure.getMessage(),
          failure);
    }
  }

  public boolean isEmpty() {
    return geometry.isEmpty();
  }

  public Geometry getGeometry() {
    return geometry.copy();
  }

  public void setCraterParameters(CraterParameters value) {
    crater = Objects.requireNonNull(value);
  }

  public void rebuild(World world) {
    replace(geometry, world, revision);
  }

  private void replace(Geometry nextGeometry, World world, long nextRevision) {
    boolean requestedWorldLocked = world != null && world.isLocked();
    boolean currentWorldLocked = body != null && body.getWorld().isLocked();
    if (requestedWorldLocked || currentWorldLocked)
      throw new GeometryException("physics", "terrain replacement during world step");
    List<List<Point2>> nextParts = TerrainGeometry.convexParts(nextGeometry);
    List<PolygonShape> shapes = PolygonFixtures.prepare(nextParts, Point2.ZERO);
    var nextSnapshot = new GeometrySnapshot(nextRevision, List.of(), nextParts, fill);
    Body replacementBody =
        world == null || nextGeometry.isEmpty() ? null : createBody(world, shapes);

    if (body != null) body.getWorld().destroyBody(body);
    body = replacementBody;
    geometry = nextGeometry;
    parts = nextParts;
    snapshot = nextSnapshot;
    revision = nextRevision;
  }

  @Override
  public void addToPhysics(World world) {
    if (body != null || isEmpty()) return;
    body = createBody(world, PolygonFixtures.prepare(parts, Point2.ZERO));
  }

  private Body createBody(World world, List<PolygonShape> shapes) {
    if (world.isLocked())
      throw new GeometryException("physics", "terrain replacement during world step");
    var definition = new BodyDef();
    definition.type = BodyType.STATIC;
    Body newBody = world.createBody(definition);
    try {
      newBody.setUserData(this);
      for (var shape : shapes) {
        var fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = density;
        fixture.friction = friction;
        newBody.createFixture(fixture);
      }
      return newBody;
    } catch (RuntimeException | AssertionError failure) {
      world.destroyBody(newBody);
      throw new GeometryException("physics", "terrain fixture creation failed", failure);
    } catch (Error fatal) {
      world.destroyBody(newBody);
      throw fatal;
    }
  }

  public Body getPhysicsBody() {
    return body;
  }

  @Override
  public void removeFromPhysics() {
    if (body != null) {
      body.getWorld().destroyBody(body);
      body = null;
    }
  }

  public void teleport(double x, double y) {
    var translatedGeometry =
        AffineTransformation.translationInstance(x - position.x(), y - position.y())
            .transform(geometry);
    replace(translatedGeometry, body == null ? null : body.getWorld(), revision + 1);
    position = new Point2(x, y);
  }

  @Override
  public EntitySnapshot snapshot() {
    return EntitySnapshot.forTerrain(getId(), snapshot);
  }
}
