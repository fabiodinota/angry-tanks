package com.angrytanks.util;

import javafx.geometry.Point2D;
import java.util.Collections;
import java.util.List;

public class PolygonUtil {
    public static double signedArea(List<Point2D> polygon) {
        double sum = 0;
        for (int i = 0; i < polygon.size(); i++) {
            Point2D current = polygon.get(i);
            Point2D next = polygon.get((i + 1) % polygon.size());
            sum += (current.getX() * next.getY()) - (next.getX() * current.getY());

        }
        return sum / 2;
    }

    public static void ensureClockwiseOrder(List<Point2D> polygon) {
        if (signedArea(polygon) > 0) {
            Collections.reverse(polygon);
        }
    }
}
