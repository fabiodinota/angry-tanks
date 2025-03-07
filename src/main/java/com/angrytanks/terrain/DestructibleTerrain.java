package com.angrytanks.terrain;

import com.angrytanks.entity.Actor;
import com.angrytanks.util.ConvexDecomposer;
import com.angrytanks.util.Decomposable;
import com.angrytanks.util.CraterParameters;
import com.angrytanks.util.PolygonUtil;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;


public abstract class DestructibleTerrain extends Actor implements Decomposable {

    protected List<Point2D> vertices;
    protected List<List<Point2D>> convexParts;
    protected Color fill;
    protected World worldRef;
    protected Shape mergedVisual;
    protected final Group visualGroup = new Group();
    protected CraterParameters craterParameters = new CraterParameters(50, 20, 0, 20);

    public DestructibleTerrain(List<Point2D> vertices, Color fill) {
        super(vertices.get(0).getX(), vertices.get(0).getY());
        this.vertices = ConvexDecomposer.normalizePolygon(vertices);
        PolygonUtil.ensureClockwiseOrder(this.vertices);
        this.fill = (fill != null) ? fill : Color.GRAY;

        rebuildVisuals();
    }

    protected void rebuildVisuals() {
        convexParts = ConvexDecomposer.decompose(vertices);
        if (convexParts.isEmpty()) {
            convexParts = new ArrayList<>();
            convexParts.add(new ArrayList<>(vertices));
        }
        mergedVisual = ConvexDecomposer.mergeConvexParts(convexParts, fill);
        visualGroup.getChildren().clear();
        visualGroup.getChildren().add(mergedVisual);
        visuals.getChildren().clear();
        visuals.getChildren().add(visualGroup);
    }


    public void rebuild(World world) {
        PolygonUtil.ensureClockwiseOrder(vertices);
        convexParts = ConvexDecomposer.decompose(vertices);
        if (convexParts.isEmpty()) {
            convexParts = new ArrayList<>();
            convexParts.add(new ArrayList<>(vertices));
        }
        mergedVisual = ConvexDecomposer.mergeConvexParts(convexParts, fill);
        visuals.getChildren().clear();
        visuals.getChildren().add(mergedVisual);
        if (getPhysicsBody() != null && world != null) {
            world.destroyBody((Body) getPhysicsBody());
        }
        addToPhysics(world);
    }

    public List<Point2D> getVertices() {
        return vertices;
    }

    public void setVertices(List<Point2D> newVerts) {
        this.vertices = newVerts;
    }

    public CraterParameters getCraterParameters() {
        return craterParameters;
    }

    public void setCraterParameters(CraterParameters craterParameters) {
        this.craterParameters = craterParameters;
    }

    public abstract Object getPhysicsBody();

    @Override
    public abstract void addToPhysics(World jbox2dWorld);

    @Override
    public void showDecompositionOutline() {
        visuals.getChildren().clear();
        Group outlineGroup = new Group();
        for (List<Point2D> part : convexParts) {
            javafx.scene.shape.Polygon partPoly = new javafx.scene.shape.Polygon();
            for (Point2D pt : part) {
                partPoly.getPoints().addAll(pt.getX(), pt.getY());
            }
            partPoly.setFill(Color.TRANSPARENT);
            partPoly.setStroke(Color.RED);
            partPoly.setStrokeWidth(2);
            outlineGroup.getChildren().add(partPoly);
        }
        visuals.getChildren().add(outlineGroup);
    }

    @Override
    public void hideDecompositionOutline() {
        visuals.getChildren().clear();
        visuals.getChildren().add(mergedVisual);
    }
}
