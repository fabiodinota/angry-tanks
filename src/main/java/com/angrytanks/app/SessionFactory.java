package com.angrytanks.app;

import com.angrytanks.infrastructure.assets.SvgAssetLoader;
import com.angrytanks.infrastructure.assets.SvgTankLoader;
import com.angrytanks.model.api.DelayScheduler;
import com.angrytanks.model.api.GameModelFactory;
import com.angrytanks.model.api.MatchConfig;
import com.angrytanks.model.geometry.GeometryException;
import com.angrytanks.model.match.GameSession;
import com.angrytanks.model.world.MapLayout;

public final class SessionFactory implements GameModelFactory {
  private final DelayScheduler scheduler;

  public SessionFactory(DelayScheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public GameSession create(MatchConfig config) {
    var asset = SvgAssetLoader.load(config.mapResource());
    var map = new MapLayout();
    try {
      map.load(asset.elements(), asset.background());
    } catch (GeometryException failure) {
      throw failure.withContext(config.mapResource());
    }
    var leftTankData = SvgTankLoader.loadTankData(config.tank1Resource(), true);
    var rightTankData = SvgTankLoader.loadTankData(config.tank2Resource(), true);
    return new GameSession(config, map, leftTankData, rightTankData, scheduler);
  }
}
