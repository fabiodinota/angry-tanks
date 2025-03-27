package com.angrytanks.util;

import com.angrytanks.hud.custom.InGame.HealthBar;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GameOverUtil {
    public static boolean gameOver = false;
    private static final StringProperty playerWonProperty = new SimpleStringProperty("");
    public static String player1 = "";
    public static String player2 = "";

    private static final List<GameOverListener> listeners = new ArrayList<>();

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOver(boolean value) {
        if (gameOver != value) {
            gameOver = value;
            if (gameOver) {
                notifyListeners();
                HealthBar.resetHealth();
            }
        }
    }

    public static void addListener(GameOverListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(GameOverListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (GameOverListener listener : listeners) {
            listener.onGameOver();
        }
    }

    public static String getPlayerWon() {
        return playerWonProperty.get();
    }

    public static void setPlayerWon(String playerWon) {
        playerWonProperty.set(playerWon);
    }

    public static StringProperty playerWonProperty() {
        return playerWonProperty;
    }

    public static String getPlayer1() {
        return player1;
    }

    public static void setPlayer1(String player1) {
        GameOverUtil.player1 = player1;
        System.out.println("GOU name 1: " + player1);
    }

    public static String getPlayer2() {
        return player2;
    }

    public static void setPlayer2(String player2) {
        GameOverUtil.player2 = player2;
        System.out.println("GOU name 2: " + player2);
    }

    public void updateDatabaseEntry() {
        if (getPlayerWon() == null || getPlayerWon().isEmpty()) {
            System.err.println("No winning player specified; database update skipped.");
            return;
        }

        String updateQuery = "UPDATE users " +
                "SET wins = wins + 1, score = score + 100, trophies = trophies + 1 " +
                "WHERE username = '" + getPlayerWon() + "'";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(updateQuery);
            System.out.println("Updated " + rowsAffected + " row(s) for player: " + getPlayerWon());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
