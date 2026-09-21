package com.angrytanks.view.fx.support;

import com.angrytanks.model.api.Registration;
import com.angrytanks.presenter.contract.FrameLoop;
import javafx.animation.AnimationTimer;

public final class FxFrameLoop implements FrameLoop {
  @Override
  public Registration start(Runnable pulse) {
    var timer =
        new AnimationTimer() {
          @Override
          public void handle(long now) {
            pulse.run();
          }
        };
    timer.start();
    return timer::stop;
  }
}
