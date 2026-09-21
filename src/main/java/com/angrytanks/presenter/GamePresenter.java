package com.angrytanks.presenter;

import com.angrytanks.model.api.GameAction;
import com.angrytanks.model.api.GameModel;
import com.angrytanks.model.api.GameModelFactory;
import com.angrytanks.model.api.MatchConfig;
import com.angrytanks.model.api.MatchResult;
import com.angrytanks.model.api.Registration;
import com.angrytanks.model.leaderboard.LeaderboardRecorder;
import com.angrytanks.model.leaderboard.LeaderboardUnavailableException;
import com.angrytanks.presenter.contract.FrameLoop;
import com.angrytanks.presenter.contract.Navigator;
import com.angrytanks.view.contract.GameFrame;
import com.angrytanks.view.contract.GameView;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GamePresenter implements GameView.Actions, AutoCloseable {
  private static final Logger LOG = Logger.getLogger(GamePresenter.class.getName());
  private final GameView view;
  private final GameModelFactory modelFactory;
  private final FrameLoop frameLoop;
  private final Navigator navigator;
  private final LeaderboardRecorder leaderboardRecorder;
  private GameModel model;
  private Registration inputRegistration = Registration.NONE;
  private Registration frameRegistration = Registration.NONE;
  private Registration completionRegistration = Registration.NONE;
  private boolean active;
  private boolean closed;
  private boolean decompositionVisible;

  public GamePresenter(
      GameView view,
      GameModelFactory modelFactory,
      FrameLoop frameLoop,
      Navigator navigator,
      LeaderboardRecorder leaderboardRecorder) {
    this.view = view;
    this.modelFactory = modelFactory;
    this.frameLoop = frameLoop;
    this.navigator = navigator;
    this.leaderboardRecorder = Objects.requireNonNull(leaderboardRecorder);
  }

  public void start(MatchConfig config) {
    if (closed || active) throw new IllegalStateException("Presenter already started or closed");
    try {
      model = modelFactory.create(config);
      active = true;
      completionRegistration = model.onFinished(this::complete);
      if (!active) {
        completionRegistration.close();
        return;
      }
      inputRegistration = view.bind(this);
      if (!active) {
        inputRegistration.close();
        return;
      }
      displayFrame();
      if (!active) return;
      frameRegistration = frameLoop.start(this::pulse);

      if (!active) frameRegistration.close();
    } catch (RuntimeException | Error error) {
      close();
      throw error;
    }
  }

  @Override
  public void gameAction(GameAction action) {
    if (active) model.act(action);
  }

  @Override
  public void decompositionChanged(boolean visible) {
    if (!active) return;
    decompositionVisible = visible;
    displayFrame();
  }

  private void complete(MatchResult result) {
    if (!active) return;
    close();
    try {
      leaderboardRecorder.recordMatch(result);
    } catch (LeaderboardUnavailableException error) {
      LOG.log(Level.WARNING, "Unable to record completed match", error);
    }
    navigator.showResults(result);
  }

  private void pulse() {
    if (!active) return;
    model.step();

    if (active) displayFrame();
  }

  private void displayFrame() {
    view.display(new GameFrame(model.snapshot(), decompositionVisible));
  }

  @Override
  public void close() {
    if (closed) return;
    closed = true;
    active = false;
    frameRegistration.close();
    inputRegistration.close();
    completionRegistration.close();
    if (model != null) model.close();
    view.clear();
  }
}
