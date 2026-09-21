package com.angrytanks.model.geometry;

public record CraterParameters(
    double radiusX, double radiusY, double rotation, int ellipseVertices) {
  public CraterParameters {
    if (!Double.isFinite(radiusX)
        || !Double.isFinite(radiusY)
        || !Double.isFinite(rotation)
        || radiusX <= 0
        || radiusY <= 0
        || ellipseVertices < 3)
      throw new IllegalArgumentException(
          "Crater requires positive radii, finite rotation and at least three vertices");
  }
}
