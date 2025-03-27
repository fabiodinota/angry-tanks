package com.angrytanks.core;

import com.angrytanks.entity.custom.Projectile;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.entity.custom.tank.components.TankData;
import com.angrytanks.util.SVGTankLoader;
import com.angrytanks.world.GameWorld;
import com.angrytanks.world.MapLayout;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public static GameWorld world;
    public static final List<Tank> players = new ArrayList<>();
    public static boolean gameOver = false;
    public static Tank winner;
    public static MapLayout mapLayout;
    private static int currentPlayerIndex;

    public static void initialize() {
        if (world != null) {
            world.clearActors();

        }
        world = new GameWorld();
        mapLayout = new MapLayout();

        mapLayout.loadFromSVG("/maps/Desert.svg");

        for (var actor : mapLayout.getAllMapActors()) {
            world.addActor(actor);
        }

        players.clear();
        gameOver = false;
        winner = null;
    }

    public static void setupPlayers(int numPlayers,  String tankAResource, String tankBResource) {
        players.clear();

        for (int i = 0; i < numPlayers; i   ++) {
            String resourcePath = (i == 0) ? tankAResource : tankBResource;
            TankData data = SVGTankLoader.loadTankData(resourcePath, true);

            double spawnX = (i == 0) ? 600.0 : 1150.0;
            double spawnY = 500.0;
            Tank tank = new Tank(data, spawnX, spawnY, (i == 1));

            addPlayer(tank);


//            Projectile projectile1 = new Projectile(700, 200);
//            Projectile projectile2 = new Projectile(740, 200);
//            Projectile projectile3 = new Projectile(740, 200);
//

//            world.addActor(projectile1);
//            world.addActor(projectile2);
//            world.addActor(projectile3);






        }
        currentPlayerIndex = 0;

    }

    public static void addPlayer(Tank tank) {
        players.add(tank);
        world.addActor(tank);
    }

    public static void update() {
        if (world != null) {
            world.update();
        }
    }

    public static MapLayout getMapLayout() {
        return mapLayout;
    }

    public static void reset() {
        for (Tank tank : players) {
            tank.teleport(tank.getPosition().getX(), 100);
        }
    }
    public static GameWorld getWorld() {
        return world;
    };

    public static Tank getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public static void switchTurn() {
        if (currentPlayerIndex == 0) {
            currentPlayerIndex = 1;
        } else {
            currentPlayerIndex = 0;
        }
    }



}
