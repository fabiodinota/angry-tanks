package com.angrytanks.entity;

import javafx.geometry.Point2D;
import javafx.scene.Group;


public abstract class Actor {

    protected Point2D position;
    protected final Group visuals;

    public Actor(double x, double y) {
        this.position = new Point2D(x, y);
        this.visuals = new Group();
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }


    public Group getVisuals() {
        return visuals;
    }

    public abstract void render();
}
