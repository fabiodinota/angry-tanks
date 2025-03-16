package com.angrytanks.entity.custom.tank;

import com.angrytanks.core.GameState;
import com.angrytanks.entity.custom.Projectile;
import com.angrytanks.entity.custom.tank.components.TankCannon;
import javafx.geometry.Point2D;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class TankController {

    private Tank tank;
    private boolean canFire = true;
    private final float moveForce = 30f;
    private final float maxSpeed = 0.8f;

    public TankController(Tank tank) {
        this.tank = tank;
    }

    public void setTank(Tank newTank) {
        this.tank = newTank;
        canFire = true;
    }

    public void moveLeft() {
        Body body = tank.getHull().getPhysicsBody();
        if (body != null) {
            body.setAwake(true);
            if (body.getLinearVelocity().x > -maxSpeed) {
                Vec2 force = new Vec2(-moveForce, 0);
                body.applyForceToCenter(force);
            }
        }
    }

    public void moveRight() {
        Body body = tank.getHull().getPhysicsBody();
        if (body != null) {
            body.setAwake(true);
            if (body.getLinearVelocity().x < maxSpeed) {
                Vec2 force = new Vec2(moveForce, 0);
                body.applyForceToCenter(force);
            }
        }
    }

    public void stop() {
        if (tank.getHull() != null && tank.getHull().getPhysicsBody() != null) {
            Vec2 vel = tank.getHull().getPhysicsBody().getLinearVelocity();
            tank.getHull().getPhysicsBody().setLinearVelocity(new Vec2(0, vel.y));
        }
    }


    public void turretUp() {

        if (tank.getTurret() != null) {
            tank.getTurret().rotateCannonUp();
        }
    }

    public void turretDown() {
        if (tank.getTurret() != null) {
            tank.getTurret().rotateCannonDown();
        }
    }
    public void fire() {
        if (!canFire) {
            System.out.println("Tank already fired this turn.");
            return;
        }

        TankCannon cannon = tank.getTurret().getCannon();

        cannon.fireShell(10);
        canFire = false;
    }

    public Tank getTank() {
        return tank;
    }
}
