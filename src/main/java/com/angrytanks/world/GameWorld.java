package com.angrytanks.world;

import com.angrytanks.entity.Actor;
import com.angrytanks.entity.custom.Tank;
import java.util.ArrayList;
import java.util.List;


public class GameWorld {

    private final PhysicsWorld physicsWorld;
    private final List<Actor> actors;

    public GameWorld() {
        this.physicsWorld = new PhysicsWorld();
        this.actors = new ArrayList<>();
    }


    public void addActor(Actor actor) {
        actors.add(actor);

        if (actor instanceof Tank) {
            ((Tank) actor).addToPhysics(physicsWorld.getPhysicsWorld());
        } else if (actor instanceof Landscape) {
            ((Landscape) actor).addToPhysics(physicsWorld.getPhysicsWorld());
        }

    }




    public void update() {
        physicsWorld.update();
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public List<Actor> getAllActors() {
        return actors;
    }
}
