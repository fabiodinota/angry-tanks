package com.angrytanks.app;

import com.angrytanks.infrastructure.persistence.DatabaseConfiguration;
import com.angrytanks.infrastructure.persistence.DatabaseUtil;
import com.angrytanks.infrastructure.persistence.PostgresLeaderboardRepository;
import com.angrytanks.infrastructure.persistence.SqliteLeaderboardRepository;
import com.angrytanks.model.api.GameModelFactory;
import com.angrytanks.model.leaderboard.LeaderboardRecorder;
import com.angrytanks.model.leaderboard.LeaderboardRepository;
import com.angrytanks.presenter.GamePresenter;
import com.angrytanks.presenter.LeaderboardPresenter;
import com.angrytanks.presenter.MainMenuPresenter;
import com.angrytanks.presenter.ResultsPresenter;
import com.angrytanks.presenter.SelectionPresenter;
import com.angrytanks.presenter.contract.FrameLoop;
import com.angrytanks.view.fx.screen.FxGameView;
import com.angrytanks.view.fx.screen.FxLeaderboardView;
import com.angrytanks.view.fx.screen.FxMainMenuView;
import com.angrytanks.view.fx.screen.FxResultsView;
import com.angrytanks.view.fx.screen.FxSelectionView;
import com.angrytanks.view.fx.support.FxDelayScheduler;
import com.angrytanks.view.fx.support.FxFrameLoop;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public final class AppBootstrap {
  private AppBootstrap() {}

  public static SceneManager create(Stage stage) {
    var configuration = DatabaseConfiguration.fromEnvironment();
    var modelFactory = new SessionFactory(new FxDelayScheduler());
    var frameLoop = new FxFrameLoop();
    return switch (configuration.mode()) {
      case SQLITE -> {
        var repository = new SqliteLeaderboardRepository(configuration.sqlitePath());
        yield create(stage, repository, repository, modelFactory, frameLoop);
      }
      case POSTGRES -> {
        var settings = configuration.postgresSettings();
        var repository =
            new PostgresLeaderboardRepository(() -> DatabaseUtil.getConnection(settings));
        yield create(stage, repository, repository, modelFactory, frameLoop);
      }
    };
  }

  public static SceneManager create(
      Stage stage,
      LeaderboardRepository repository,
      LeaderboardRecorder recorder,
      GameModelFactory modelFactory,
      FrameLoop frameLoop) {
    var manager = new SceneManager(stage);
    load(
        manager,
        SceneManager.Screen.MENU,
        "menu/menu.fxml",
        "menu/menu.css",
        (FxMainMenuView view) -> new MainMenuPresenter(view, manager));
    load(
        manager,
        SceneManager.Screen.SELECTION,
        "selection/selection.fxml",
        "selection/selection.css",
        (FxSelectionView view) -> new SelectionPresenter(view, manager));
    load(
        manager,
        SceneManager.Screen.GAME,
        "game/game.fxml",
        null,
        (FxGameView view) ->
            manager.setGamePresenterFactory(
                () -> new GamePresenter(view, modelFactory, frameLoop, manager, recorder)));
    load(
        manager,
        SceneManager.Screen.RESULTS,
        "results/results.fxml",
        "results/results.css",
        (FxResultsView view) ->
            manager.setResultsHandler(new ResultsPresenter(view, manager)::show));
    load(
        manager,
        SceneManager.Screen.LEADERBOARD,
        "leaderboard/leaderboard.fxml",
        "leaderboard/leaderboard.css",
        (FxLeaderboardView view) ->
            manager.setLeaderboardRefresh(
                new LeaderboardPresenter(view, repository, manager)::refresh));
    return manager;
  }

  private static <V> void load(
      SceneManager manager,
      SceneManager.Screen screen,
      String fxml,
      String css,
      Consumer<V> attachPresenter) {
    try {
      var loader = new FXMLLoader(AppBootstrap.class.getResource("/com/angrytanks/view/" + fxml));
      Parent root = loader.load();
      attachPresenter.accept(loader.getController());
      String stylesheet = null;
      if (css != null) {
        stylesheet = AppBootstrap.class.getResource("/com/angrytanks/view/" + css).toExternalForm();
      }
      manager.register(screen, root, stylesheet);
    } catch (IOException error) {
      manager.close();
      throw new IllegalStateException("Unable to load " + fxml, error);
    }
  }
}
