package com.angrytanks.infrastructure.persistence;

import com.angrytanks.model.leaderboard.LeaderboardEntry;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

final class LeaderboardSql {
  private LeaderboardSql() {}

  static LeaderboardEntry readEntry(ResultSet rows, int rank) throws SQLException {
    return new LeaderboardEntry(
        rank,
        rows.getString("username"),
        rows.getInt("score"),
        rows.getInt("wins"),
        rows.getInt("losses"));
  }

  static void rollback(Connection connection, Throwable failure) {
    try {
      connection.rollback();
    } catch (SQLException rollbackFailure) {
      failure.addSuppressed(rollbackFailure);
    }
  }
}
