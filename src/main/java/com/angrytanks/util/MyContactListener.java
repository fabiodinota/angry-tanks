package com.angrytanks.util;

import com.angrytanks.core.GameState;
import com.angrytanks.entity.Actor;
import com.angrytanks.entity.custom.Projectile;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.hud.custom.InGame.HealthBar;
import com.angrytanks.terrain.DestructibleTerrain;
import com.angrytanks.ui.SceneManager;
import com.angrytanks.world.PhysicsWorld;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Actor aA = extractActor(contact.getFixtureA());
        Actor aB = extractActor(contact.getFixtureB());

        if (aA == null || aB == null) {
            return;
        }

        boolean isProjA = (aA instanceof Projectile);
        boolean isProjB = (aB instanceof Projectile);
        boolean isTerrainA = (aA instanceof DestructibleTerrain);
        boolean isTerrainB = (aB instanceof DestructibleTerrain);

        // Projectile vs Terrain
        if (isProjA && isTerrainB) {
            Projectile proj = (Projectile) aA;
            Actor ground = aB;
            PhysicsWorld.scheduleAction(() -> handleHit(proj, ground));
        } else if (isProjB && isTerrainA) {
            Projectile proj = (Projectile) aB;
            Actor ground = aA;
            PhysicsWorld.scheduleAction(() -> handleHit(proj, ground));
        }


        if (aA instanceof Projectile && aB instanceof Tank) {
            Projectile proj = (Projectile) aA;
            Tank hitTank = (Tank) aB;

            if (proj.getShooter() != hitTank) {
                if (proj.getShooter() == GameState.players.get(0)) {
                    HealthBar.hitPlayer2(15);
                    checkHealth(hitTank, proj);
                } else {
                    HealthBar.hitPlayer1(15);
                    checkHealth(hitTank, proj);
                }
            }

        } else if (aB instanceof Projectile && aA instanceof Tank) {
            Projectile proj = (Projectile) aB;
            Tank hitTank = (Tank) aA;

            if (proj.getShooter() != hitTank) {
                if (proj.getShooter() == GameState.players.get(0)) {
                    HealthBar.hitPlayer2(15);
                    checkHealth(hitTank, proj);
                } else {
                    HealthBar.hitPlayer1(15);
                    checkHealth(hitTank, proj);
                }
            }
        }
    }

    public void checkHealth(Tank hitTank, Projectile proj) {
        deleteProjectile(proj);

        if(HealthBar.getPlayer1Health() <= 0) {
            hitTank.detachTurret();
        } else if(HealthBar.getPlayer2Health() <= 0) {
            hitTank.detachTurret();
        }
    }

    public void deleteProjectile(Projectile proj){

        if (proj.physicsBody != null && proj.physicsBody.isActive()) {
            proj.physicsBody.getWorld().destroyBody(proj.physicsBody);
            proj.physicsBody = null;
        }
        GameState.getWorld().removeActor(proj);

    }
    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }

    private void handleHit(Projectile proj, Actor groundActor) {
        Point2D impact = proj.getPosition();

        deleteProjectile(proj);

        if (proj.physicsBody != null && proj.physicsBody.isActive()) {
            proj.physicsBody.getWorld().destroyBody(proj.physicsBody);
            proj.physicsBody = null;
        }

        if (groundActor instanceof DestructibleTerrain) {
            DestructibleTerrain terrain = (DestructibleTerrain) groundActor;
            CraterParameters params = terrain.getCraterParameters();

            List<List<Point2D>> newPolys = DestructionHelper.subtractEllipseFromPolygon(
                    terrain.getVertices(), impact,
                    params.getRadiusX(), params.getRadiusY(),
                    params.getRotation(), params.getEllipseVertices());

            if (!newPolys.isEmpty()) {
                terrain.setVertices(newPolys.get(0));
                World world = PhysicsWorld.getPhysicsWorld();
                if (world != null) {
                    terrain.rebuild(world);
                }
            }
        }
    }

    private Actor extractActor(Fixture fix) {
        if (fix == null) return null;
        Object ud = fix.getBody().getUserData();
        return (ud instanceof Actor) ? (Actor) ud : null;
    }
}
