package com.angrytanks.entity.custom.tank.components;

import com.angrytanks.entity.Decoration;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class TankTracks extends Decoration {

    private final Polygon trackPolygon;
    private final Group wheelGroup;
    private final List<TankWheel> wheels;

    public TankTracks(List<Point2D> trackVertices, List<List<Point2D>> wheelVertices, Color trackColor, Color wheelColor) {
        super(trackVertices.isEmpty() ? 0 : trackVertices.get(0).getX(),
                trackVertices.isEmpty() ? 0 : trackVertices.get(0).getY());
        trackPolygon = new Polygon();
        for (Point2D pt : trackVertices) {
            trackPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        trackPolygon.setFill(trackColor != null ? trackColor : Color.DARKGRAY);

        wheels = new ArrayList<>();
        for (List<Point2D> wVerts : wheelVertices) {
            TankWheel wheel = new TankWheel(wVerts, wheelColor);
            wheels.add(wheel);
        }

        wheelGroup = new Group();
        for (TankWheel wheel : wheels) {
            wheelGroup.getChildren().add(wheel.getVisuals());
        }

        visuals.getChildren().addAll(trackPolygon, wheelGroup);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
    }

    @Override
    public void render() {
        trackPolygon.setLayoutX(0);
        trackPolygon.setLayoutY(0);
        wheelGroup.setLayoutX(0);
        wheelGroup.setLayoutY(0);
    }

    @Override
    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));
        trackPolygon.setLayoutX(0);
        trackPolygon.setLayoutY(0);
        wheelGroup.setLayoutX(0);
        wheelGroup.setLayoutY(0);
    }
}
