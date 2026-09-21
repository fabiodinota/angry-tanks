package com.angrytanks.view.contract;

public interface MainMenuView {
  void bind(Actions actions);

  interface Actions {
    void play();

    void leaderboard();

    void exit();
  }
}
