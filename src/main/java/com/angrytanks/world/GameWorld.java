package com.angrytanks.world;

import com.angrytanks.entity.custom.Tank;
import java.util.ArrayList;
import java.util.List;


public class GameWorld {

    private final PhysicsWorld physicsWorld;
    private final List<Tank> objects;
    private final List<Landscape> landscapes;

    public GameWorld() {
        this.physicsWorld = new PhysicsWorld();
        this.objects = new ArrayList<>();
        this.landscapes = new ArrayList<>();
    }


    public void addObject(Tank tank) {
        objects.add(tank);
        tank.addToPhysics(physicsWorld.getPhysicsWorld());
    }

    public void addLandscape(Landscape landscape) {
        landscapes.add(landscape);
        landscape.addToPhysics(physicsWorld.getPhysicsWorld());
    }

    public void update() {
        physicsWorld.update();
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public List<Tank> getObjects() {
        return objects;
    }

    public List<Landscape> getLandscapes() {
        return landscapes;
    }
}
