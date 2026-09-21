package com.angrytanks.model.entity;

import com.angrytanks.model.geometry.ConvexDecomposer;
import com.angrytanks.model.geometry.GeometryException;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.snapshot.EntitySnapshot;
import com.angrytanks.model.snapshot.GeometrySnapshot;
import java.util.List;

public final class Decoration extends Actor {
  private final GeometrySnapshot geometry;

  public Decoration(double x, double y, List<Point2> points, String fill) {
    super(x, y);
    List<List<Point2>> parts;
    try {
      parts = ConvexDecomposer.decompose(points);
    } catch (GeometryException invalid) {
      if (!invalid.stage().equals("input")) throw invalid;

      parts = List.of();
    }
    geometry = new GeometrySnapshot(0, points, parts, fill);
  }

  @Override
  public EntitySnapshot snapshot() {
    return EntitySnapshot.forDecoration(getId(), geometry);
  }
}
