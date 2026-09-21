package com.angrytanks.infrastructure.persistence;

import com.angrytanks.model.api.MatchResult;
import com.angrytanks.model.leaderboard.LeaderboardEntry;
import com.angrytanks.model.leaderboard.LeaderboardRecorder;
import com.angrytanks.model.leaderboard.LeaderboardRepository;
import com.angrytanks.model.leaderboard.LeaderboardUnavailableException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record SqliteLeaderboardRepository(Path databasePath)
    implements LeaderboardRepository, LeaderboardRecorder {
  private static final String SCHEMA =
      """
      CREATE TABLE users (
          username TEXT NOT NULL,
          wins INTEGER NOT NULL DEFAULT 0,
          losses INTEGER NOT NULL DEFAULT 0,
          score INTEGER NOT NULL DEFAULT 0
      )
      """;
  private static final String QUERY =
      "SELECT username, wins, losses, score FROM users ORDER BY score DESC";
  private static final String UNIQUE_INDEX =
      "CREATE UNIQUE INDEX IF NOT EXISTS users_username_nocase "
          + "ON users(username COLLATE NOCASE)";

  public SqliteLeaderboardRepository {
    databasePath = Objects.requireNonNull(databasePath).toAbsolutePath().normalize();
  }

  private static void ensureSchema(Connection connection) throws SQLException {
    boolean autoCommit = connection.getAutoCommit();
    connection.setAutoCommit(false);
    try {
      List<String> columns = readColumnNames(connection);

      if (columns.isEmpty()) {
        try (Statement statement = connection.createStatement()) {
          statement.executeUpdate(SCHEMA);
        }
      } else if (!columns.containsAll(List.of("username", "wins", "losses", "score"))) {
        throw new SQLException("SQLite users table has an incompatible schema");
      }

      try (Statement statement = connection.createStatement()) {
        statement.executeUpdate(UNIQUE_INDEX);
      }
      connection.commit();
    } catch (SQLException | RuntimeException error) {
      LeaderboardSql.rollback(connection, error);
      throw error;
    }
    connection.setAutoCommit(autoCommit);
  }

  private static List<String> readColumnNames(Connection connection) throws SQLException {
    List<String> columns = new ArrayList<>();
    try (Statement statement = connection.createStatement();
        ResultSet rows = statement.executeQuery("PRAGMA table_info(users)")) {
      while (rows.next()) {
        columns.add(rows.getString("name"));
      }
    }
    return columns;
  }

  private static void recordWin(Connection connection, String username) throws SQLException {
    try (PreparedStatement update =
        connection.prepareStatement(
            "UPDATE users SET wins = wins + 1, score = score + ? "
                + "WHERE username = ? COLLATE NOCASE")) {
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
            "UPDATE users SET losses = losses + 1 WHERE username = ? COLLATE NOCASE")) {
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

  @Override
  public List<LeaderboardEntry> load() {
    try (Connection connection = openConnection()) {
      ensureSchema(connection);
      try (PreparedStatement query = connection.prepareStatement(QUERY);
          ResultSet rows = query.executeQuery()) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        while (rows.next()) {
          int rank = entries.size() + 1;
          entries.add(LeaderboardSql.readEntry(rows, rank));
        }
        return List.copyOf(entries);
      }
    } catch (IOException | SQLException | RuntimeException error) {
      throw unavailable(error);
    }
  }

  @Override
  public void recordMatch(MatchResult result) {
    Objects.requireNonNull(result, "match result");
    Objects.requireNonNull(result.winnerName(), "winner name");
    Objects.requireNonNull(result.loserName(), "loser name");
    try (Connection connection = openConnection()) {
      ensureSchema(connection);
      connection.setAutoCommit(false);
      try {
        recordWin(connection, result.winnerName());
        recordLoss(connection, result.loserName());
        connection.commit();
      } catch (SQLException | RuntimeException error) {
        LeaderboardSql.rollback(connection, error);
        throw error;
      }
    } catch (IOException | SQLException | RuntimeException error) {
      throw unavailable(error);
    }
  }

  private Connection openConnection() throws IOException, SQLException {
    Path parent = databasePath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
  }

  private LeaderboardUnavailableException unavailable(Throwable error) {
    if (error instanceof LeaderboardUnavailableException unavailable) {
      return unavailable;
    }
    return new LeaderboardUnavailableException("SQLite database " + databasePath, error);
  }
}
