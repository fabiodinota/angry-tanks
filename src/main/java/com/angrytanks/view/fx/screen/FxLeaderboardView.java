package com.angrytanks.view.fx.screen;

import com.angrytanks.model.leaderboard.LeaderboardEntry;
import com.angrytanks.view.contract.LeaderboardView;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public final class FxLeaderboardView implements LeaderboardView {
  @FXML private TableView<LeaderboardEntry> leaderboardTable;
  @FXML private TableColumn<LeaderboardEntry, Integer> rankColumn;
  @FXML private TableColumn<LeaderboardEntry, String> usernameColumn;
  @FXML private TableColumn<LeaderboardEntry, Integer> scoreColumn;
  @FXML private TableColumn<LeaderboardEntry, Integer> winsColumn;
  @FXML private TableColumn<LeaderboardEntry, Integer> lossesColumn;
  private Runnable backAction;

  @FXML
  private void initialize() {
    rankColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().rank()));
    usernameColumn.setCellValueFactory(
        row -> new ReadOnlyObjectWrapper<>(row.getValue().username()));
    scoreColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().score()));
    winsColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().wins()));
    lossesColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().losses()));
  }

  @Override
  public void display(List<LeaderboardEntry> rows) {
    leaderboardTable.setItems(FXCollections.observableArrayList(rows));
  }

  @FXML
  private void onBack() {
    backAction.run();
  }

  @Override
  public void bind(Runnable backAction) {
    this.backAction = backAction;
  }
}
