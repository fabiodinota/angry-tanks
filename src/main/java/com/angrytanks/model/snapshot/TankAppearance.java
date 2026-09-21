package com.angrytanks.model.snapshot;

import com.angrytanks.model.geometry.Point2;
import java.util.List;

public record TankAppearance(
    GeometrySnapshot hull,
    GeometrySnapshot turret,
    GeometrySnapshot cannon,
    GeometrySnapshot tracks,
    GeometrySnapshot decor,
    List<GeometrySnapshot> wheels,
    Point2 cannonPivot) {
  public TankAppearance {
    wheels = List.copyOf(wheels);
  }
}
