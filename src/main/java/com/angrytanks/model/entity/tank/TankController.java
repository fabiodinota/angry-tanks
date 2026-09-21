package com.angrytanks.model.entity.tank;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public final class TankController {
  private static final float MOVE_FORCE = 30f;
  private static final float MAX_SPEED = 0.8f;
  private static final double SHOT_IMPULSE = 10;

  private TankController() {}

  public static void moveLeft(Tank tank) {
    move(tank, -1);
  }

  public static void moveRight(Tank tank) {
    move(tank, 1);
  }

  private static void move(Tank tank, int direction) {
    Body body = tank.getHull().getPhysicsBody();
    if (body == null) return;
    body.setAwake(true);
    if (direction * body.getLinearVelocity().x < MAX_SPEED)
      body.applyForceToCenter(new Vec2(direction * MOVE_FORCE, 0));
  }

  public static void stop(Tank tank) {
    Body body = tank.getHull().getPhysicsBody();
    if (body != null) body.setLinearVelocity(new Vec2(0, body.getLinearVelocity().y));
  }

  public static void turretUp(Tank tank) {
    TankTurret turret = tank.getTurret();
    if (turret != null) {
      turret.rotateCannonUp();
    }
  }

  public static void turretDown(Tank tank) {
    TankTurret turret = tank.getTurret();
    if (turret != null) {
      turret.rotateCannonDown();
    }
  }

  public static boolean fire(Tank tank) {
    TankTurret turret = tank.getTurret();
    if (turret == null) {
      return false;
    }
    turret.getCannon().fireShell(SHOT_IMPULSE);
    return true;
  }
}
