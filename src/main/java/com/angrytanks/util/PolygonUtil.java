package com.angrytanks.util;

import javafx.geometry.Point2D;
import java.util.Collections;
import java.util.List;

public class PolygonUtil {

    /**
     * Computes the signed area of a polygon.
     * <p>
     * The area is calculated using the shoelace formula. The sign of the area indicates the
     * winding order of the polygon (positive means counterclockwise, negative means clockwise).
     *
     * @param polygon List of Point2D points representing the vertices of the polygon.
     * @return The signed area of the polygon.
     */
    public static double signedArea(List<Point2D> polygon) {
        double sum = 0;
        // Iterate through each vertex and the next (wrapping around at the end)
        for (int i = 0; i < polygon.size(); i++) {
            Point2D current = polygon.get(i);
            Point2D next = polygon.get((i + 1) % polygon.size());
            // Apply the shoelace formula step for current pair of vertices.
            sum += (current.getX() * next.getY()) - (next.getX() * current.getY());
        }
        // Divide by 2 to get the actual area
        return sum / 2;
    }

    /**
     * Ensures that the polygon vertices are in clockwise order.
     * <p>
     * The signed area is used to determine the current winding order.
     * If the signed area is positive (indicating counterclockwise order), the list of points
     * is reversed to make it clockwise.
     *
     * @param polygon List of Point2D points representing the vertices of the polygon.
     */
    public static void ensureClockwiseOrder(List<Point2D> polygon) {
        // If the signed area is positive, the vertices are in counterclockwise order.
        if (signedArea(polygon) > 0) {
            // Reverse the list to enforce a clockwise order.
            Collections.reverse(polygon);
        }
    }
}
