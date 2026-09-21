package com.angrytanks.presenter;

import com.angrytanks.model.api.MatchResult;
import com.angrytanks.presenter.contract.Navigator;
import com.angrytanks.view.contract.ResultsView;

public final class ResultsPresenter {
  private final ResultsView view;

  public ResultsPresenter(ResultsView view, Navigator navigator) {
    this.view = view;
    view.bind(navigator::showMainMenu);
  }

  public void show(MatchResult result) {
    view.showWinner("Player " + result.winnerName() + " won!");
  }
}
