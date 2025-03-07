package com.angrytanks.world;

import com.angrytanks.util.MyContactListener;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

    private static PhysicsWorld instance;
    private final World physicsWorld;
    private static final List<Runnable> pendingActions = new ArrayList<>();

    public PhysicsWorld() {
        instance = this;
        this.physicsWorld = new World(new Vec2(0, 9.8f));
        this.physicsWorld.setContactListener(new MyContactListener());
    }

    public void update() {
        physicsWorld.step(1 / 60f, 8, 3);
        for (Runnable r : pendingActions) {
            r.run();
        }
        pendingActions.clear();
    }

    public static void scheduleAction(Runnable action) {
        pendingActions.add(action);
    }

    public static World getPhysicsWorld() {
        return instance.physicsWorld;
    }
}
