package com.angrytanks.util;

import com.angrytanks.entity.custom.tank.components.TankData;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads tank parts from a single SVG file using your SVGMapLoader approach.
 */
public class SVGTankLoader {

    public static TankData loadTankData(String resourcePath) {
        TankData tankData = new TankData();

        // Use your existing SVGMapLoader to load elements from the SVG.
        List<MapElement> elements = SVGMapLoader.loadMapElements(resourcePath);

        for (MapElement me : elements) {
            String shapeId = me.getType();
            Color fillColor = me.getFillColor();
            List<Point2D> rawVerts = me.getVertices();


            List<Point2D> shiftedVerts = shiftVerticesToLocalZero(rawVerts);


            System.out.println("Loaded shape: " + shapeId );
            if ("tank_hull".equalsIgnoreCase(shapeId)) {
                tankData.setHullVertices(shiftedVerts);
                tankData.setHullColor(fillColor);
            } else if ("tank_tracks".equalsIgnoreCase(shapeId)) {
                tankData.setTracksVertices(shiftedVerts);
                tankData.setTracksColor(fillColor);
            } else if ("tank_decor".equalsIgnoreCase(shapeId)) {
                tankData.setDecorVertices(shiftedVerts);
                tankData.setDecorColor(fillColor);
            }

        }
        return tankData;
    }

    private static List<Point2D> shiftVerticesToLocalZero(List<Point2D> verts) {
        if (verts.isEmpty()) return verts;
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        for (Point2D p : verts) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getY() < minY) minY = p.getY();
        }
        List<Point2D> shifted = new ArrayList<>();
        for (Point2D p : verts) {
            shifted.add(new Point2D(p.getX() - minX, p.getY() - minY));
        }
        return shifted;
    }
}