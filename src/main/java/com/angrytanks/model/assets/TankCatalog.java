package com.angrytanks.model.assets;

import java.util.List;

public final class TankCatalog {
  public static final List<String> NAMES =
      List.of("Yellow", "Blue", "Red", "Green", "Purple", "BMW", "Cyan", "Pink", "Dark Red");

  private TankCatalog() {}

  public static String resource(int index) {
    String name = NAMES.get(index);
    return "/tanks/" + (name.equals("Purple") ? "purple" : name) + ".svg";
  }

  public static int adjacent(int current, int other, int direction) {
    if (direction != 1 && direction != -1)
      throw new IllegalArgumentException("Direction must be -1 or 1");
    int next = current + direction;
    if (next == other) next += direction;
    return next >= 0 && next < NAMES.size() ? next : current;
  }
}
