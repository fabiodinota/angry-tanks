package com.angrytanks.model.terrain;

import com.angrytanks.model.geometry.CraterParameters;
import com.angrytanks.model.geometry.Point2;
import java.util.List;

public final class Sand extends DestructibleTerrain {
  public Sand(List<Point2> vertices, String fill) {
    super(vertices, fill, new CraterParameters(40, 35, 0, 100), 1f, 0.5f);
  }
}
