package com.angrytanks.entity.custom.tank.components;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class TankData {
    private List<Point2D> hullVertices = new ArrayList<>();
    private Color hullColor;

    private List<Point2D> cannonVertices = new ArrayList<>();
    private Color cannonColor;

    private List<Point2D> trackVertices = new ArrayList<>();
    private Color trackColor;

    private List<List<Point2D>> wheelVertices = new ArrayList<>();
    private Color wheelColor;

    private List<Point2D> decorVertices = new ArrayList<>();
    private Color decorColor;

    private List<Point2D> turretVertices = new ArrayList<>();
    private Color turretColor;

    private List<Point2D> cannonPathPoints  = new ArrayList<>();


    // getters / setters ...
    public List<Point2D> getHullVertices() {
        return hullVertices;
    }
    public void setHullVertices(List<Point2D> hullVertices) {
        this.hullVertices = hullVertices;
    }
    public Color getHullColor() {
        return hullColor;
    }
    public void setHullColor(Color hullColor) {
        this.hullColor = hullColor;
    }
    public List<Point2D> getTrackVertices() {
        return trackVertices;
    }
    public void setTrackVertices(List<Point2D> trackVertices) {
        this.trackVertices = trackVertices;
    }
    public Color getTrackColor() {
        return trackColor;
    }
    public void setTrackColor(Color trackColor) {
        this.trackColor = trackColor;
    }
    public List<List<Point2D>> getWheelVertices() {
        return wheelVertices;
    }
    public void setWheelVertices(List<List<Point2D>> wheelVertices) {
        this.wheelVertices = wheelVertices;
    }
    public Color getWheelColor() {
        return wheelColor;
    }
    public void setWheelColor(Color wheelColor) {
        this.wheelColor = wheelColor;
    }
    public List<Point2D> getDecorVertices() {
        return decorVertices;
    }
    public void setDecorVertices(List<Point2D> decorVertices) {
        this.decorVertices = decorVertices;
    }
    public Color getDecorColor() {
        return decorColor;
    }
    public void setDecorColor(Color decorColor) {
        this.decorColor = decorColor;
    }
    public List<Point2D> getTurretVertices() {
        return turretVertices;
    }
    public void setTurretVertices(List<Point2D> turretVertices) {
        this.turretVertices = turretVertices;
    }
    public Color getTurretColor() {
        return turretColor;
    }
    public void setTurretColor(Color turretColor) {
        this.turretColor = turretColor;
    }

    public List<Point2D> getCannonVertices() {
        return cannonVertices;
    }

    public void setCannonVertices(List<Point2D> cannonVertices) {
        this.cannonVertices = cannonVertices;
    }

    public Color getCannonColor() {
        return cannonColor;
    }

    public void setCannonColor(Color cannonColor) {
        this.cannonColor = cannonColor;
    }

    public List<Point2D> getCannonPath() {
        return cannonPathPoints;
    }

    public void setCannonPath(List<Point2D> cannonPathPoints) {
        this.cannonPathPoints = cannonPathPoints;
    }

}
