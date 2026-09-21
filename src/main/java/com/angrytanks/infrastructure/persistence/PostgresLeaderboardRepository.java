package com.angrytanks.infrastructure.persistence;

import com.angrytanks.model.api.MatchResult;
import com.angrytanks.model.leaderboard.LeaderboardEntry;
import com.angrytanks.model.leaderboard.LeaderboardRecorder;
import com.angrytanks.model.leaderboard.LeaderboardRepository;
import com.angrytanks.model.leaderboard.LeaderboardUnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PostgresLeaderboardRepository
    implements LeaderboardRepository, LeaderboardRecorder {
  private final Connections connections;

  public PostgresLeaderboardRepository(Connections connections) {
    this.connections = Objects.requireNonNull(connections);
  }

  private static void ensureUsernameIndex(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(
          "CREATE UNIQUE INDEX IF NOT EXISTS users_username_lower " + "ON users (LOWER(username))");
    }
  }

  private static void recordWin(Connection connection, String username) throws SQLException {
    try (PreparedStatement update =
        connection.prepareStatement(
            "UPDATE users SET wins = wins + 1, score = score + ? "
                + "WHERE LOWER(username) = LOWER(?)")) {
      update.setInt(1, LeaderboardRecorder.WIN_SCORE);
      update.setString(2, username);
      if (update.executeUpdate() != 0) {
        return;
      }
      try (PreparedStatement insert =
          connection.prepareStatement(
              "INSERT INTO users(username, wins, losses, score) VALUES (?, 1, 0, ?)")) {
        insert.setString(1, username);
        insert.setInt(2, LeaderboardRecorder.WIN_SCORE);
        insert.executeUpdate();
      }
    }
  }

  private static void recordLoss(Connection connection, String username) throws SQLException {
    try (PreparedStatement update =
        connection.prepareStatement(
            "UPDATE users SET losses = losses + 1 WHERE LOWER(username) = LOWER(?)")) {
      update.setString(1, username);
      if (update.executeUpdate() != 0) {
        return;
      }
      try (PreparedStatement insert =
          connection.prepareStatement(
              "INSERT INTO users(username, wins, losses, score) VALUES (?, 0, 1, 0)")) {
        insert.setString(1, username);
        insert.executeUpdate();
      }
    }
  }

  private static LeaderboardUnavailableException unavailable(Throwable error) {
    if (error instanceof LeaderboardUnavailableException unavailable) {
      return unavailable;
    }
    return new LeaderboardUnavailableException("PostgreSQL connection", error);
  }

  @Override
  public List<LeaderboardEntry> load() {
    try (Connection connection = connections.open()) {
      boolean autoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
      try {
        ensureUsernameIndex(connection);
        connection.commit();
      } catch (SQLException | RuntimeException error) {
        LeaderboardSql.rollback(connection, error);
        throw error;
      }
      connection.setAutoCommit(autoCommit);
      List<LeaderboardEntry> entries = new ArrayList<>();
      try (PreparedStatement statement =
              connection.prepareStatement(
                  "SELECT username, wins, losses, score FROM users ORDER BY score DESC");
          ResultSet rows = statement.executeQuery()) {
        while (rows.next()) {
          int rank = entries.size() + 1;
          entries.add(LeaderboardSql.readEntry(rows, rank));
        }
      }
      return List.copyOf(entries);
    } catch (SQLException | RuntimeException error) {
      throw unavailable(error);
    }
  }

  @Override
  public void recordMatch(MatchResult result) {
    Objects.requireNonNull(result, "match result");
    Objects.requireNonNull(result.winnerName(), "winner name");
    Objects.requireNonNull(result.loserName(), "loser name");
    try (Connection connection = connections.open()) {
      boolean autoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
      try {
        ensureUsernameIndex(connection);
        recordWin(connection, result.winnerName());
        recordLoss(connection, result.loserName());
        connection.commit();
      } catch (SQLException | RuntimeException error) {
        LeaderboardSql.rollback(connection, error);
        throw error;
      }
      connection.setAutoCommit(autoCommit);
    } catch (SQLException | RuntimeException error) {
      throw unavailable(error);
    }
  }

  @FunctionalInterface
  public interface Connections {
    Connection open() throws SQLException;
  }
}
