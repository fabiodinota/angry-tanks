package com.angrytanks.infrastructure.persistence;

import java.nio.file.Path;
import java.util.Locale;

public record DatabaseConfiguration(Mode mode, Path sqlitePath, PostgresSettings postgresSettings) {
  private static final String DEFAULT_POSTGRES_URL = "jdbc:postgresql://localhost:8080/angrytanks";
  private static final String DEFAULT_POSTGRES_USER = "postgres";

  public DatabaseConfiguration {
    if (mode == null) throw new IllegalArgumentException("Database mode is required");
    switch (mode) {
      case SQLITE -> {
        if (sqlitePath == null)
          throw new IllegalArgumentException("SQLite database path is required");
        if (postgresSettings != null)
          throw new IllegalArgumentException("SQLite mode cannot contain PostgreSQL settings");
      }
      case POSTGRES -> {
        if (postgresSettings == null)
          throw new IllegalArgumentException("PostgreSQL settings are required");
        if (sqlitePath != null)
          throw new IllegalArgumentException("PostgreSQL mode cannot contain a SQLite path");
      }
    }
  }

  public static DatabaseConfiguration fromEnvironment() {
    return fromValues(
        System.getenv("ANGRY_TANKS_DB_MODE"),
        System.getenv("ANGRY_TANKS_DB_PATH"),
        System.getenv("ANGRY_TANKS_DB_URL"),
        System.getenv("ANGRY_TANKS_DB_USER"),
        System.getenv("ANGRY_TANKS_DB_PASSWORD"));
  }

  static DatabaseConfiguration fromValues(
      String configuredMode,
      String configuredPath,
      String configuredUrl,
      String configuredUser,
      String configuredPassword) {
    String modeValue =
        configuredMode == null || configuredMode.isBlank() ? "sqlite" : configuredMode;
    String normalizedMode = modeValue.toLowerCase(Locale.ROOT);
    String sqlitePath =
        configuredPath == null || configuredPath.isBlank() ? "data/angrytanks.db" : configuredPath;
    return switch (normalizedMode) {
      case "sqlite" -> new DatabaseConfiguration(Mode.SQLITE, Path.of(sqlitePath), null);
      case "postgres" ->
          new DatabaseConfiguration(
              Mode.POSTGRES,
              null,
              new PostgresSettings(
                  configuredUrl == null ? DEFAULT_POSTGRES_URL : configuredUrl,
                  configuredUser == null ? DEFAULT_POSTGRES_USER : configuredUser,
                  configuredPassword == null ? "" : configuredPassword));
      default ->
          throw new IllegalArgumentException(
              "Unsupported ANGRY_TANKS_DB_MODE '" + normalizedMode + "'; expected sqlite or postgres");
    };
  }

  public enum Mode {
    SQLITE,
    POSTGRES
  }

  public record PostgresSettings(String url, String username, String password) {
    public PostgresSettings {
      if (url == null || username == null || password == null)
        throw new IllegalArgumentException("PostgreSQL settings cannot be null");
    }

    @Override
    public String toString() {
      return "PostgresSettings[redacted]";
    }
  }
}
