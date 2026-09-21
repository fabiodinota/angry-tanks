package com.angrytanks.view.contract;

public interface SelectionView {
  void bind(Actions actions);

  void display(State state);

  void invalidNames();

  interface Actions {
    void changeLeft(int direction);

    void changeRight(int direction);

    void start(String leftName, String rightName);

    void back();
  }

  record State(
      String leftTank,
      String rightTank,
      boolean leftPrev,
      boolean leftNext,
      boolean rightPrev,
      boolean rightNext) {}
}
