package com.angrytanks.entity.custom.tank;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class TankController {

    private final Tank tank;

    private final float moveForce = 15f;
    private final float maxSpeed = 1f;

    public TankController(Tank tank) {
        this.tank = tank;
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
            System.out.println("TankController: Stopping horizontal movement");
        }
    }

    public void fire() {
        System.out.println("TankController: Fire!");
    }
}
