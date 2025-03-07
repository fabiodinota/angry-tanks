package com.angrytanks.entity;


import org.jbox2d.dynamics.World;

public abstract class Decoration extends Actor {
    public Decoration(double x, double y) {
        super(x, y);
    }

    @Override
    public abstract void render();

    @Override
    public abstract void addToPhysics(World physicsWorld);

    @Override
    public abstract void teleport(double newX, double newY);
}
