package com.angrytanks.util;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class ConvexDecomposer {

    // Small tolerance value for floating point comparisons.
    private static final double EPSILON = 1e-6;
    // Tolerance used during vertex simplification.
    private static final double SIMPLIFICATION_TOLERANCE = 0.5;

    /**
     * Decomposes the given polygon into convex parts.
     * Steps: simplify, normalize, triangulate (ear clipping), and merge triangles.
     *
     * @param polygon List of Point2D representing the polygon.
     * @return List of convex parts (each as a list of Point2D).
     */
    public static List<List<Point2D>> decompose(List<Point2D> polygon) {
        // Simplify polygon vertices to reduce complexity.
        List<Point2D> simplified = simplifyVertices(polygon, SIMPLIFICATION_TOLERANCE);
        // Remove duplicate endpoints if present.
        List<Point2D> normalized = normalizePolygon(simplified);

        List<List<Point2D>> triangles = new ArrayList<>();
        List<Point2D> verts = new ArrayList<>(normalized);
        if (verts.size() < 3) return triangles;

        // Triangulate using ear clipping method.
        while (verts.size() > 3) {
            boolean earFound = false;
            for (int i = 0; i < verts.size(); i++) {
                int prevIndex = (i - 1 + verts.size()) % verts.size();
                int nextIndex = (i + 1) % verts.size();
                Point2D prev = verts.get(prevIndex);
                Point2D curr = verts.get(i);
                Point2D next = verts.get(nextIndex);
                // Check if current vertex forms a convex angle.
                if (isConvex(prev, curr, next)) {
                    boolean contains = false;
                    // Verify no other vertex lies within the ear triangle.
                    for (int j = 0; j < verts.size(); j++) {
                        if (j == prevIndex || j == i || j == nextIndex) continue;
                        if (pointInTriangle(verts.get(j), prev, curr, next)) {
                            contains = true;
                            break;
                        }
                    }
                    // If valid ear found, cut it out and add the triangle.
                    if (!contains) {
                        List<Point2D> ear = new ArrayList<>();
                        ear.add(prev);
                        ear.add(curr);
                        ear.add(next);
                        triangles.add(ear);
                        verts.remove(i);
                        earFound = true;
                        break;
                    }
                }
            }
            if (!earFound) {
                // Break if no valid ear is found to prevent infinite loop.
                break;
            }
        }
        // Add the remaining triangle if three vertices remain.
        if (verts.size() == 3) {
            triangles.add(new ArrayList<>(verts));
        }
        // Merge adjacent triangles into larger convex pieces.
        List<List<Point2D>> merged = mergeTriangles(triangles);

        return merged;
    }

    /**
     * Removes duplicate last point if it's the same as the first.
     *
     * @param polygon List of Point2D.
     * @return Normalized polygon with no redundant endpoint.
     */
    public static List<Point2D> normalizePolygon(List<Point2D> polygon) {
        List<Point2D> normalized = new ArrayList<>(polygon);
        if (normalized.size() > 1 && normalized.get(0).equals(normalized.get(normalized.size() - 1))) {
            normalized.remove(normalized.size() - 1);
        }
        return normalized;
    }

    /**
     * Simplifies a series of points using a tolerance threshold (similar to RDP algorithm).
     *
     * @param points List of Point2D.
     * @param tolerance Maximum allowed deviation.
     * @return Simplified list of points.
     */
    public static List<Point2D> simplifyVertices(List<Point2D> points, double tolerance) {
        if (points.size() < 3) return points;
        int index = -1;
        double maxDistance = 0;
        // Find the point with maximum distance from the line between endpoints.
        for (int i = 1; i < points.size() - 1; i++) {
            double distance = perpendicularDistance(points.get(i), points.get(0), points.get(points.size() - 1));
            if (distance > maxDistance) {
                maxDistance = distance;
                index = i;
            }
        }
        // If deviation is too high, recursively simplify segments.
        if (maxDistance > tolerance) {
            List<Point2D> recResults1 = simplifyVertices(points.subList(0, index + 1), tolerance);
            List<Point2D> recResults2 = simplifyVertices(points.subList(index, points.size()), tolerance);
            List<Point2D> result = new ArrayList<>(recResults1);
            result.remove(result.size() - 1);
            result.addAll(recResults2);
            return result;
        } else {
            // If within tolerance, reduce to endpoints only.
            List<Point2D> result = new ArrayList<>();
            result.add(points.get(0));
            result.add(points.get(points.size() - 1));
            return result;
        }
    }

    /**
     * Calculates the perpendicular distance from point p to the line defined by lineStart and lineEnd.
     *
     * @param p The point to measure.
     * @param lineStart Start point of the line.
     * @param lineEnd End point of the line.
     * @return Perpendicular distance.
     */
    private static double perpendicularDistance(Point2D p, Point2D lineStart, Point2D lineEnd) {
        double dx = lineEnd.getX() - lineStart.getX();
        double dy = lineEnd.getY() - lineStart.getY();
        if (dx == 0 && dy == 0) return p.distance(lineStart);
        double numerator = Math.abs(dy * p.getX() - dx * p.getY()
                + lineEnd.getX() * lineStart.getY() - lineEnd.getY() * lineStart.getX());
        double denominator = Math.sqrt(dx * dx + dy * dy);
        return numerator / denominator;
    }

    /**
     * Checks if the sequence of points (a, b, c) makes a convex turn.
     *
     * @param a First point.
     * @param b Second point.
     * @param c Third point.
     * @return True if convex, false otherwise.
     */
    private static boolean isConvex(Point2D a, Point2D b, Point2D c) {
        double cross = (b.getX() - a.getX()) * (c.getY() - b.getY())
                - (b.getY() - a.getY()) * (c.getX() - b.getX());
        return cross < 0;
    }

    /**
     * Determines if point p is inside the triangle defined by points a, b, and c.
     *
     * @param p The point to test.
     * @param a Triangle vertex.
     * @param b Triangle vertex.
     * @param c Triangle vertex.
     * @return True if p is inside the triangle, false otherwise.
     */
    private static boolean pointInTriangle(Point2D p, Point2D a, Point2D b, Point2D c) {
        double areaOrig = Math.abs(triangleArea(a, b, c));
        double area1 = Math.abs(triangleArea(p, b, c));
        double area2 = Math.abs(triangleArea(a, p, c));
        double area3 = Math.abs(triangleArea(a, b, p));
        return Math.abs(areaOrig - (area1 + area2 + area3)) < 0.1;
    }

    /**
     * Computes the signed area of the triangle defined by points a, b, and c.
     *
     * @param a Triangle vertex.
     * @param b Triangle vertex.
     * @param c Triangle vertex.
     * @return The area of the triangle.
     */
    private static double triangleArea(Point2D a, Point2D b, Point2D c) {
        return 0.5 * (a.getX() * (b.getY() - c.getY())
                + b.getX() * (c.getY() - a.getY())
                + c.getX() * (a.getY() - b.getY()));
    }

    /**
     * Attempts to merge adjacent triangles into larger convex polygons.
     *
     * @param triangles List of triangles to merge.
     * @return List of merged convex polygons.
     */
    private static List<List<Point2D>> mergeTriangles(List<List<Point2D>> triangles) {
        boolean mergedSomething = true;
        while (mergedSomething) {
            mergedSomething = false;
            outer:
            for (int i = 0; i < triangles.size(); i++) {
                List<Point2D> polyA = triangles.get(i);
                for (int j = i + 1; j < triangles.size(); j++) {
                    List<Point2D> polyB = triangles.get(j);
                    List<Point2D> merged = tryMerge(polyA, polyB);
                    if (merged != null) {
                        // Remove the two merged parts and add the new merged polygon.
                        triangles.remove(j);
                        triangles.remove(i);
                        triangles.add(merged);
                        mergedSomething = true;
                        break outer;
                    }
                }
            }
        }
        return triangles;
    }

    /**
     * Checks if two polygons share a common edge and attempts to merge them.
     *
     * @param polyA First polygon.
     * @param polyB Second polygon.
     * @return Merged polygon if mergeable and convex, otherwise null.
     */
    private static List<Point2D> tryMerge(List<Point2D> polyA, List<Point2D> polyB) {
        for (int i = 0; i < polyA.size(); i++) {
            Point2D a1 = polyA.get(i);
            Point2D a2 = polyA.get((i + 1) % polyA.size());
            for (int j = 0; j < polyB.size(); j++) {
                Point2D b1 = polyB.get(j);
                Point2D b2 = polyB.get((j + 1) % polyB.size());
                // Check if an edge from polyA and polyB are the same (within EPSILON).
                if (a1.distance(b2) < EPSILON && a2.distance(b1) < EPSILON) {
                    List<Point2D> merged = new ArrayList<>();
                    int idx = i;
                    // Add vertices from polyA in order.
                    do {
                        merged.add(polyA.get(idx));
                        idx = (idx + 1) % polyA.size();
                    } while (idx != i);
                    // Add vertices from polyB, skipping the common edge.
                    idx = (j + 2) % polyB.size();
                    while (idx != j) {
                        merged.add(polyB.get(idx));
                        idx = (idx + 1) % polyB.size();
                    }
                    // Confirm merged polygon is convex.
                    if (isConvexPolygon(merged)) {
                        // Ensure polygon is closed by repeating the first vertex if necessary.
                        if (merged.get(0).distance(merged.get(merged.size() - 1)) > EPSILON) {
                            merged.add(merged.get(0));
                        }
                        return merged;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a polygon is convex by ensuring consistent cross product sign.
     *
     * @param poly List of Point2D forming the polygon.
     * @return True if convex, false otherwise.
     */
    public static boolean isConvexPolygon(List<Point2D> poly) {
        if (poly.size() < 3) return false;
        boolean signSet = false;
        boolean sign = false;
        int n = poly.size();
        for (int i = 0; i < n; i++) {
            Point2D a = poly.get(i);
            Point2D b = poly.get((i + 1) % n);
            Point2D c = poly.get((i + 2) % n);
            double cross = (b.getX() - a.getX()) * (c.getY() - b.getY())
                    - (b.getY() - a.getY()) * (c.getX() - b.getX());
            if (Math.abs(cross) < EPSILON) continue;
            if (!signSet) {
                sign = cross > 0;
                signSet = true;
            } else if ((cross > 0) != sign) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a single JavaFX Shape by merging convex parts.
     * Each convex part is converted to a Polygon and unioned together.
     *
     * @param parts List of convex parts (each as a list of Point2D).
     * @param fill Color to fill the shape.
     * @return Merged JavaFX Shape.
     */
    public static Shape mergeConvexParts(List<List<Point2D>> parts, Color fill) {
        Shape merged = null;
        for (List<Point2D> part : parts) {
            Polygon poly = new Polygon();
            for (Point2D pt : part) {
                poly.getPoints().addAll(pt.getX(), pt.getY());
            }
            poly.setFill(fill);
            poly.setStroke(null);
            if (merged == null) {
                merged = poly;
            } else {
                merged = javafx.scene.shape.Shape.union(merged, poly);
                merged.setFill(fill);
            }
        }
        if (merged == null) {
            merged = new Polygon();
            merged.setFill(fill);
        }
        return merged;
    }
}
