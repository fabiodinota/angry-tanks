package com.angrytanks.model.physics;

import com.angrytanks.model.entity.Actor;
import com.angrytanks.model.entity.Projectile;
import com.angrytanks.model.entity.tank.Tank;
import com.angrytanks.model.geometry.Point2;
import com.angrytanks.model.match.ProjectileImpact;
import com.angrytanks.model.terrain.DestructibleTerrain;
import java.util.function.Consumer;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public final class ProjectileContactListener implements ContactListener {
  private final Consumer<ProjectileImpact> onImpact;

  public ProjectileContactListener(Consumer<ProjectileImpact> onImpact) {
    this.onImpact = onImpact;
  }

  @Override
  public void beginContact(Contact contact) {
    Object firstActor = contact.getFixtureA().getBody().getUserData();
    Object secondActor = contact.getFixtureB().getBody().getUserData();
    if (firstActor instanceof Projectile projectile && secondActor instanceof Actor target) {
      capture(projectile, target);
    } else if (secondActor instanceof Projectile projectile && firstActor instanceof Actor target) {
      capture(projectile, target);
    }
  }

  private void capture(Projectile projectile, Actor target) {
    if (!(target instanceof DestructibleTerrain) && !(target instanceof Tank)) return;
    if (target == projectile.getShooter()) return;
    if (!projectile.consume()) return;
    var position = projectile.getPhysicsBody().getPosition();
    onImpact.accept(
        new ProjectileImpact(
            projectile,
            target,
            new Point2(
                position.x * PhysicsUnits.PIXELS_PER_METER,
                position.y * PhysicsUnits.PIXELS_PER_METER)));
  }

  @Override
  public void endContact(Contact contact) {}

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {}

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {}
}
