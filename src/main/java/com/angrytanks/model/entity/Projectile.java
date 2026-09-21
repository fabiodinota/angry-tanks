package com.angrytanks.model.entity;

import com.angrytanks.model.entity.tank.Tank;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.physics.PhysicsUnits;
import com.angrytanks.model.snapshot.EntitySnapshot;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public final class Projectile extends Actor {
  private final Tank shooter;
  private Body body;
  private boolean consumed;

  public Projectile(double x, double y, Tank shooter) {
    super(x, y);
    this.shooter = shooter;
  }

  public boolean consume() {
    if (consumed || body == null) return false;
    consumed = true;
    return true;
  }

  public boolean isConsumed() {
    return consumed;
  }

  public Body getPhysicsBody() {
    return body;
  }

  public Tank getShooter() {
    return shooter;
  }

  @Override
  public void addToPhysics(World world) {
    if (body != null) return;
    BodyDef bodyDefinition = new BodyDef();
    bodyDefinition.type = BodyType.DYNAMIC;
    bodyDefinition.position.set(
        (float) (position.x() / PhysicsUnits.PIXELS_PER_METER),
        (float) (position.y() / PhysicsUnits.PIXELS_PER_METER));
    body = world.createBody(bodyDefinition);
    body.setUserData(this);
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(0.5f, 0.5f);
    FixtureDef fixture = new FixtureDef();
    fixture.shape = shape;
    fixture.density = 1;
    fixture.friction = 0.2f;
    body.createFixture(fixture);
  }

  @Override
  public void updateFromPhysics() {
    if (body != null) {
      float pixelX = (float) (body.getPosition().x * PhysicsUnits.PIXELS_PER_METER);
      float pixelY = (float) (body.getPosition().y * PhysicsUnits.PIXELS_PER_METER);
      position = new Point2(pixelX, pixelY);
      body.setTransform(
          new Vec2(
              (float) (pixelX / PhysicsUnits.PIXELS_PER_METER),
              (float) (pixelY / PhysicsUnits.PIXELS_PER_METER)),
          body.getAngle());
      body.setAwake(true);
    }
  }

  @Override
  public EntitySnapshot snapshot() {
    double angle =
        body == null ? 0 : Math.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);
    return EntitySnapshot.forProjectile(
        getId(), "/tanks/projectile/projectile.png", position.x(), position.y(), angle);
  }

  @Override
  public void removeFromPhysics() {
    if (body != null) {
      body.getWorld().destroyBody(body);
      body = null;
    }
    consumed = true;
  }
}
