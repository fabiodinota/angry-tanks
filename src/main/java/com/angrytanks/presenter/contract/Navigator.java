package com.angrytanks.presenter.contract;

import com.angrytanks.model.api.MatchConfig;
import com.angrytanks.model.api.MatchResult;

public interface Navigator {
  void showMainMenu();

  void showTankSelection();

  void showLeaderboard();

  void showGame(MatchConfig config);

  void showResults(MatchResult result);

  void exit();
}
