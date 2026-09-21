package com.angrytanks.model.leaderboard;

public final class LeaderboardUnavailableException extends RuntimeException {
  public LeaderboardUnavailableException(Throwable cause) {
    super("Leaderboard unavailable", cause);
  }

  public LeaderboardUnavailableException(String context, Throwable cause) {
    super("Leaderboard unavailable (" + context + ")", cause);
  }
}
