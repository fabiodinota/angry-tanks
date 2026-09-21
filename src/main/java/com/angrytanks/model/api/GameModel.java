package com.angrytanks.model.api;

import com.angrytanks.model.snapshot.MatchSnapshot;
import java.util.function.Consumer;

public interface GameModel extends AutoCloseable {
  void act(GameAction action);

  void step();

  MatchSnapshot snapshot();

  Registration onFinished(Consumer<MatchResult> listener);

  @Override
  void close();
}
