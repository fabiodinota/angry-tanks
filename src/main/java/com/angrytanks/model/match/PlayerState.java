package com.angrytanks.model.match;

import com.angrytanks.model.entity.tank.Tank;
import java.util.Objects;

public final class PlayerState {
  public static final int MAX_HEALTH = 100;
  private final String name;
  private final Tank tank;
  private int health = MAX_HEALTH;

  public PlayerState(String name, Tank tank) {
    this.name = Objects.requireNonNull(name);
    this.tank = Objects.requireNonNull(tank);
  }

  public String getName() {
    return name;
  }

  public Tank getTank() {
    return tank;
  }

  public int getHealth() {
    return health;
  }

  public void damage(int amount) {
    if (amount < 0) throw new IllegalArgumentException("Damage cannot be negative");
    health = Math.max(0, health - amount);
  }
}
