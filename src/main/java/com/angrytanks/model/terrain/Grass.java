package com.angrytanks.model.terrain;

import com.angrytanks.model.geometry.CraterParameters;
import com.angrytanks.model.geometry.Point2;
import java.util.List;

public final class Grass extends DestructibleTerrain {
  public Grass(List<Point2> vertices, String fill) {
    super(vertices, fill, new CraterParameters(30, 30, 0, 16), 1f, 0.5f);
  }
}
