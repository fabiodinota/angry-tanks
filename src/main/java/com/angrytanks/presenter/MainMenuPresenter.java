package com.angrytanks.presenter;

import com.angrytanks.presenter.contract.Navigator;
import com.angrytanks.view.contract.MainMenuView;

public final class MainMenuPresenter {
  public MainMenuPresenter(MainMenuView view, Navigator navigator) {
    view.bind(
        new MainMenuView.Actions() {
          @Override
          public void play() {
            navigator.showTankSelection();
          }

          @Override
          public void leaderboard() {
            navigator.showLeaderboard();
          }

          @Override
          public void exit() {
            navigator.exit();
          }
        });
  }
}
