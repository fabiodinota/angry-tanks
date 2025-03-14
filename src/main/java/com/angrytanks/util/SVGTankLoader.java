package com.angrytanks.util;

import com.angrytanks.entity.custom.tank.components.TankData;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SVGTankLoader {

    /**
     * Loads tank parts from the given SVG.
     *
     * @param resourcePath      e.g. "/tanks/tank1.svg"
     * @param shiftToLocalZero  if true, all vertices are shifted so that the overall tank’s bounding box starts at (0,0);
     *                          if false, the original SVG coordinates are preserved.
     * @return a TankData object with hull, track, wheel, and decor vertices/colors set.
     */
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
            String shapeId = me.getType(); // expected: "tank_hull", "tank_track", "tank_wheel", "tank_decor"
            Color fillColor = me.getFillColor();
            List<Point2D> rawVerts = me.getVertices();
            List<Point2D> finalVerts = shiftToLocalZero ? shiftVertices(rawVerts, globalMinX, globalMinY) : rawVerts;

            if ("tank_hull".equalsIgnoreCase(shapeId)) {
                System.out.println("SVGTankLoader: Loading TankHull");
                tankData.setHullVertices(finalVerts);
                tankData.setHullColor(fillColor);
            } else if ("tank_track".equalsIgnoreCase(shapeId)) {
                System.out.println("SVGTankLoader: Loading TankTrack");
                tankData.setTrackVertices(finalVerts);
                tankData.setTrackColor(fillColor);
            } else if ("tank_wheel".equalsIgnoreCase(shapeId)) {
                System.out.println("SVGTankLoader: Loading TankWheel");
                tankData.getWheelVertices().add(finalVerts);
                if (tankData.getWheelColor() == null && fillColor != null) {
                    tankData.setWheelColor(fillColor);
                }
            } else if ("tank_decor".equalsIgnoreCase(shapeId)) {
                System.out.println("SVGTankLoader: Loading TankDecor");
                tankData.setDecorVertices(finalVerts);
                tankData.setDecorColor(fillColor);
            } else if ("tank_turret".equalsIgnoreCase(shapeId)) {
                System.out.println("SVGTankLoader: Loading TankTurret");
                tankData.setTurretVertices(finalVerts);
                tankData.setTurretColor(fillColor);
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
