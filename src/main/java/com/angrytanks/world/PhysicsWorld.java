package com.angrytanks.world;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


public class PhysicsWorld {

    private final World physicsWorld;

    public PhysicsWorld() {

        this.physicsWorld = new World(new Vec2(2, 9.8f));
    }


    public void update() {
        physicsWorld.step(1 / 60f, 8, 3);
    }


    public World getPhysicsWorld() {
        return physicsWorld;
    }
}
