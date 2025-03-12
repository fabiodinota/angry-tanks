package com.angrytanks.entity.custom.tank.components;


import com.angrytanks.entity.Actor;
import com.angrytanks.util.Constant;
import com.angrytanks.util.ConvexDecomposer;
import com.angrytanks.util.Decomposable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.angrytanks.util.PolygonUtil;



public class TankHull extends Actor implements Decomposable {

    private Body physicsBody;
    private final Polygon mergedVisualPolygon;
    private final List<Point2D> hullVertices;
    private final List<List<Point2D>> convexParts;
    private final Group subPolysGroup;
    private boolean showingOutline = false;

    public TankHull(List<Point2D> hullVertices, Color fill) {

        super(hullVertices.isEmpty() ? 0 : hullVertices.get(0).getX(),
                hullVertices.isEmpty() ? 0 : hullVertices.get(0).getY());

        this.hullVertices = new ArrayList<>(hullVertices);


        PolygonUtil.ensureClockwiseOrder(this.hullVertices);

        convexParts = ConvexDecomposer.decompose(this.hullVertices);
        if (convexParts.isEmpty()) {
            convexParts.add(new ArrayList<>(this.hullVertices));
        }
        System.out.println("[TankHull] Convex parts count: " + convexParts.size());
        for (List<Point2D> part : convexParts) {
            System.out.println("[TankHull] Part vertices count: " + part.size());
        }

        mergedVisualPolygon = new Polygon();
        for (Point2D pt : this.hullVertices) {
            mergedVisualPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        mergedVisualPolygon.setFill(fill != null ? fill : Color.GRAY);

        subPolysGroup = new Group();

        visuals.getChildren().add(mergedVisualPolygon);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
        if (hullVertices.size() < 3) {
            System.err.println("no vertices.");
            return;
        }
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set((float)(position.getX() / Constant.SCALE),
                (float)(position.getY() / Constant.SCALE));
        physicsBody = physicsWorld.createBody(bd);
        physicsBody.setUserData(this);

        // For each convex part, create a fixture.
        // Note: Box2D supports up to 8 vertices per fixture. If needed, further decomposition/simplification is required.
        for (List<Point2D> part : convexParts) {
            int count = Math.min(part.size(), 8);
            Vec2[] vecs = new Vec2[count];
            for (int i = 0; i < count; i++) {
                Point2D p = part.get(i);
                vecs[i] = new Vec2((float)(p.getX() / Constant.SCALE),
                        (float)(p.getY() / Constant.SCALE));
            }
            PolygonShape shape = new PolygonShape();
            try {
                shape.set(vecs, count);
            } catch (ArrayIndexOutOfBoundsException e) {

                e.printStackTrace();
            }

            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 1.0f;
            fd.friction = 0.3f;
            physicsBody.createFixture(fd);
        }
    }

    @Override
    public void render() {
        if (physicsBody != null) {
            float xPos = physicsBody.getPosition().x * (float) Constant.SCALE;
            float yPos = physicsBody.getPosition().y * (float) Constant.SCALE;
            setPosition(new Point2D(xPos, yPos));
            double angleDegrees = Math.toDegrees(physicsBody.getAngle());
            if (!showingOutline) {
                mergedVisualPolygon.setLayoutX(xPos);
                mergedVisualPolygon.setLayoutY(yPos);
                mergedVisualPolygon.setRotate(angleDegrees);
            } else {
                subPolysGroup.setLayoutX(xPos);
                subPolysGroup.setLayoutY(yPos);
                subPolysGroup.setRotate(angleDegrees);
            }
        }
    }

    @Override
    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));
        if (physicsBody != null) {
            physicsBody.setTransform(
                    new Vec2((float)(newX / Constant.SCALE), (float)(newY / Constant.SCALE)),
                    physicsBody.getAngle()
            );
            physicsBody.setAwake(true);
        }
    }

    @Override
    public void showDecompositionOutline() {
        if (showingOutline) return;
        showingOutline = true;
        visuals.getChildren().remove(mergedVisualPolygon);
        subPolysGroup.getChildren().clear();
        for (List<Point2D> part : convexParts) {
            Polygon outline = new Polygon();
            for (Point2D pt : part) {
                outline.getPoints().addAll(pt.getX(), pt.getY());
            }
            outline.setFill(null);
            outline.setStroke(Color.RED);
            outline.setStrokeWidth(2);
            subPolysGroup.getChildren().add(outline);
        }
        visuals.getChildren().add(subPolysGroup);
    }

    @Override
    public void hideDecompositionOutline() {
        if (!showingOutline) return;
        showingOutline = false;
        visuals.getChildren().remove(subPolysGroup);
        if (!visuals.getChildren().contains(mergedVisualPolygon)) {
            visuals.getChildren().add(mergedVisualPolygon);
        }
    }

    public Body getPhysicsBody() {
        return physicsBody;
    }
}