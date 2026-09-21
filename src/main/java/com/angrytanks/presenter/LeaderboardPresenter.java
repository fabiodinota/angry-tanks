package com.angrytanks.presenter;

import com.angrytanks.model.leaderboard.LeaderboardEntry;
import com.angrytanks.model.leaderboard.LeaderboardRepository;
import com.angrytanks.model.leaderboard.LeaderboardUnavailableException;
import com.angrytanks.presenter.contract.Navigator;
import com.angrytanks.view.contract.LeaderboardView;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LeaderboardPresenter {
  private static final Logger LOG = Logger.getLogger(LeaderboardPresenter.class.getName());

  private final LeaderboardView view;
  private final LeaderboardRepository repository;

  public LeaderboardPresenter(
      LeaderboardView view, LeaderboardRepository repository, Navigator navigator) {
    this.view = view;
    this.repository = repository;
    view.bind(navigator::showMainMenu);
    refresh();
  }

  public void refresh() {
    try {
      view.display(repository.load());
    } catch (LeaderboardUnavailableException error) {
      LOG.log(Level.WARNING, error.getMessage(), error.getCause());
      view.display(List.of(new LeaderboardEntry(0, "Error loading data", 0, 0, 0)));
    }
  }
}
