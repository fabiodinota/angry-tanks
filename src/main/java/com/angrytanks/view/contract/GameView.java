package com.angrytanks.view.contract;

import com.angrytanks.model.api.GameAction;
import com.angrytanks.model.api.Registration;

public interface GameView {
  Registration bind(Actions actions);

  void display(GameFrame frame);

  void clear();

  interface Actions {
    void gameAction(GameAction action);

    void decompositionChanged(boolean visible);
  }
}
