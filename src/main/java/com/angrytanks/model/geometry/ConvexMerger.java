package com.angrytanks.model.geometry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

final class ConvexMerger {
  private ConvexMerger() {}

  static List<List<Point2>> merge(List<List<Point2>> input) {
    if (!input.stream().allMatch(DecompositionValidator::validPart))
      throw new GeometryException("merging", "input contains a nonconvex or degenerate piece");
    var parts = new ArrayList<>(input.stream().map(PolygonRing::canonical).toList());
    boolean changed;
    do {
      changed = false;
      var adjacent = findAdjacentPairs(parts);
      for (var pair : adjacent) {
        var firstOwner = pair.first();
        var secondOwner = pair.second();
        var merged =
            join(
                parts.get(firstOwner.polygonIndex()),
                firstOwner.edgeIndex(),
                parts.get(secondOwner.polygonIndex()),
                secondOwner.edgeIndex());
        if (merged == null) continue;
        parts.set(firstOwner.polygonIndex(), merged);
        parts.remove(secondOwner.polygonIndex());
        changed = true;
        break;
      }
    } while (changed);
    return DecompositionValidator.immutable(parts);
  }

  private static List<Pair> findAdjacentPairs(List<List<Point2>> parts) {
    var edges = new LinkedHashMap<Edge, Owner>();
    var adjacent = new ArrayList<Pair>();
    for (int i = 0; i < parts.size(); i++) {
      var part = parts.get(i);
      for (int j = 0; j < part.size(); j++) {
        var edge = new Edge(part.get(j), part.get((j + 1) % part.size()));
        var other = edges.get(new Edge(edge.to(), edge.from()));
        var owner = new Owner(i, j);
        if (other != null) adjacent.add(new Pair(other, owner));
        edges.put(edge, owner);
      }
    }
    return adjacent;
  }

  private static List<Point2> join(
      List<Point2> firstPart, int firstEdge, List<Point2> secondPart, int secondEdge) {
    if (firstPart.size() + secondPart.size() - 2 > DecompositionValidator.MAX_VERTICES) return null;
    var boundary = new ArrayList<Point2>();

    for (int step = 1; step <= firstPart.size(); step++)
      boundary.add(firstPart.get((firstEdge + step) % firstPart.size()));
    for (int step = 2; step < secondPart.size(); step++)
      boundary.add(secondPart.get((secondEdge + step) % secondPart.size()));
    if (!DecompositionValidator.validPart(boundary)) return null;

    var mergedPolygon = PolygonRing.polygon(boundary);
    double expectedArea =
        PolygonRing.polygon(firstPart).getArea() + PolygonRing.polygon(secondPart).getArea();
    if (Math.abs(expectedArea - mergedPolygon.getArea())
        > DecompositionValidator.areaTolerance(mergedPolygon)) return null;
    return PolygonRing.canonical(boundary);
  }

  private record Edge(Point2 from, Point2 to) {}

  private record Owner(int polygonIndex, int edgeIndex) {}

  private record Pair(Owner first, Owner second) {}
}
