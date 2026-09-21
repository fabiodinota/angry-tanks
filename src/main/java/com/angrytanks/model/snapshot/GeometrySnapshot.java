package com.angrytanks.model.snapshot;

import com.angrytanks.model.geometry.Point2;
import java.util.List;

public record GeometrySnapshot(
    long revision, List<Point2> outline, List<List<Point2>> parts, String fill) {
  public GeometrySnapshot {
    outline = List.copyOf(outline);
    parts = parts.stream().map(List::copyOf).toList();
  }
}
