package com.angrytanks.model.snapshot;

import java.util.List;

public record MatchSnapshot(
    String background,
    List<EntitySnapshot> entities,
    List<PlayerSnapshot> players,
    int currentPlayerIndex,
    boolean finished) {
  public MatchSnapshot {
    entities = List.copyOf(entities);
    players = List.copyOf(players);
  }
}
