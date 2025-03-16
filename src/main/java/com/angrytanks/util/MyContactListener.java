package com.angrytanks.util;

import com.angrytanks.entity.Actor;
import com.angrytanks.entity.custom.Projectile;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.terrain.DestructibleTerrain;
import com.angrytanks.world.PhysicsWorld;
import javafx.geometry.Point2D;
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
            System.out.println("Projectile from " + aA +
                    " hit enemy tank " + aB);

            Projectile proj = (Projectile) aA;
            Tank hitTank = (Tank) aB;
            if (proj.getShooter() != hitTank) {
                System.out.println("Projectile from " + proj.getShooter() +
                        " hit enemy tank " + hitTank);

            }
        } else if (aB instanceof Projectile && aA instanceof Tank) {
            Projectile proj = (Projectile) aB;
            Tank hitTank = (Tank) aA;
            if (proj.getShooter() != hitTank) {
                System.out.println("Projectile from " + proj.getShooter() +
                        " hit enemy tank " + hitTank);
            }
        }


    }

    @Override public void endContact(Contact contact) { }

    @Override public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override public void postSolve(Contact contact, ContactImpulse impulse) { }

    private void handleHit(Projectile proj, Actor groundActor) {
        Point2D impact = proj.getPosition();

        if (proj.physicsBody != null && proj.physicsBody.isActive()) {
            proj.physicsBody.getWorld().destroyBody(proj.physicsBody);
            proj.physicsBody = null;
        }



        if (groundActor instanceof DestructibleTerrain) {
            DestructibleTerrain terrain = (DestructibleTerrain) groundActor;
            CraterParameters params = terrain.getCraterParameters();


            List<List<Point2D>> updatedParts = new ArrayList<>();

            var newPolys = DestructionHelper.subtractEllipseFromPolygon(
                    terrain.getVertices(), impact,
                    params.getRadiusX(), params.getRadiusY(),
                    params.getRotation(), params.getEllipseVertices());

            if (!newPolys.isEmpty()) {
                terrain.setVertices(newPolys.get(0));
                World world = PhysicsWorld.getPhysicsWorld();
                if (world != null) {
                    terrain.rebuild(world);
                } else {
                }
            } else {
                System.out.println("1");
            }
        } else {
        }
    }

    private Actor extractActor(Fixture fix) {
        if (fix == null) return null;
        Object ud = fix.getBody().getUserData();
        return (ud instanceof Actor) ? (Actor) ud : null;
    }
}
