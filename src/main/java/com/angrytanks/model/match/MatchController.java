package com.angrytanks.model.match;

import com.angrytanks.model.entity.tank.Tank;
import com.angrytanks.model.entity.tank.TankController;
import java.util.LinkedHashSet;
import java.util.Set;

final class MatchController {
  private final GameSession session;
  private final Set<Tank> movingTanks = new LinkedHashSet<>();

  MatchController(GameSession session) {
    this.session = session;
  }

  private boolean acceptsInput() {
    return !session.isClosed() && !session.isFinished();
  }

  void moveLeft() {
    if (!acceptsInput()) return;
    Tank tank = session.getCurrentPlayer().getTank();
    movingTanks.add(tank);
    TankController.moveLeft(tank);
  }

  void moveRight() {
    if (!acceptsInput()) return;
    Tank tank = session.getCurrentPlayer().getTank();
    movingTanks.add(tank);
    TankController.moveRight(tank);
  }

  void stop() {
    if (!acceptsInput()) return;
    if (movingTanks.isEmpty()) TankController.stop(session.getCurrentPlayer().getTank());
    else movingTanks.forEach(TankController::stop);
    movingTanks.clear();
  }

  void aimUp() {
    if (acceptsInput()) TankController.turretUp(session.getCurrentPlayer().getTank());
  }

  void aimDown() {
    if (acceptsInput()) TankController.turretDown(session.getCurrentPlayer().getTank());
  }
}
