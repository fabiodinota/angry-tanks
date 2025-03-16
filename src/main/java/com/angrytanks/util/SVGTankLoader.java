package com.angrytanks.util;

import com.angrytanks.entity.custom.tank.components.TankData;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SVGTankLoader {

    public static TankData loadTankData(String resourcePath, boolean shiftToLocalZero) {
        TankData tankData = new TankData();
        List<MapElement> elements = SVGMapLoader.loadMapElements(resourcePath);

        double globalMinX = 0;
        double globalMinY = 0;
        if (shiftToLocalZero) {
            globalMinX = Double.POSITIVE_INFINITY;
            globalMinY = Double.POSITIVE_INFINITY;
            for (MapElement me : elements) {
                for (Point2D p : me.getVertices()) {
                    if (p.getX() < globalMinX) globalMinX = p.getX();
                    if (p.getY() < globalMinY) globalMinY = p.getY();
                }
            }
        }

        for (MapElement me : elements) {
            String shapeId = me.getType();

            String normalizedId = shapeId.toLowerCase().replaceAll("(_\\d+)+_?$", "");

            Color fillColor = me.getFillColor();
            List<Point2D> rawVerts = me.getVertices();
            List<Point2D> finalVerts = shiftToLocalZero ? shiftVertices(rawVerts, globalMinX, globalMinY) : rawVerts;

            if ("tank_hull".equals(normalizedId)) {

                tankData.setHullVertices(finalVerts);
                tankData.setHullColor(fillColor);
            } else if ("tank_track".equals(normalizedId)) {
                tankData.setTrackVertices(finalVerts);
                tankData.setTrackColor(fillColor);
            } else if ("tank_wheel".equals(normalizedId)) {
                tankData.getWheelVertices().add(finalVerts);
                if (tankData.getWheelColor() == null && fillColor != null) {
                    tankData.setWheelColor(fillColor);
                }
            } else if ("tank_decor".equals(normalizedId)) {
                tankData.setDecorVertices(finalVerts);
                tankData.setDecorColor(fillColor);
            } else if ("tank_turret".equals(normalizedId)) {
                tankData.setTurretVertices(finalVerts);
                tankData.setTurretColor(fillColor);
            } else if ("tank_cannon".equals(normalizedId)) {
                tankData.setCannonVertices(finalVerts);
                tankData.setCannonColor(fillColor);
            } else if ("cannon_anchor".equals(normalizedId)) {
                tankData.setCannonAnchor(finalVerts);
            }
        }

        return tankData;
    }

    private static List<Point2D> shiftVertices(List<Point2D> verts, double minX, double minY) {
        List<Point2D> shifted = new ArrayList<>();
        for (Point2D p : verts) {
            shifted.add(new Point2D(p.getX() - minX, p.getY() - minY));
        }
        return shifted;
    }
}
