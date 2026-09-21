package com.angrytanks.model.terrain;

import com.angrytanks.model.geometry.CraterParameters;
import com.angrytanks.model.geometry.Point2;
import java.util.List;

public final class Landscape extends DestructibleTerrain {
  public Landscape(List<Point2> vertices, String fill) {
    super(vertices, fill, new CraterParameters(35, 45, 0, 100), 0f, 0.9f);
  }
}
