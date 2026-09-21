package com.angrytanks.view.fx.support;

import com.angrytanks.model.api.DelayScheduler;
import com.angrytanks.model.api.Registration;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class FxDelayScheduler implements DelayScheduler {
  @Override
  public Registration schedule(long millis, Runnable action) {
    var timer = new PauseTransition(Duration.millis(millis));
    timer.setOnFinished(e -> action.run());
    timer.play();
    return () -> {
      timer.stop();
      timer.setOnFinished(null);
    };
  }
}
