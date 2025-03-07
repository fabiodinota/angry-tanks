package com.angrytanks.terrain.elements;

import com.angrytanks.entity.Decoration;
import com.angrytanks.util.Decomposable;
import com.angrytanks.util.ConvexDecomposer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.dynamics.World;

import java.util.List;


public class Decor extends Decoration implements Decomposable {

    private final Polygon decorVisual;
    private List<Point2D> vertices;

    public Decor(double x, double y, List<Point2D> vertices, Color fill) {
        super(x, y);
        this.vertices = vertices;

        decorVisual = new Polygon();
        for (Point2D pt : vertices) {
            decorVisual.getPoints().addAll(pt.getX(), pt.getY());
        }

        if (fill != null) {
            decorVisual.setFill(fill);
        } else {
            decorVisual.setFill(Color.BEIGE);
        }

        visuals.getChildren().add(decorVisual);
    }

    @Override
    public void addToPhysics(World jbox2dWorld) {
    }

    @Override
    public void render() {
    }

    @Override
    public void teleport(double newX, double newY) {

        setPosition(new Point2D(newX, newY));
    }

    @Override
    public void showDecompositionOutline() {
        List<List<Point2D>> convexParts = ConvexDecomposer.decompose(vertices);
        if (convexParts.isEmpty()) {
            convexParts.add(vertices);
        }
        visuals.getChildren().clear();
        visuals.getChildren().add(decorVisual);

        for (List<Point2D> part : convexParts) {
            Polygon outline = new Polygon();
            for (Point2D pt : part) {
                outline.getPoints().addAll(pt.getX(), pt.getY());
            }
            outline.setFill(null);
            outline.setStroke(Color.RED);
            outline.setStrokeWidth(2);
            visuals.getChildren().add(outline);
        }
    }

    @Override
    public void hideDecompositionOutline() {
        visuals.getChildren().clear();
        visuals.getChildren().add(decorVisual);
    }
}
