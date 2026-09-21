package com.angrytanks.app;

import com.angrytanks.model.api.MatchConfig;
import com.angrytanks.model.api.MatchResult;
import com.angrytanks.presenter.GamePresenter;
import com.angrytanks.presenter.contract.Navigator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneManager implements Navigator, AutoCloseable {
  private final Stage stage;
  private final Scene scene = new Scene(new Group(), 1980, 1080);
  private final Map<Screen, ScreenContent> screens = new EnumMap<>(Screen.class);
  private Supplier<GamePresenter> gamePresenterFactory;
  private Consumer<MatchResult> resultsHandler;
  private GamePresenter activeGamePresenter;
  private Runnable leaderboardRefresh = () -> {};
  SceneManager(Stage stage) {
    this.stage = stage;
    stage.setResizable(false);
    stage.setScene(scene);
    stage.setFullScreenExitHint("Press Esc to exit full screen");
    stage.setFullScreen(true);
    stage.setOnCloseRequest(e -> close());
  }

  void setLeaderboardRefresh(Runnable refresh) {
    leaderboardRefresh = Objects.requireNonNull(refresh);
  }

  void register(Screen screen, Parent root, String stylesheet) {
    Parent requiredRoot = Objects.requireNonNull(root);
    List<String> stylesheets = stylesheet == null ? List.of() : List.of(stylesheet);
    screens.put(screen, new ScreenContent(requiredRoot, stylesheets));
  }

  void setGamePresenterFactory(Supplier<GamePresenter> factory) {
    gamePresenterFactory = factory;
  }

  void setResultsHandler(Consumer<MatchResult> handler) {
    resultsHandler = handler;
  }

  private void show(Screen screen, String title) {
    select(screen, title);
    if (!stage.isShowing()) stage.show();
  }

  private void select(Screen screen, String title) {
    ScreenContent content = Objects.requireNonNull(screens.get(screen));
    stage.setTitle("Angry Tanks - " + title);
    scene.setRoot(content.root());
    scene.getStylesheets().setAll(content.stylesheets());
  }

  @Override
  public void showMainMenu() {
    closeActiveMatch();
    show(Screen.MENU, "Main Menu");
  }

  @Override
  public void showTankSelection() {
    closeActiveMatch();
    show(Screen.SELECTION, "Tank Selection");
  }

  @Override
  public void showLeaderboard() {
    closeActiveMatch();
    leaderboardRefresh.run();
    show(Screen.LEADERBOARD, "Leaderboard");
  }

  @Override
  public void showGame(MatchConfig config) {
    closeActiveMatch();
    select(Screen.GAME, "In Game");
    activeGamePresenter = gamePresenterFactory.get();
    try {
      activeGamePresenter.start(config);
      if (!stage.isShowing()) stage.show();
    } catch (RuntimeException | Error error) {
      closeActiveMatch();
      throw error;
    }
  }

  @Override
  public void showResults(MatchResult result) {
    closeActiveMatch();
    resultsHandler.accept(result);
    show(Screen.RESULTS, "Results");
  }

  @Override
  public void exit() {
    closeActiveMatch();
    Platform.exit();
  }

  @Override
  public void close() {
    closeActiveMatch();
  }

  private void closeActiveMatch() {
    if (activeGamePresenter == null) {
      return;
    }

    var presenter = activeGamePresenter;
    activeGamePresenter = null;
    presenter.close();
  }

  enum Screen {
    MENU,
    SELECTION,
    GAME,
    RESULTS,
    LEADERBOARD
  }

  private record ScreenContent(Parent root, List<String> stylesheets) {}
}
