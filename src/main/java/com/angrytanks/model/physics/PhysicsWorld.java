package com.angrytanks.model.physics;

import java.util.ArrayDeque;
import java.util.Objects;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public final class PhysicsWorld implements AutoCloseable {
  private static final int VELOCITY_ITERATIONS = 32;
  private static final int POSITION_ITERATIONS = 16;
  private static final float TIME_STEP = 1 / 60f;
  private final World physicsWorld = new World(new Vec2(0, 9.8f));
  private final ArrayDeque<Runnable> pendingActions = new ArrayDeque<>();
  private boolean closed;

  public void update() {
    if (closed) return;
    physicsWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

    while (!closed && !pendingActions.isEmpty()) {
      pendingActions.removeFirst().run();
    }
  }

  public void scheduleAction(Runnable action) {
    if (!closed) pendingActions.addLast(Objects.requireNonNull(action));
  }

  public World rawWorld() {
    return physicsWorld;
  }

  @Override
  public void close() {
    if (physicsWorld.isLocked())
      throw new IllegalStateException("Cannot close a world during a physics step");
    closed = true;
    pendingActions.clear();
    while (physicsWorld.getBodyList() != null) {
      physicsWorld.destroyBody(physicsWorld.getBodyList());
    }
  }
}
