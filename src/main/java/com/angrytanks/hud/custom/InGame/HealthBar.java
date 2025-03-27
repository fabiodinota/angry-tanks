package com.angrytanks.hud.custom.InGame;

public class HealthBar {

    // Static fields to store each player's health.
    // Accessible from anywhere via HealthBar.player1Health, etc.
    public static int player1Health = 100;
    public static int player2Health = 100;

    // Example "hit" methods to reduce health safely (not going below 0).
    public static void hitPlayer1(int damage) {
        player1Health = Math.max(0, player1Health - damage);
    }

    public static void hitPlayer2(int damage) {
        player2Health = Math.max(0, player2Health - damage);
    }

    // Optionally, you can add methods to set health, get health, etc.
    public static int getPlayer1Health() {
        return player1Health;
    }

    public static int getPlayer2Health() {
        return player2Health;
    }

    public static void setPlayer1Health(int health) {
        player1Health = Math.max(0, health);
    }

    public static void setPlayer2Health(int health) {
        player2Health = Math.max(0, health);
    }

    public static void resetHealth() {
        player1Health = 100;
        player2Health = 100;
    }
}
