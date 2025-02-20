package com.angrytanks.core;

import com.angrytanks.entity.custom.Tank;
import com.angrytanks.world.GameWorld;
import com.angrytanks.world.Landscape;

import java.util.ArrayList;
import java.util.List;


public class GameState {

    public static GameWorld world;
    public static final List<Tank> players = new ArrayList<>();
    public static boolean gameOver = false;
    public static Tank winner;


    public static void initialize() {
        if (world == null) {
            world = new GameWorld();
            Landscape ground = new Landscape(0, 550, 800, 50);
            world.addLandscape(ground);
        }
        players.clear();
        gameOver = false;
        winner = null;
    }

    public static void setupPlayers(int numPlayers) {
        players.clear();
        double spacing = 150;
        double startX = 10;

        for (int i = 0; i < numPlayers; i++) {
            double xPosition = startX + (i * spacing);
            Tank tank = new Tank(xPosition, 100);
            addPlayer(tank);
        }
    }


    public static void addPlayer(Tank tank) {
        players.add(tank);
        if (world != null) {
            world.addObject(tank);
        }
    }



    public static void update() {
        if (world != null) {
            world.update();
        }
    }

    public static void reset() {
        for (Tank tank : players) {
            tank.teleport(tank.getPosition().getX(), 100);
        }
    }
}
