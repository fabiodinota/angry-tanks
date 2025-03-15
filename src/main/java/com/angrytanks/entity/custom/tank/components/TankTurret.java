package com.angrytanks.entity.custom.tank.components;

import com.angrytanks.entity.Decoration;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.dynamics.World;
import java.util.List;

public class TankTurret extends Decoration {

    private final Polygon turretPolygon;

    public TankTurret(List<Point2D> vertices, Color fill) {
        super(vertices.isEmpty() ? 0 : vertices.get(0).getX(),
                vertices.isEmpty() ? 0 : vertices.get(0).getY());
        turretPolygon = new Polygon();
        for (Point2D pt : vertices) {
            turretPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        turretPolygon.setFill(fill != null ? fill : Color.DARKSLATEGRAY);
        visuals.getChildren().add(turretPolygon);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
    }

    @Override
    public void render() {
        turretPolygon.setLayoutX(0);
        turretPolygon.setLayoutY(0);
    }

    @Override
    public void teleport(double newX, double newY) {
        turretPolygon.setTranslateX(0);
        turretPolygon.setTranslateY(0);
    }
}