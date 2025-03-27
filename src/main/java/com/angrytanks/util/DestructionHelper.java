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

    /**
     * Subtracts an ellipse (representing a crater) from a given polygon (representing terrain).
     * <p>
     * Steps:
     * 1. Convert the input terrain polygon from Point2D to JTS Coordinates.
     * 2. Ensure the terrain polygon is closed (first coordinate equals last coordinate).
     * 3. Create a JTS Polygon from the terrain coordinates.
     * 4. Generate ellipse coordinates by sampling points along the ellipse perimeter
     *    using the provided radii, rotation, and number of vertices.
     * 5. Create a JTS Polygon for the ellipse.
     * 6. Compute the difference between the terrain and ellipse using a snap overlay operation.
     *    If a TopologyException occurs, buffer both geometries slightly before subtracting.
     * 7. Clean the resulting geometry by buffering with zero distance.
     * 8. Convert the resulting geometry back to a list of JavaFX Point2D lists.
     *
     * @param polygon    List of Point2D representing the terrain.
     * @param center     Center of the ellipse.
     * @param radiusX    Horizontal radius of the ellipse.
     * @param radiusY    Vertical radius of the ellipse.
     * @param rotation   Rotation (in radians) of the ellipse.
     * @param numVertices Number of vertices to approximate the ellipse.
     * @return A list of polygons (each as a list of Point2D) resulting after subtraction.
     */
    public static List<List<Point2D>> subtractEllipseFromPolygon(List<Point2D> polygon,
                                                                 Point2D center,
                                                                 double radiusX, double radiusY,
                                                                 double rotation,
                                                                 int numVertices) {
        // Create a GeometryFactory for building JTS geometries.
        GeometryFactory geomFactory = new GeometryFactory();

        // Convert the input polygon (terrain) to JTS Coordinates.
        List<Coordinate> terrainCoords = new ArrayList<>();
        for (Point2D p : polygon) {
            terrainCoords.add(new Coordinate(p.getX(), p.getY()));
        }
        // Ensure the terrain polygon is closed (first and last coordinates must match).
        if (!terrainCoords.get(0).equals2D(terrainCoords.get(terrainCoords.size() - 1))) {
            terrainCoords.add(terrainCoords.get(0));
        }
        // Create a JTS Polygon for the terrain.
        Polygon terrainPoly = geomFactory.createPolygon(terrainCoords.toArray(new Coordinate[0]));

        // Generate coordinates for the ellipse approximation.
        Coordinate[] ellipseCoords = new Coordinate[numVertices + 1];
        for (int i = 0; i < numVertices; i++) {
            // Calculate angle for current vertex.
            double theta = 2 * Math.PI * i / numVertices;
            // Compute ellipse coordinates before rotation.
            double x = radiusX * Math.cos(theta);
            double y = radiusY * Math.sin(theta);
            // Apply rotation to the point.
            double xr = x * Math.cos(rotation) - y * Math.sin(rotation);
            double yr = x * Math.sin(rotation) + y * Math.cos(rotation);
            // Translate the point to the ellipse's center.
            ellipseCoords[i] = new Coordinate(center.getX() + xr, center.getY() + yr);
        }
        // Close the ellipse by repeating the first coordinate.
        ellipseCoords[numVertices] = ellipseCoords[0];
        // Create a JTS Polygon for the ellipse (crater).
        Polygon craterPoly = geomFactory.createPolygon(ellipseCoords);

        Geometry diff = null;
        try {
            // Subtract the crater from the terrain using SnapIfNeededOverlayOp for robustness.
            diff = SnapIfNeededOverlayOp.overlayOp(terrainPoly, craterPoly, OverlayOp.DIFFERENCE);
        } catch (TopologyException e) {
            // In case of topology issues, buffer both geometries slightly and try again.
            Geometry bufferedTerrain = terrainPoly.buffer(0.001);
            Geometry bufferedCrater = craterPoly.buffer(0.001);
            diff = SnapIfNeededOverlayOp.overlayOp(bufferedTerrain, bufferedCrater, OverlayOp.DIFFERENCE);
        }
        // Clean up the result by buffering with zero distance (removes small artifacts).
        Geometry cleaned = diff.buffer(0);

        // Convert the resulting geometry back to lists of Point2D.
        List<List<Point2D>> result = new ArrayList<>();
        // The result may be multipart, so iterate over each geometry.
        for (int i = 0; i < cleaned.getNumGeometries(); i++) {
            Geometry g = cleaned.getGeometryN(i);
            Coordinate[] coords = g.getCoordinates();
            List<Point2D> polyResult = new ArrayList<>();
            // Convert each JTS Coordinate back to JavaFX Point2D.
            for (Coordinate c : coords) {
                polyResult.add(new Point2D(c.x, c.y));
            }
            result.add(polyResult);
        }
        return result;
    }
}
