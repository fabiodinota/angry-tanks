package com.angrytanks.model.snapshot;

import java.util.Objects;

public record EntitySnapshot(
    long id,
    Kind kind,
    String assetId,
    double x,
    double y,
    double angle,
    boolean mirrored,
    double cannonAngle,
    boolean turretPresent,
    double pivotX,
    double pivotY,
    GeometrySnapshot geometry,
    TankAppearance tankAppearance) {
  public EntitySnapshot {
    Objects.requireNonNull(kind, "entity kind");
    switch (kind) {
      case TANK -> Objects.requireNonNull(tankAppearance, "tank appearance");
      case TERRAIN, DECORATION -> Objects.requireNonNull(geometry, "entity geometry");
      case PROJECTILE -> Objects.requireNonNull(assetId, "projectile asset");
    }
  }

  public static EntitySnapshot forTank(
      long id,
      String assetId,
      double x,
      double y,
      double angle,
      boolean mirrored,
      double cannonAngle,
      boolean turretPresent,
      double pivotX,
      double pivotY,
      TankAppearance appearance) {
    return new EntitySnapshot(
        id,
        Kind.TANK,
        assetId,
        x,
        y,
        angle,
        mirrored,
        cannonAngle,
        turretPresent,
        pivotX,
        pivotY,
        null,
        appearance);
  }

  public static EntitySnapshot forProjectile(
      long id, String assetId, double x, double y, double angle) {
    return new EntitySnapshot(
        id, Kind.PROJECTILE, assetId, x, y, angle, false, 0, false, 0, 0, null, null);
  }

  public static EntitySnapshot forTerrain(long id, GeometrySnapshot geometry) {
    return new EntitySnapshot(id, Kind.TERRAIN, "", 0, 0, 0, false, 0, false, 0, 0, geometry, null);
  }

  public static EntitySnapshot forDecoration(long id, GeometrySnapshot geometry) {
    return new EntitySnapshot(
        id, Kind.DECORATION, "", 0, 0, 0, false, 0, false, 0, 0, geometry, null);
  }

  public enum Kind {
    TANK,
    PROJECTILE,
    TERRAIN,
    DECORATION
  }
}
