package com.angrytanks.entity.custom.tank.components;

import com.angrytanks.entity.Decoration;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.dynamics.World;

import java.util.List;

public class TankWheel extends Decoration {

    private final Polygon wheelPolygon;

    public TankWheel(List<Point2D> vertices, Color fill) {
        super(vertices.isEmpty() ? 0 : vertices.get(0).getX(),
                vertices.isEmpty() ? 0 : vertices.get(0).getY());
        wheelPolygon = new Polygon();
        for (Point2D pt : vertices) {
            wheelPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        wheelPolygon.setFill(fill != null ? fill : Color.BLACK);
        visuals.getChildren().add(wheelPolygon);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
    }

    @Override
    public void render() {
        wheelPolygon.setLayoutX(0);
        wheelPolygon.setLayoutY(0);
    }

    @Override
    public void teleport(double newX, double newY) {
        wheelPolygon.setTranslateX(0);
        wheelPolygon.setTranslateY(0);
    }
}
