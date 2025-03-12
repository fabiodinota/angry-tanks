package com.angrytanks.entity.custom.tank;

import com.angrytanks.entity.Actor;


import com.angrytanks.entity.custom.tank.components.TankData;
import com.angrytanks.entity.custom.tank.components.TankDecor;
import com.angrytanks.entity.custom.tank.components.TankHull;
import com.angrytanks.entity.custom.tank.components.TankTracks;
import com.angrytanks.util.Decomposable;
import org.jbox2d.dynamics.World;

public class Tank extends Actor implements Decomposable {

    private TankHull hull;
    private TankTracks tracks; // May be null if no tracks are defined
    private TankDecor decor; // May be null if no decor is defined


    public Tank(TankData tankData, double spawnX, double spawnY) {
        super(spawnX, spawnY);

        hull = new TankHull(tankData.getHullVertices(), tankData.getHullColor());

        if (!tankData.getTracksVertices().isEmpty()) {
            tracks = new TankTracks(tankData.getTracksVertices().get(0).getX(),
                    tankData.getTracksVertices().get(0).getY());
        }

        if (hull != null) {
            visuals.getChildren().add(hull.getVisuals());
        }
        if (tracks != null) {
            visuals.getChildren().add(tracks.getVisuals());
        }

        if(decor != null) {
            visuals.getChildren().add(decor.getVisuals());
        }

        teleport(spawnX, spawnY);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
        if (hull != null) {
            hull.addToPhysics(physicsWorld);
        }
        if (tracks != null) {
            tracks.addToPhysics(physicsWorld);
        }
    }

    @Override
    public void render() {
        if (hull != null) {
            hull.render();
        }
        if (tracks != null) {
            tracks.render();
        }
        if (decor != null) {
            decor.render();
        }
    }

    @Override
    public void teleport(double newX, double newY) {
        if (hull != null) {
            hull.teleport(newX, newY);
        }
        if (tracks != null) {
            tracks.teleport(newX, newY);
        }
        if (decor != null) {
            decor.teleport(newX, newY);
        }
    }

    @Override
    public void showDecompositionOutline() {
        if (hull instanceof Decomposable) {
            ((Decomposable) hull).showDecompositionOutline();
        }
        if (tracks != null && tracks instanceof Decomposable) {
            ((Decomposable) tracks).showDecompositionOutline();
        }

    }

    @Override
    public void hideDecompositionOutline() {
        if (hull instanceof Decomposable) {
            ((Decomposable) hull).hideDecompositionOutline();
        }
        if (tracks != null && tracks instanceof Decomposable) {
            ((Decomposable) tracks).hideDecompositionOutline();
        }
    }
}