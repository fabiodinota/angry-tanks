package com.angrytanks.model.entity.tank;

import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.snapshot.GeometrySnapshot;
import com.angrytanks.model.snapshot.TankAppearance;

final class TankKinematics {
  private TankKinematics() {}

  static Point2 pivot(TankAppearance asset, double cannonAngle, boolean hasTurret) {
    Bounds bounds = new Bounds();
    bounds.add(asset.hull());
    bounds.add(asset.tracks());
    bounds.add(asset.decor());
    asset.wheels().forEach(bounds::add);
    if (hasTurret) {
      bounds.add(asset.turret());
      for (Point2 point : asset.cannon().outline()) {
        Point2 rotatedPoint =
            rotate(
                new Point2((float) point.x(), (float) point.y()), asset.cannonPivot(), cannonAngle);
        bounds.add((float) rotatedPoint.x(), (float) rotatedPoint.y());
      }
    }
    return new Point2((bounds.minX + bounds.maxX) / 2, (bounds.minY + bounds.maxY) / 2);
  }

  static Point2 tip(
      TankAppearance asset,
      double x,
      double y,
      double hullAngle,
      double cannonAngle,
      boolean mirrored) {
    Point2 tip = Point2.ZERO;
    double extremeX = mirrored ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    for (Point2 point : asset.cannon().outline()) {
      if (mirrored ? point.x() < extremeX : point.x() > extremeX) {
        extremeX = point.x();
        tip = point;
      }
    }
    Point2 anchor = asset.cannonPivot();
    if (mirrored) tip = new Point2(anchor.x() - (tip.x() - anchor.x()), tip.y());
    tip = rotate(tip, anchor, cannonAngle);
    Point2 pivot = pivot(asset, cannonAngle, true);
    if (mirrored) tip = new Point2(2 * pivot.x() - tip.x(), tip.y());
    tip = rotate(tip, pivot, hullAngle);
    return new Point2(x + tip.x(), y + tip.y());
  }

  private static Point2 rotate(Point2 point, Point2 pivot, double angle) {
    double cosine = Math.cos(angle);
    double sine = Math.sin(angle);
    double offsetX = point.x() - pivot.x();
    double offsetY = point.y() - pivot.y();
    return new Point2(
        pivot.x() + cosine * offsetX - sine * offsetY,
        pivot.y() + sine * offsetX + cosine * offsetY);
  }

  private static final class Bounds {
    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;

    void add(double x, double y) {
      minX = Math.min(minX, x);
      minY = Math.min(minY, y);
      maxX = Math.max(maxX, x);
      maxY = Math.max(maxY, y);
    }

    void add(GeometrySnapshot geometry) {
      for (Point2 point : geometry.outline()) {
        add((float) point.x(), (float) point.y());
      }
    }
  }
}
