package com.angrytanks.model.entity.tank;

import com.angrytanks.model.api.DelayScheduler;
import com.angrytanks.model.assets.TankData;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.physics.PhysicsUnits;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public final class TankTurret {
  private static final float MAX_MOTOR_TORQUE = 1900;
  private static final float LOWER_JOINT_ANGLE = (float) Math.toRadians(-45);
  private static final float UPPER_JOINT_ANGLE = (float) Math.toRadians(45);
  private final TankCannon cannon;
  private final Tank parent;
  private final Point2 position;
  private Body body;

  public TankTurret(TankData data, Tank tank, DelayScheduler scheduler) {
    parent = tank;
    position = data.turretVertices().getFirst();
    cannon = new TankCannon(data.cannonVertices(), data.cannonAnchor().getFirst(), tank, scheduler);
  }

  public void addToPhysics(World world) {
    if (body != null) return;
    BodyDef bodyDefinition = new BodyDef();
    bodyDefinition.type = BodyType.STATIC;
    bodyDefinition.position.set(
        (float) (position.x() / PhysicsUnits.PIXELS_PER_METER),
        (float) (position.y() / PhysicsUnits.PIXELS_PER_METER));
    body = world.createBody(bodyDefinition);
    body.setUserData(this);
    cannon.addToPhysics(world);
    RevoluteJointDef jointDefinition = new RevoluteJointDef();
    jointDefinition.bodyA = body;
    jointDefinition.bodyB = cannon.getPhysicsBody();
    Point2 pivot = cannon.getPivotOffset();
    jointDefinition.localAnchorA.set(
        (float) (pivot.x() / PhysicsUnits.PIXELS_PER_METER),
        (float) (pivot.y() / PhysicsUnits.PIXELS_PER_METER));
    jointDefinition.localAnchorB.set(0, 0);
    jointDefinition.enableMotor = true;
    jointDefinition.motorSpeed = 0;
    jointDefinition.maxMotorTorque = MAX_MOTOR_TORQUE;
    jointDefinition.lowerAngle = LOWER_JOINT_ANGLE;
    jointDefinition.upperAngle = UPPER_JOINT_ANGLE;
    jointDefinition.enableLimit = true;
    cannon.setCannonJoint((RevoluteJoint) world.createJoint(jointDefinition));
    body.setUserData(parent);
  }

  public void rotateCannonUp() {
    cannon.rotateUp();
  }

  public void rotateCannonDown() {
    cannon.rotateDown();
  }

  public TankCannon getCannon() {
    return cannon;
  }

  public Body getPhysicsBody() {
    return body;
  }

  void translatePhysics(double offsetX, double offsetY) {
    Vec2 delta =
        new Vec2(
            (float) (offsetX / PhysicsUnits.PIXELS_PER_METER),
            (float) (offsetY / PhysicsUnits.PIXELS_PER_METER));
    if (body != null) body.setTransform(body.getPosition().add(delta), body.getAngle());
    Body cannonBody = cannon.getPhysicsBody();
    if (cannonBody != null) {
      cannonBody.setTransform(cannonBody.getPosition().add(delta), cannonBody.getAngle());
    }
  }

  public void removeFromPhysics() {
    cannon.removeFromPhysics();
    if (body != null) {
      body.getWorld().destroyBody(body);
      body = null;
    }
  }
}
