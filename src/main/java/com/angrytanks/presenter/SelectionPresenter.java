package com.angrytanks.presenter;

import com.angrytanks.model.api.MatchConfig;
import com.angrytanks.model.assets.TankCatalog;
import com.angrytanks.presenter.contract.Navigator;
import com.angrytanks.view.contract.SelectionView;

public final class SelectionPresenter implements SelectionView.Actions {
  private final SelectionView view;
  private final Navigator navigator;
  private int leftIndex = 0;
  private int rightIndex = 1;

  public SelectionPresenter(SelectionView view, Navigator navigator) {
    this.view = view;
    this.navigator = navigator;
    view.bind(this);
    displaySelection();
  }

  @Override
  public void changeLeft(int direction) {
    leftIndex = TankCatalog.adjacent(leftIndex, rightIndex, direction);
    displaySelection();
  }

  @Override
  public void changeRight(int direction) {
    rightIndex = TankCatalog.adjacent(rightIndex, leftIndex, direction);
    displaySelection();
  }

  @Override
  public void start(String leftPlayerName, String rightPlayerName) {
    if (leftPlayerName.isEmpty() || rightPlayerName.isEmpty()) {
      view.invalidNames();
      return;
    }
    navigator.showGame(
        new MatchConfig(
            leftPlayerName,
            rightPlayerName,
            TankCatalog.resource(leftIndex),
            TankCatalog.resource(rightIndex),
            "/maps/Desert.svg"));
  }

  @Override
  public void back() {
    navigator.showMainMenu();
  }

  private void displaySelection() {
    boolean leftPreviousAvailable = TankCatalog.adjacent(leftIndex, rightIndex, -1) != leftIndex;
    boolean leftNextAvailable = TankCatalog.adjacent(leftIndex, rightIndex, 1) != leftIndex;
    boolean rightPreviousAvailable = TankCatalog.adjacent(rightIndex, leftIndex, -1) != rightIndex;
    boolean rightNextAvailable = TankCatalog.adjacent(rightIndex, leftIndex, 1) != rightIndex;
    view.display(
        new SelectionView.State(
            TankCatalog.NAMES.get(leftIndex),
            TankCatalog.NAMES.get(rightIndex),
            leftPreviousAvailable,
            leftNextAvailable,
            rightPreviousAvailable,
            rightNextAvailable));
  }
}
