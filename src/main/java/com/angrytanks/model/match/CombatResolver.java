package com.angrytanks.model.match;

import com.angrytanks.model.entity.tank.Tank;
import com.angrytanks.model.terrain.DestructibleTerrain;

final class CombatResolver {
  private static final int SHELL_DAMAGE = 25;
  private final GameSession session;

  CombatResolver(GameSession session) {
    this.session = session;
  }

  void resolve(ProjectileImpact impact) {
    if (session.isClosed()) return;
    var world = session.getWorld();
    if (!world.getAllActors().contains(impact.projectile())) return;
    world.removeActor(impact.projectile());
    if (session.isFinished() || !world.getAllActors().contains(impact.target())) return;
    if (impact.target() instanceof Tank tank) {
      var hitPlayer =
          session.getPlayers().stream()
              .filter(player -> player.getTank() == tank)
              .findFirst()
              .orElse(null);
      if (hitPlayer == null || tank == impact.projectile().getShooter()) {
        return;
      }
      hitPlayer.damage(SHELL_DAMAGE);
      if (hitPlayer.getHealth() == 0) {
        tank.detachTurret();
        var winner =
            session.getPlayers().stream()
                .filter(player -> player != hitPlayer)
                .findFirst()
                .orElseThrow();
        session.finish(winner);
      }
    } else if (impact.target() instanceof DestructibleTerrain terrain) {
      terrain.applyImpact(impact.position());
      if (terrain.isEmpty()) world.removeActor(terrain);
    }
  }
}
