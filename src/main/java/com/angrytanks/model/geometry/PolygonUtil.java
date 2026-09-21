package com.angrytanks.model.geometry;

import java.util.Collections;
import java.util.List;

public final class PolygonUtil {
  private PolygonUtil() {}

  private static double signedArea(List<Point2> polygon) {
    double sum = 0;

    for (int i = 0; i < polygon.size(); i++) {
      Point2 current = polygon.get(i);
      Point2 next = polygon.get((i + 1) % polygon.size());

      sum += (current.x() * next.y()) - (next.x() * current.y());
    }

    return sum / 2;
  }

  public static void ensureClockwiseOrder(List<Point2> polygon) {

    if (signedArea(polygon) > 0) {

      Collections.reverse(polygon);
    }
  }
}
