package com.angrytanks.core;

import com.angrytanks.entity.custom.Projectile;
import com.angrytanks.entity.custom.Tank;
import com.angrytanks.world.GameWorld;
import com.angrytanks.world.MapLayout;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public static GameWorld world;
    public static final List<Tank> players = new ArrayList<>();
    public static boolean gameOver = false;
    public static Tank winner;
    public static MapLayout mapLayout;


    public static void initialize() {
        if (world == null) {
            world = new GameWorld();
        }

        mapLayout = new MapLayout();

        mapLayout.loadFromSVG("/maps/map2.svg");




        for (var actor : mapLayout.getAllMapActors()) {
            world.addActor(actor);
        }

        players.clear();
        gameOver = false;
        winner = null;
    }

    public static void setupPlayers(int numPlayers) {
        players.clear();
        double spacing = 150;
        double startX = 800;
        for (int i = 0; i < numPlayers; i++) {
            double xPosition = startX + (i * spacing);
            Tank tank = new Tank(xPosition, 20);
           Projectile projectile = new Projectile(800, 100);
//            Projectile projectile1 = new Projectile(xPosition, 200);
//            Projectile projectile2 = new Projectile(xPosition, 400);
//            Projectile projectile3 = new Projectile(xPosition, 300);
            world.addActor(projectile);
//            world.addActor(projectile1);
//            world.addActor(projectile2);
//            world.addActor(projectile3);


            addPlayer(tank);
        }
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
}
