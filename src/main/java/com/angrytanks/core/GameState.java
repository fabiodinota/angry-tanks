package com.angrytanks.core;

import com.angrytanks.entity.custom.Tank;
import com.angrytanks.world.World;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<Tank> players;
    private Tank currentPlayer;
    private World world;
    private Tank winner;
    private boolean gameOver;



    public GameState() {
        players = new ArrayList<>();
        world = new World();
        gameOver = false;
    }



}
