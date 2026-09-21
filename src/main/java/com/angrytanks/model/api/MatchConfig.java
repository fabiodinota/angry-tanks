package com.angrytanks.model.api;

import java.util.Objects;

public record MatchConfig(
    String player1Name,
    String player2Name,
    String tank1Resource,
    String tank2Resource,
    String mapResource) {
  public MatchConfig {
    Objects.requireNonNull(player1Name);
    Objects.requireNonNull(player2Name);
    Objects.requireNonNull(tank1Resource);
    Objects.requireNonNull(tank2Resource);
    Objects.requireNonNull(mapResource);
    if (player1Name.isEmpty() || player2Name.isEmpty())
      throw new IllegalArgumentException("Both players need names");
  }
}
