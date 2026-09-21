package com.angrytanks.model.api;

@FunctionalInterface
public interface GameModelFactory {
  GameModel create(MatchConfig config);
}
