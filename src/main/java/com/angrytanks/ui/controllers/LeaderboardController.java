package com.angrytanks.ui.controllers;

import com.angrytanks.ui.SceneManager;
import com.angrytanks.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LeaderboardController {

    @FXML
    private TableView<LeaderboardEntry> leaderboardTable;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> rankColumn;

    @FXML
    private TableColumn<LeaderboardEntry, String> usernameColumn;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> scoreColumn;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> winsColumn;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> lossesColumn;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> trophiesColumn;

    private SceneManager sceneManager;


    /**
     * Model class representing a leaderboard entry.
     */
    public static class LeaderboardEntry {
        private final int rank;
        private final String username;
        private final int score;
        private final int wins;
        private final int losses;
        private final int trophies;

        public LeaderboardEntry(int rank, String username, int score, int wins, int losses, int trophies) {
            this.rank = rank;
            this.username = username;
            this.score = score;
            this.wins = wins;
            this.losses = losses;
            this.trophies = trophies;
        }

        public int getRank() { return rank; }
        public String getUsername() { return username; }
        public int getScore() { return score; }
        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public int getTrophies() { return trophies; }
    }

    /**
     * Initializes the leaderboard table with data from the database.
     */
    @FXML
    private void initialize() {
        // Setup cell value factories for each column.
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        lossesColumn.setCellValueFactory(new PropertyValueFactory<>("losses"));
        trophiesColumn.setCellValueFactory(new PropertyValueFactory<>("trophies"));

        ObservableList<LeaderboardEntry> data = FXCollections.observableArrayList();

        // Use DatabaseUtil to get the connection instead of DriverManager directly.
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             // Ordering by coins descending; adjust the query as needed.
             ResultSet rs = stmt.executeQuery("SELECT username, wins, losses, score, trophies FROM users ORDER BY score DESC")) {

            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                int score = rs.getInt("score"); // Adjust if you renamed this column to 'score'
                int trophies = rs.getInt("trophies");

                data.add(new LeaderboardEntry(rank++, username, score, wins, losses, trophies));
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.add(new LeaderboardEntry(0, "Error loading data", 0, 0, 0, 0));
        }
        leaderboardTable.setItems(data);
    }


    /**
     * Called when the "Back" button is clicked.
     * Returns to the main menu.
     */
    @FXML
    private void onBack() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    /**
     * Setter for the SceneManager.
     *
     * @param sceneManager the SceneManager instance used for scene transitions.
     */
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
