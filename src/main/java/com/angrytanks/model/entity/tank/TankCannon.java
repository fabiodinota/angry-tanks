package com.angrytanks.model.entity.tank;

import com.angrytanks.model.api.DelayScheduler;
import com.angrytanks.model.api.Registration;
import com.angrytanks.model.entity.Projectile;
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
import org.jbox2d.dynamics.joints.RevoluteJoint;

public final class TankCannon {
  private static final float MIN_AIM_ANGLE = (float) Math.toRadians(-45);
  private static final float MAX_AIM_ANGLE = (float) Math.toRadians(20);
  private static final float AIM_MOTOR_SPEED = 3;
  private static final int AIM_PULSE_MILLIS = 50;
  private final List<List<Point2>> parts;
  private final Point2 pivot;
  private final Tank tank;
  private final DelayScheduler scheduler;
  private final List<Registration> timers = new ArrayList<>();
  private Body body;
  private RevoluteJoint joint;

  public TankCannon(List<Point2> vertices, Point2 pivot, Tank tank, DelayScheduler scheduler) {
    var points = new ArrayList<>(vertices);
    PolygonUtil.ensureClockwiseOrder(points);
    parts = TerrainGeometry.bodyParts(points);
    this.pivot = pivot;
    this.tank = tank;
    this.scheduler = scheduler;
  }

  public void addToPhysics(World world) {
    if (body != null) return;
    var shapes = PolygonFixtures.prepare(parts, pivot);
    BodyDef bodyDefinition = new BodyDef();
    bodyDefinition.type = BodyType.DYNAMIC;
    bodyDefinition.position.set(
        (float) (pivot.x() / PhysicsUnits.PIXELS_PER_METER),
        (float) (pivot.y() / PhysicsUnits.PIXELS_PER_METER));
    body = world.createBody(bodyDefinition);
    body.setUserData(this);
    body.setGravityScale(1f);
    try {
      for (var shape : shapes) {
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 1;
        fixture.friction = 0.5f;
        fixture.isSensor = true;
        body.createFixture(fixture);
      }
    } catch (RuntimeException | Error failure) {
      removeFromPhysics();
      throw failure;
    }
  }

  void setCannonJoint(RevoluteJoint joint) {
    this.joint = joint;
  }

  public void rotateUp() {
    if (joint != null && body.getAngle() > MIN_AIM_ANGLE) pulse(-AIM_MOTOR_SPEED);
  }

  public void rotateDown() {
    if (joint != null && body.getAngle() < MAX_AIM_ANGLE) pulse(AIM_MOTOR_SPEED);
  }

  private void pulse(float speed) {
    joint.setMotorSpeed(speed);
    var handle = new Registration[1];
    handle[0] =
        scheduler.schedule(
            AIM_PULSE_MILLIS,
            () -> {
              if (joint != null) joint.setMotorSpeed(0);
              timers.remove(handle[0]);
            });
    timers.add(handle[0]);
  }

  public void stopRotation() {
    if (joint != null) joint.setMotorSpeed(0);
  }

  public void fireShell(double impulse) {
    if (body == null) return;
    Point2 tip = tank.cannonTip();
    var shell = new Projectile(tip.x(), tip.y(), tank);
    tank.addProjectile(shell);
    double angle = body.getAngle();
    if (tank.isMirrored()) angle = Math.PI - angle;
    var shellBody = shell.getPhysicsBody();
    var shotImpulse =
        new Vec2(
            (float) (Math.cos(angle) * impulse),
            (float) (Math.sin(angle) * impulse));
    shellBody.applyLinearImpulse(shotImpulse, shellBody.getWorldCenter());
  }

  public void removeFromPhysics() {
    timers.forEach(Registration::close);
    timers.clear();
    if (body != null) {
      body.getWorld().destroyBody(body);
      body = null;
    }
    joint = null;
  }

  public Body getPhysicsBody() {
    return body;
  }

  public Point2 getPivotOffset() {
    return pivot;
  }
}
