package com.angrytanks.world;

import com.angrytanks.entity.Actor;
import javafx.scene.Group;
import javafx.scene.layout.Pane;

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

    public void removeActor(Actor actor) {
        actors.remove(actor);
        if (actor.getVisuals().getParent() instanceof Group) {
            ((Group) actor.getVisuals().getParent()).getChildren().remove(actor.getVisuals());
        } else if (actor.getVisuals().getParent() instanceof Pane) {
            ((Pane) actor.getVisuals().getParent()).getChildren().remove(actor.getVisuals());
        }
    }


}
