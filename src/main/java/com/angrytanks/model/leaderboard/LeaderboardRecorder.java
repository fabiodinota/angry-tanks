package com.angrytanks.model.leaderboard;

import com.angrytanks.model.api.MatchResult;

@FunctionalInterface
public interface LeaderboardRecorder {
  int WIN_SCORE = 100;

  void recordMatch(MatchResult result) throws LeaderboardUnavailableException;
}
