package com.angrytanks.util;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PathFollower {
    private final List<Point2D> points;
    private final int numSegments;

    public PathFollower(List<Point2D> points) {
        if (points.size() < 2) throw new IllegalArgumentException("Need at least 2 points");
        this.points = points;
        this.numSegments = points.size() - 1;
    }

    // param in [0,1]
    public Point2D getPosition(double param) {
        param = Math.max(0, Math.min(param, 1));
        double segmentParam = param * numSegments;
        int i = (int) segmentParam;
        double t = segmentParam - i;

        // clamp segments
        Point2D p0 = points.get(Math.max(i - 1, 0));
        Point2D p1 = points.get(i);
        Point2D p2 = points.get(Math.min(i + 1, points.size() - 1));
        Point2D p3 = points.get(Math.min(i + 2, points.size() - 1));

        // Catmull-Rom spline interpolation
        return catmullRomInterpolate(p0, p1, p2, p3, t);
    }

    public double getAngle(double param) {
        double eps = 0.001;
        Point2D before = getPosition(Math.max(0, param - eps));
        Point2D after  = getPosition(Math.min(1, param + eps));
        return Math.atan2(after.getY() - before.getY(), after.getX() - before.getX());
    }

    private Point2D catmullRomInterpolate(Point2D p0, Point2D p1, Point2D p2, Point2D p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double x = 0.5 * ((2 * p1.getX()) +
                (-p0.getX() + p2.getX()) * t +
                (2*p0.getX() - 5*p1.getX() + 4*p2.getX() - p3.getX()) * t2 +
                (-p0.getX() + 3*p1.getX() - 3*p2.getX() + p3.getX()) * t3);

        double y = 0.5 * ((2 * p1.getY()) +
                (-p0.getY() + p2.getY()) * t +
                (2*p0.getY() - 5*p1.getY() + 4*p2.getY() - p3.getY()) * t2 +
                (-p0.getY() + 3*p1.getY() - 3*p2.getY() + p3.getY()) * t3);

        return new Point2D(x, y);
    }
}