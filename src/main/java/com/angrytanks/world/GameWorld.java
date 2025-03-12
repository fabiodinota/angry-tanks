package com.angrytanks.world;

import com.angrytanks.entity.Actor;

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

        actor.addToPhysics(physicsWorld.getPhysicsWorld());



        /* Old code used to add actors to physics world
        /* replaced with direct call of the actor self addToPhysics method
        if (actor instanceof Tank) {
            ((Tank) actor).addToPhysics(physicsWorld.getPhysicsWorld());
        } else if (actor instanceof Landscape) {
            ((Landscape) actor).addToPhysics(physicsWorld.getPhysicsWorld());
        }
        */
    }

    public void clearActors() {
        actors.clear();
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
