package com.angrytanks.model.entity;

import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.snapshot.EntitySnapshot;
import org.jbox2d.dynamics.World;

public abstract class Actor {
  protected Point2 position;
  private long id;

  protected Actor(double x, double y) {
    position = new Point2(x, y);
  }

  public Point2 getPosition() {
    return position;
  }

  public long getId() {
    return id;
  }

  public void assignId(long id) {
    if (this.id != 0) throw new IllegalStateException("Already registered");
    this.id = id;
  }

  public void updateFromPhysics() {}

  public void addToPhysics(World world) {}

  public void removeFromPhysics() {}

  public abstract EntitySnapshot snapshot();
}
