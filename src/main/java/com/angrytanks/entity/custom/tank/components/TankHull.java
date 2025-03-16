package com.angrytanks.entity.custom.tank.components;

import com.angrytanks.entity.Actor;
import com.angrytanks.util.Constant;
import com.angrytanks.util.ConvexDecomposer;
import com.angrytanks.util.Decomposable;
import com.angrytanks.util.PolygonUtil;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

public class TankHull extends Actor implements Decomposable {

    private Body physicsBody;
    private final Polygon mergedVisualPolygon;
    private final List<Point2D> hullVertices;
    private final List<List<Point2D>> convexParts;
    private final Group subPolysGroup;
    private boolean showingOutline = false;


    public TankHull(List<Point2D> hullVertices, Color fill) {
        super(0, 0);
        this.hullVertices = new ArrayList<>(hullVertices);
        PolygonUtil.ensureClockwiseOrder(this.hullVertices);
        convexParts = ConvexDecomposer.decompose(this.hullVertices);
        if (convexParts.isEmpty()) {
            convexParts.add(new ArrayList<>(this.hullVertices));
        }
        mergedVisualPolygon = new Polygon();
        for (Point2D pt : this.hullVertices) {
            mergedVisualPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        mergedVisualPolygon.setFill(fill != null ? fill : Color.GRAY);
        mergedVisualPolygon.setLayoutX(0);
        mergedVisualPolygon.setLayoutY(0);
        subPolysGroup = new Group();
        visuals.getChildren().add(mergedVisualPolygon);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
        if (hullVertices.size() < 3) {

            return;
        }
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.bullet = true;
        bd.position.set((float)(getPosition().getX() / Constant.SCALE),
                (float)(getPosition().getY() / Constant.SCALE));
        physicsBody = physicsWorld.createBody(bd);
        physicsBody.setUserData(this);

        for (List<Point2D> part : convexParts) {
            int count = Math.min(part.size(), 8);
            Vec2[] vecs = new Vec2[count];
            for (int i = 0; i < count; i++) {
                Point2D p = part.get(i);
                vecs[i] = new Vec2((float)(p.getX() / Constant.SCALE),
                        (float)(p.getY() / Constant.SCALE));
            }
            PolygonShape shape = new PolygonShape();
            shape.set(vecs, count);
            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 1.0f;
            fd.friction = 0.5f;
            physicsBody.createFixture(fd);

        }
    }

    public double getHullWidth() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for (Point2D vertex : hullVertices) {
            double x = vertex.getX();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
        }
        return maxX - minX;
    }

    public double getHullHeight() {
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for (Point2D vertex : hullVertices) {
            double y = vertex.getY();
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        return maxY - minY;
    }

    @Override
    public void render() {
        if (physicsBody != null) {
            float xPos = physicsBody.getPosition().x * (float) Constant.SCALE;
            float yPos = physicsBody.getPosition().y * (float) Constant.SCALE;
            setPosition(new Point2D(xPos, yPos));
            double angleDegrees = Math.toDegrees(physicsBody.getAngle());
            if (!showingOutline) {
                mergedVisualPolygon.setLayoutX(0);
                mergedVisualPolygon.setLayoutY(0);
                mergedVisualPolygon.setRotate(angleDegrees);
            } else {
                subPolysGroup.setLayoutX(0);
                subPolysGroup.setLayoutY(0);
                subPolysGroup.setRotate(angleDegrees);
            }
            System.out.println("TankHull.render() - Physics pos: (" + xPos + ", " + yPos + "), angle: " + angleDegrees);
        }
    }

    @Override
    public void teleport(double newX, double newY) {
        System.out.println("TankHull.teleport() - Teleporting to (" + newX + ", " + newY + ")");
        setPosition(new Point2D(newX, newY));
        if (physicsBody != null) {
            physicsBody.setTransform(new Vec2((float)(newX / Constant.SCALE), (float)(newY / Constant.SCALE)),
                    physicsBody.getAngle());
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