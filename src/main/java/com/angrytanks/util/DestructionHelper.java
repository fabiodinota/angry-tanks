package com.angrytanks.util;

import javafx.geometry.Point2D;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.overlay.snap.SnapIfNeededOverlayOp;
import org.locationtech.jts.geom.TopologyException;

import java.util.ArrayList;
import java.util.List;

public class DestructionHelper {

    //made with help of chatgpt and stackoverflow ;)
    public static List<List<Point2D>> subtractEllipseFromPolygon(List<Point2D> polygon,
                                                                 Point2D center,
                                                                 double radiusX, double radiusY,
                                                                 double rotation,
                                                                 int numVertices) {
        GeometryFactory geomFactory = new GeometryFactory();

        List<Coordinate> terrainCoords = new ArrayList<>();
        for (Point2D p : polygon) {
            terrainCoords.add(new Coordinate(p.getX(), p.getY()));
        }
        if (!terrainCoords.get(0).equals2D(terrainCoords.get(terrainCoords.size() - 1))) {
            terrainCoords.add(terrainCoords.get(0));
        }
        Polygon terrainPoly = geomFactory.createPolygon(terrainCoords.toArray(new Coordinate[0]));

        Coordinate[] ellipseCoords = new Coordinate[numVertices + 1];
        for (int i = 0; i < numVertices; i++) {
            double theta = 2 * Math.PI * i / numVertices;
            double x = radiusX * Math.cos(theta);
            double y = radiusY * Math.sin(theta);
            double xr = x * Math.cos(rotation) - y * Math.sin(rotation);
            double yr = x * Math.sin(rotation) + y * Math.cos(rotation);
            ellipseCoords[i] = new Coordinate(center.getX() + xr, center.getY() + yr);
        }
        ellipseCoords[numVertices] = ellipseCoords[0]; // Close the ellipse.
        Polygon craterPoly = geomFactory.createPolygon(ellipseCoords);

        Geometry diff = null;
        try {
            diff = SnapIfNeededOverlayOp.overlayOp(terrainPoly, craterPoly, OverlayOp.DIFFERENCE);
        } catch (TopologyException e) {
            Geometry bufferedTerrain = terrainPoly.buffer(0.001);
            Geometry bufferedCrater = craterPoly.buffer(0.001);
            diff = SnapIfNeededOverlayOp.overlayOp(bufferedTerrain, bufferedCrater, OverlayOp.DIFFERENCE);
        }
        Geometry cleaned = diff.buffer(0);



        List<List<Point2D>> result = new ArrayList<>();
        for (int i = 0; i < cleaned.getNumGeometries(); i++) {
            Geometry g = cleaned.getGeometryN(i);
            Coordinate[] coords = g.getCoordinates();
            List<Point2D> polyResult = new ArrayList<>();
            for (Coordinate c : coords) {
                polyResult.add(new Point2D(c.x, c.y));
            }
            result.add(polyResult);
        }
        return result;
    }
}
