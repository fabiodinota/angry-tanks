package com.angrytanks.util;

public class CraterParameters {
    private double radiusX;
    private double radiusY;
    private double rotation;
    private int ellipseVertices;

    public CraterParameters(double radiusX, double radiusY, double rotation, int ellipseVertices) {
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.rotation = rotation;
        this.ellipseVertices = ellipseVertices;
    }

    public double getRadiusX() { return radiusX; }
    public double getRadiusY() { return radiusY; }
    public double getRotation() { return rotation; }
    public int getEllipseVertices() { return ellipseVertices; }

    public void setRadiusX(double radiusX) { this.radiusX = radiusX; }
    public void setRadiusY(double radiusY) { this.radiusY = radiusY; }
    public void setRotation(double rotation) { this.rotation = rotation; }
    public void setEllipseVertices(int ellipseVertices) { this.ellipseVertices = ellipseVertices; }
}
