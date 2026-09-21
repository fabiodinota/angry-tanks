package com.angrytanks.model.assets;

import com.angrytanks.model.geometry.Point2;
import java.util.List;

public record MapElement(String type, List<Point2> vertices, String fillColor) {
  public MapElement {
    vertices = List.copyOf(vertices);
  }
}
