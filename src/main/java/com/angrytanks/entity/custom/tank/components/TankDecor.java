package com.angrytanks.entity.custom.tank.components;

import com.angrytanks.entity.Decoration;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.dynamics.World;
import java.util.List;

public class TankDecor extends Decoration {

    private final Polygon decorPolygon;

    public TankDecor(List<Point2D> vertices, Color fill) {
        super(vertices.isEmpty() ? 0 : vertices.get(0).getX(),
                vertices.isEmpty() ? 0 : vertices.get(0).getY());
        decorPolygon = new Polygon();
        for (Point2D pt : vertices) {
            decorPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        decorPolygon.setFill(fill != null ? fill : Color.WHITE);
        visuals.getChildren().add(decorPolygon);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
    }

    @Override
    public void render() {
        decorPolygon.setLayoutX(0);
        decorPolygon.setLayoutY(0);
    }

    @Override
    public void teleport(double newX, double newY) {
        decorPolygon.setTranslateX(0);
        decorPolygon.setTranslateY(0);
    }
}
