package com.angrytanks.util;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.List;

public class MapElement {
    private final String type;
    private final List<Point2D> vertices;
    private Color fillColor;

    public MapElement(String type, List<Point2D> vertices) {
        this.type = type;
        this.vertices = vertices;
    }

    public String getType() {
        return type;
    }

    public List<Point2D> getVertices() {
        return vertices;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
}
