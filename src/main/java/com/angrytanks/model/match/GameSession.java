package com.angrytanks.model.match;

import com.angrytanks.model.api.DelayScheduler;
import com.angrytanks.model.api.GameAction;
import com.angrytanks.model.api.GameModel;
import com.angrytanks.model.api.MatchConfig;
import com.angrytanks.model.api.MatchResult;
import com.angrytanks.model.api.Registration;
import com.angrytanks.model.assets.TankData;
import com.angrytanks.model.entity.Actor;
import com.angrytanks.model.entity.tank.Tank;
import com.angrytanks.model.entity.tank.TankController;
import com.angrytanks.model.geometry.GeometryException;
import com.angrytanks.model.physics.ProjectileContactListener;
import com.angrytanks.model.snapshot.MatchSnapshot;
import com.angrytanks.model.snapshot.PlayerSnapshot;
import com.angrytanks.model.world.GameWorld;
import com.angrytanks.model.world.MapLayout;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class GameSession implements GameModel {
  private static final int SHOT_COOLDOWN_STEPS = 60;
  private static final double LEFT_SPAWN_X_PIXELS = 600;
  private static final double RIGHT_SPAWN_X_PIXELS = 1150;
  private static final double SPAWN_Y_PIXELS = 500;
  private final GameWorld world = new GameWorld();
  private final MapLayout mapLayout;
  private final List<PlayerState> players;
  private final MatchController actions;
  private int currentPlayerIndex;
  private long simulationStep;
  private long nextAllowedShotStep;
  private PlayerState winner;
  private boolean closed;
  private Consumer<MatchResult> completionHandler = result -> {};

  public GameSession(
      MatchConfig config,
      MapLayout map,
      TankData leftTankData,
      TankData rightTankData,
      DelayScheduler scheduler) {
    mapLayout = map;
    try {
      map.getAllMapActors().forEach(world::addActor);
      players =
          List.of(
              createPlayer(
                  config.player1Name(),
                  config.tank1Resource(),
                  leftTankData,
                  LEFT_SPAWN_X_PIXELS,
                  false,
                  scheduler),
              createPlayer(
                  config.player2Name(),
                  config.tank2Resource(),
                  rightTankData,
                  RIGHT_SPAWN_X_PIXELS,
                  true,
                  scheduler));
      actions = new MatchController(this);
      registerCollisionHandling();
    } catch (RuntimeException | Error error) {
      world.close();
      throw error;
    }
  }

  private void registerCollisionHandling() {
    var combat = new CombatResolver(this);
    var physics = world.getPhysicsWorld();
    physics
        .rawWorld()
        .setContactListener(
            new ProjectileContactListener(
                impact -> physics.scheduleAction(() -> combat.resolve(impact))));
  }

  private PlayerState createPlayer(
      String name,
      String assetId,
      TankData tankData,
      double startX,
      boolean mirrored,
      DelayScheduler scheduler) {
    try {
      var tank =
          new Tank(tankData, assetId, startX, SPAWN_Y_PIXELS, mirrored, world::addActor, scheduler);
      world.addActor(tank);
      return new PlayerState(name, tank);
    } catch (GeometryException failure) {
      throw failure.withContext(assetId);
    }
  }

  public GameWorld getWorld() {
    return world;
  }

  public MapLayout getMapLayout() {
    return mapLayout;
  }

  public List<PlayerState> getPlayers() {
    return players;
  }

  public PlayerState getCurrentPlayer() {
    return players.get(currentPlayerIndex);
  }

  public PlayerState getWinner() {
    return winner;
  }

  public boolean isFinished() {
    return winner != null;
  }

  public boolean isClosed() {
    return closed;
  }

  private void fireAndAdvance() {
    if (closed || isFinished() || simulationStep < nextAllowedShotStep) return;
    if (!TankController.fire(getCurrentPlayer().getTank())) return;
    nextAllowedShotStep = simulationStep + SHOT_COOLDOWN_STEPS;
    currentPlayerIndex = 1 - currentPlayerIndex;
  }

  @Override
  public Registration onFinished(Consumer<MatchResult> callback) {
    Objects.requireNonNull(callback);
    if (closed) return Registration.NONE;
    completionHandler = callback;
    return () -> {
      if (completionHandler == callback) completionHandler = result -> {};
    };
  }

  public void finish(PlayerState player) {
    if (closed || isFinished()) return;
    int winnerIndex = players.indexOf(player);
    if (winnerIndex < 0) {
      throw new IllegalArgumentException("Invalid winner");
    }
    winner = player;
    PlayerState loser = players.get(1 - winnerIndex);
    completionHandler.accept(new MatchResult(winnerIndex, player.getName(), loser.getName()));
  }

  @Override
  public void act(GameAction action) {
    switch (action) {
      case MOVE_LEFT -> actions.moveLeft();
      case MOVE_RIGHT -> actions.moveRight();
      case STOP -> actions.stop();
      case AIM_UP -> actions.aimUp();
      case AIM_DOWN -> actions.aimDown();
      case FIRE -> fireAndAdvance();
    }
  }

  @Override
  public void step() {
    if (closed || isFinished()) {
      return;
    }

    world.update();
    simulationStep++;
  }

  @Override
  public MatchSnapshot snapshot() {
    return new MatchSnapshot(
        mapLayout.getBackgroundName(),
        world.getAllActors().stream().map(Actor::snapshot).toList(),
        players.stream()
            .map(player -> new PlayerSnapshot(player.getName(), player.getHealth()))
            .toList(),
        currentPlayerIndex,
        isFinished());
  }

  @Override
  public void close() {
    if (closed) return;
    closed = true;
    completionHandler = result -> {};
    world.close();
  }
}
