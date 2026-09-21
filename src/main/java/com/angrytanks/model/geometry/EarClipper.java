package com.angrytanks.model.geometry;

import java.util.ArrayList;
import java.util.List;

final class EarClipper {
  private EarClipper() {}

  static List<List<Point2>> triangulate(List<Point2> ring) {
    if (ring.isEmpty()) return List.of();
    int size = ring.size();
    int[] previous = new int[size];
    int[] next = new int[size];
    boolean[] active = new boolean[size];
    for (int i = 0; i < size; i++) {
      previous[i] = (i + size - 1) % size;
      next[i] = (i + 1) % size;
      active[i] = true;
    }
    var result = new ArrayList<List<Point2>>();
    int remainingVertices = size;
    int currentIndex = 0;
    int rejectedVertices = 0;
    while (remainingVertices > 3) {
      int previousIndex = previous[currentIndex];
      int nextIndex = next[currentIndex];
      if (isEar(ring, active, previous, next, currentIndex)) {
        result.add(List.of(ring.get(previousIndex), ring.get(currentIndex), ring.get(nextIndex)));
        active[currentIndex] = false;
        next[previousIndex] = nextIndex;
        previous[nextIndex] = previousIndex;
        remainingVertices--;
        rejectedVertices = 0;
      } else {
        rejectedVertices++;
        if (rejectedVertices >= remainingVertices) {
          throw new GeometryException(
              "triangulation", "no valid ear with " + remainingVertices + " vertices remaining");
        }
      }
      currentIndex = next[currentIndex];
    }
    int secondIndex = next[currentIndex];
    int thirdIndex = next[secondIndex];
    var finalTriangle =
        List.of(ring.get(currentIndex), ring.get(secondIndex), ring.get(thirdIndex));
    if (PolygonRing.turn(finalTriangle.get(0), finalTriangle.get(1), finalTriangle.get(2)) >= 0)
      throw new GeometryException("triangulation", "degenerate final triangle");
    result.add(finalTriangle);
    return result;
  }

  private static boolean isEar(
      List<Point2> ring,
      boolean[] active,
      int[] previous,
      int[] next,
      int currentIndex) {
    int previousIndex = previous[currentIndex];
    int nextIndex = next[currentIndex];
    var previousPoint = ring.get(previousIndex);
    var currentPoint = ring.get(currentIndex);
    var nextPoint = ring.get(nextIndex);
    if (PolygonRing.turn(previousPoint, currentPoint, nextPoint) >= 0) return false;
    for (int i = 0; i < ring.size(); i++) {
      if (!active[i] || i == previousIndex || i == currentIndex || i == nextIndex) continue;

      if (PolygonRing.turn(ring.get(previous[i]), ring.get(i), ring.get(next[i])) < 0) continue;
      var candidatePoint = ring.get(i);
      if (PolygonRing.turn(previousPoint, currentPoint, candidatePoint) <= 0
          && PolygonRing.turn(currentPoint, nextPoint, candidatePoint) <= 0
          && PolygonRing.turn(nextPoint, previousPoint, candidatePoint) <= 0) return false;
    }
    return true;
  }
}
