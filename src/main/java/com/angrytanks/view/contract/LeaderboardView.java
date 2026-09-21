package com.angrytanks.view.contract;

import com.angrytanks.model.leaderboard.LeaderboardEntry;
import java.util.List;

public interface LeaderboardView {
  void bind(Runnable backAction);

  void display(List<LeaderboardEntry> rows);
}
