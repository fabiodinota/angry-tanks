package com.angrytanks.infrastructure.assets;

import com.angrytanks.model.assets.MapElement;
import java.util.List;

public record SvgAsset(String background, List<MapElement> elements) {
  public SvgAsset {
    elements = List.copyOf(elements);
  }
}
