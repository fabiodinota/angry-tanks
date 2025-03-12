package com.angrytanks.entity.custom.tank.components;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;



public class TankData {
    private List<Point2D> hullVertices = new ArrayList<>();
    private Color hullColor;

    private List<Point2D> tracksVertices = new ArrayList<>();
    private Color tracksColor;

    private List<Point2D> decorVertices = new ArrayList<>();
    private Color decorColor;



    public List<Point2D> getDecorVertices() {
        return decorVertices;
    }
    public List<Point2D> setDecorVertices(List<Point2D> decorVertices) {
        return this.decorVertices = decorVertices;
    }

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

    public List<Point2D> getTracksVertices() {
        return tracksVertices;
    }

    public void setTracksVertices(List<Point2D> tracksVertices) {
        this.tracksVertices = tracksVertices;
    }

    public Color getTracksColor() {
        return tracksColor;
    }

    public void setTracksColor(Color tracksColor) {
        this.tracksColor = tracksColor;
    }

    public void setDecorColor(Color fillColor) {
        this.decorColor = fillColor;
    }
}