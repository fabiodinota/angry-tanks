package com.angrytanks.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseUtil {
  private DatabaseUtil() {}

  public static Connection getConnection(DatabaseConfiguration.PostgresSettings settings)
      throws SQLException {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException error) {
      throw new SQLException(
          "PostgreSQL mode requires the Maven 'postgres' profile or an external JDBC driver",
          error);
    }
    return DriverManager.getConnection(settings.url(), settings.username(), settings.password());
  }
}
