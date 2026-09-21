package com.angrytanks.model.geometry;

public record Point2(double x, double y) {
  public static final Point2 ZERO = new Point2(0, 0);

  public double distance(Point2 other) {
    double dx = x - other.x;
    double dy = y - other.y;
    return Math.sqrt(dx * dx + dy * dy);
  }
}
