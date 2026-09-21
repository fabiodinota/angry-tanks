package com.angrytanks.model.assets;

import com.angrytanks.model.geometry.Point2;
import java.util.ArrayList;
import java.util.List;

public record TankData(
    List<Point2> hullVertices,
    String hullColor,
    List<Point2> trackVertices,
    String trackColor,
    List<List<Point2>> wheelVertices,
    String wheelColor,
    List<Point2> decorVertices,
    String decorColor,
    List<Point2> turretVertices,
    String turretColor,
    List<Point2> cannonVertices,
    String cannonColor,
    List<Point2> cannonAnchor) {
  public TankData {
    hullVertices = List.copyOf(hullVertices);
    trackVertices = List.copyOf(trackVertices);
    wheelVertices = wheelVertices.stream().map(List::copyOf).toList();
    decorVertices = List.copyOf(decorVertices);
    turretVertices = List.copyOf(turretVertices);
    cannonVertices = List.copyOf(cannonVertices);
    cannonAnchor = List.copyOf(cannonAnchor);
  }

  public static final class Builder {
    private final List<List<Point2>> wheelVertices = new ArrayList<>();
    private List<Point2> hullVertices = List.of();
    private String hullColor;
    private List<Point2> trackVertices = List.of();
    private String trackColor;
    private String wheelColor;
    private List<Point2> decorVertices = List.of();
    private String decorColor;
    private List<Point2> turretVertices = List.of();
    private String turretColor;
    private List<Point2> cannonVertices = List.of();
    private String cannonColor;
    private List<Point2> cannonAnchor = List.of();

    public void hull(List<Point2> vertices, String fill) {
      hullVertices = vertices;
      hullColor = fill;
    }

    public void tracks(List<Point2> vertices, String fill) {
      trackVertices = vertices;
      trackColor = fill;
    }

    public void wheel(List<Point2> vertices, String fill) {
      wheelVertices.add(vertices);
      if (wheelColor == null) {
        wheelColor = fill;
      }
    }

    public void decor(List<Point2> vertices, String fill) {
      decorVertices = vertices;
      decorColor = fill;
    }

    public void turret(List<Point2> vertices, String fill) {
      turretVertices = vertices;
      turretColor = fill;
    }

    public void cannon(List<Point2> vertices, String fill) {
      cannonVertices = vertices;
      cannonColor = fill;
    }

    public void cannonAnchor(List<Point2> vertices) {
      cannonAnchor = vertices;
    }

    public TankData build() {
      return new TankData(
          hullVertices,
          hullColor,
          trackVertices,
          trackColor,
          wheelVertices,
          wheelColor,
          decorVertices,
          decorColor,
          turretVertices,
          turretColor,
          cannonVertices,
          cannonColor,
          cannonAnchor);
    }
  }
}
