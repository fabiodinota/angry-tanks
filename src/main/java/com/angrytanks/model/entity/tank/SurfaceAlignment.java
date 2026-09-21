package com.angrytanks.model.entity.tank;

import com.angrytanks.model.physics.PhysicsUnits;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

final class SurfaceAlignment {
  private static final float VERTICAL_OFFSET = 0.4f;
  private static final float RAY_LENGTH = 0.2f;
  private static final float CORRECTION_FACTOR = 0.1f;

  public static void align(Tank tank, World physicsWorld) {
    Body tankBody = tank.getHull().getPhysicsBody();
    Tank.HullBounds bounds = tank.getHullBounds();
    float hullWidthMeters = (float) (bounds.width / PhysicsUnits.PIXELS_PER_METER);
    float hullHeightMeters =
        (float) (tank.getHull().getHullHeight() / PhysicsUnits.PIXELS_PER_METER);
    float centerOffsetX = (float) (bounds.centerX / PhysicsUnits.PIXELS_PER_METER);
    float halfTankWidth = hullWidthMeters / 2.0f;

    Vec2 leftLocal = new Vec2(-halfTankWidth + centerOffsetX, hullHeightMeters + VERTICAL_OFFSET);
    Vec2 rightLocal = new Vec2(halfTankWidth + centerOffsetX, hullHeightMeters + VERTICAL_OFFSET);

    float angle = tankBody.getAngle();
    Vec2 leftOffset = rotate(leftLocal, angle);
    Vec2 rightOffset = rotate(rightLocal, angle);

    Vec2 tankPosition = tankBody.getPosition();
    Vec2 leftRayStart = tankPosition.add(leftOffset);
    Vec2 rightRayStart = tankPosition.add(rightOffset);

    if (tank.isMirrored()) {
      Vec2 temp = leftRayStart;
      leftRayStart = rightRayStart;
      rightRayStart = temp;
    }

    Vec2 rayDown = new Vec2(0, RAY_LENGTH);
    Vec2 leftRayEnd = leftRayStart.add(rayDown);
    Vec2 rightRayEnd = rightRayStart.add(rayDown);

    final Vec2[] leftHit = {null};
    final Vec2[] rightHit = {null};

    physicsWorld.raycast(
        (fixture, point, normal, fraction) -> {
          leftHit[0] = point;
          return fraction;
        },
        leftRayStart,
        leftRayEnd);

    physicsWorld.raycast(
        (fixture, point, normal, fraction) -> {
          rightHit[0] = point;
          return fraction;
        },
        rightRayStart,
        rightRayEnd);

    if (leftHit[0] != null && rightHit[0] != null) {
      Vec2 groundVector = rightHit[0].sub(leftHit[0]);
      float desiredAngle = (float) Math.atan2(groundVector.y, groundVector.x);
      float currentAngle = tankBody.getAngle();
      float angleDifference = desiredAngle - currentAngle;
      angleDifference = (float) Math.atan2(Math.sin(angleDifference), Math.cos(angleDifference));

      if (tank.isMirrored()) {
        angleDifference *= -1;
      }

      tankBody.setAngularVelocity(angleDifference * CORRECTION_FACTOR);
      tankBody.setFixedRotation(true);
    } else {
      tankBody.setFixedRotation(false);
    }
  }

  private static Vec2 rotate(Vec2 vector, float angle) {
    float cosine = (float) Math.cos(angle);
    float sine = (float) Math.sin(angle);
    return new Vec2(vector.x * cosine - vector.y * sine, vector.x * sine + vector.y * cosine);
  }
}
