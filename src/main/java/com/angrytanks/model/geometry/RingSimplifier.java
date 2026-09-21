package com.angrytanks.model.geometry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.algorithm.Distance;
import org.locationtech.jts.geom.TopologyException;

final class RingSimplifier {
  private RingSimplifier() {}

  static List<Point2> simplify(List<Point2> ring, double tolerance) {
    if (!Double.isFinite(tolerance) || tolerance < 0)
      throw new GeometryException("simplification", "invalid tolerance");
    if (ring.size() <= 3 || tolerance == 0) return ring;
    Point2 firstPoint = ring.getFirst();
    int oppositeIndex = 1;
    for (int i = 2; i < ring.size(); i++) {
      double candidateDistance = firstPoint.distance(ring.get(i));
      double oppositeDistance = firstPoint.distance(ring.get(oppositeIndex));
      if (candidateDistance > oppositeDistance) {
        oppositeIndex = i;
      }
    }
    var chain = new ArrayList<>(ring);
    chain.add(firstPoint);
    var candidate = simplifyChain(chain.subList(0, oppositeIndex + 1), tolerance);
    candidate.removeLast();
    candidate.addAll(simplifyChain(chain.subList(oppositeIndex, chain.size()), tolerance));
    try {
      var result = PolygonRing.prepare(candidate);
      var originalBoundary = PolygonRing.polygon(ring).getBoundary();
      var simplifiedBoundary = PolygonRing.polygon(result).getBoundary();

      if (originalBoundary.buffer(tolerance).covers(simplifiedBoundary)
          && simplifiedBoundary.buffer(tolerance).covers(originalBoundary)) return result;
    } catch (GeometryException | TopologyException rejected) {
      return ring;
    }
    return ring;
  }

  private static ArrayList<Point2> simplifyChain(List<Point2> points, double tolerance) {
    boolean[] keep = new boolean[points.size()];
    keep[0] = true;
    keep[points.size() - 1] = true;
    var pending = new ArrayDeque<IndexRange>();
    pending.push(new IndexRange(0, points.size() - 1));
    while (!pending.isEmpty()) {
      var range = pending.pop();
      int startIndex = range.start();
      int endIndex = range.end();
      int farthestIndex = -1;
      double maximumDistance = tolerance;
      var segmentStart = PolygonRing.coordinate(points.get(startIndex));
      var segmentEnd = PolygonRing.coordinate(points.get(endIndex));
      for (int i = startIndex + 1; i < endIndex; i++) {
        double distance =
            Distance.pointToSegment(
                PolygonRing.coordinate(points.get(i)),
                segmentStart,
                segmentEnd);
        if (distance > maximumDistance) {
          maximumDistance = distance;
          farthestIndex = i;
        }
      }
      if (farthestIndex >= 0) {
        keep[farthestIndex] = true;
        pending.push(new IndexRange(startIndex, farthestIndex));
        pending.push(new IndexRange(farthestIndex, endIndex));
      }
    }
    var result = new ArrayList<Point2>();
    for (int i = 0; i < points.size(); i++) {
      if (keep[i]) {
        result.add(points.get(i));
      }
    }
    return result;
  }

  private record IndexRange(int start, int end) {}
}
