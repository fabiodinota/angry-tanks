package com.angrytanks.model.world;

import com.angrytanks.model.entity.Actor;
import com.angrytanks.model.physics.PhysicsWorld;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameWorld implements AutoCloseable {
  private final PhysicsWorld physicsWorld = new PhysicsWorld();
  private final List<Actor> actors = new ArrayList<>();
  private final List<Actor> actorView = Collections.unmodifiableList(actors);
  private boolean closed;
  private long nextId = 1;

  public void addActor(Actor actor) {
    if (closed) throw new IllegalStateException("World is closed");
    if (actors.contains(actor)) return;
    actor.addToPhysics(physicsWorld.rawWorld());
    actor.assignId(nextId++);
    actors.add(actor);
  }

  public void update() {
    if (closed) return;
    physicsWorld.update();
    if (!closed) actors.forEach(Actor::updateFromPhysics);
  }

  public PhysicsWorld getPhysicsWorld() {
    return physicsWorld;
  }

  public List<Actor> getAllActors() {
    return actorView;
  }

  public void removeActor(Actor actor) {
    if (actors.contains(actor)) {
      actor.removeFromPhysics();
      actors.remove(actor);
    }
  }

  @Override
  public void close() {
    if (closed) return;
    for (Actor actor : List.copyOf(actors)) removeActor(actor);
    physicsWorld.close();
    closed = true;
  }
}
