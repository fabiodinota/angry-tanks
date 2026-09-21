package com.angrytanks.model.leaderboard;

import java.util.List;

@FunctionalInterface
public interface LeaderboardRepository {
  List<LeaderboardEntry> load() throws LeaderboardUnavailableException;
}
