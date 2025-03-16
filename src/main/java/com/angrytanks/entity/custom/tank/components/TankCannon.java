package com.angrytanks.entity.custom.tank.components;

import com.angrytanks.core.GameState;
import com.angrytanks.entity.Actor;
import com.angrytanks.entity.custom.Projectile;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.util.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import java.util.ArrayList;
import java.util.List;

public class TankCannon extends Actor {

    private Body cannonBody;
    private RevoluteJoint cannonJoint;
    private final Polygon mergedVisualPolygon;
    private final List<Point2D> cannonVertices;
    private final List<List<Point2D>> convexParts;
    private final Point2D pivotOffset;
    private final Tank parentTank;
    private boolean mirrored = false;




    public TankCannon(List<Point2D> vertices, Color fill, Point2D pivotOffset , Tank parentTank) {
        super(pivotOffset.getX(), pivotOffset.getY());
        this.pivotOffset = pivotOffset;
        this.parentTank = parentTank;
        this.cannonVertices = new ArrayList<>(vertices);
        PolygonUtil.ensureClockwiseOrder(this.cannonVertices);

        convexParts = ConvexDecomposer.decompose(this.cannonVertices);
        if (convexParts.isEmpty()) {
            convexParts.add(new ArrayList<>(this.cannonVertices));
        }

        mergedVisualPolygon = new Polygon();
        for (Point2D pt : this.cannonVertices) {
            mergedVisualPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        mergedVisualPolygon.setFill(fill != null ? fill : Color.BLACK);
        visuals.getChildren().add(mergedVisualPolygon);


    }


    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    @Override
    public void addToPhysics(World physicsWorld) {


        if (cannonBody != null) return;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set((float)(getPosition().getX() / Constant.SCALE),
                (float)(getPosition().getY() / Constant.SCALE));
        cannonBody = physicsWorld.createBody(bd);
        cannonBody.setUserData(this);
        cannonBody.setGravityScale(1.0f);

        for (List<Point2D> part : convexParts) {
            int count = Math.min(part.size(), 8);
            Vec2[] vecs = new Vec2[count];
            for (int i = 0; i < count; i++) {
                Point2D p = part.get(i);
                vecs[i] = new Vec2((float)((p.getX() - pivotOffset.getX()) / Constant.SCALE),
                        (float)((p.getY() - pivotOffset.getY()) / Constant.SCALE));
            }
            PolygonShape shape = new PolygonShape();
            shape.set(vecs, count);

            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 1.0f;
            fd.friction = 0.5f;
            fd.isSensor = true;
            cannonBody.createFixture(fd);
        }
    }

    public void setCannonJoint(RevoluteJoint joint) {
        this.cannonJoint = joint;
    }

    public void rotateUp() {
        if (cannonJoint != null) {
            float currentAngle = cannonBody.getAngle();
            float lowerLimit = (float) Math.toRadians(-45);
            if (currentAngle > lowerLimit) {
                cannonJoint.setMotorSpeed(-3f);
                PauseTransition pause = new PauseTransition(Duration.millis(50));
                pause.setOnFinished(e -> cannonJoint.setMotorSpeed(0.0f));
                pause.play();
            }
        }
    }

    public void rotateDown() {
        if (cannonJoint != null) {
            float currentAngle = cannonBody.getAngle();
            float upperLimit = (float) Math.toRadians(20);
            if (currentAngle < upperLimit) {
                cannonJoint.setMotorSpeed(3f);
                PauseTransition pause = new PauseTransition(Duration.millis(50));
                pause.setOnFinished(e -> cannonJoint.setMotorSpeed(0.0f));
                pause.play();
            }
        }
    }

    public void stopRotation() {
        if (cannonJoint != null) {
            cannonJoint.setMotorSpeed(0.0f);
        }
    }


    public Point2D computeCannonTipLocal() {
        if (!mirrored) {
            double maxX = Double.NEGATIVE_INFINITY;
            double tipX = 0, tipY = 0;
            List<Double> pts = mergedVisualPolygon.getPoints();
            for (int i = 0; i < pts.size() / 2; i++) {
                double x = pts.get(i * 2);
                double y = pts.get(i * 2 + 1);
                if (x > maxX) {
                    maxX = x;
                    tipX = x;
                    tipY = y;
                }
            }
            return new Point2D(tipX, tipY);
        } else {
            double minX = Double.POSITIVE_INFINITY;
            double tipX = 0, tipY = 0;
            List<Double> pts = mergedVisualPolygon.getPoints();
            for (int i = 0; i < pts.size() / 2; i++) {
                double x = pts.get(i * 2);
                double y = pts.get(i * 2 + 1);
                if (x < minX) {
                    minX = x;
                    tipX = x;
                    tipY = y;
                }
            }
            return new Point2D(tipX, tipY);
        }
    }




    @Override
    public void render() {
        if (cannonBody != null) {


            double angle = cannonBody.getAngle();
            mergedVisualPolygon.getTransforms().clear();
            mergedVisualPolygon.getTransforms().add(new Rotate(Math.toDegrees(angle), pivotOffset.getX(), pivotOffset.getY()));

        }
    }

    public void fireShell(double initialSpeed) {
        if (cannonBody == null) {
            System.out.println("Cannon physics body is not initialized.");
            return;
        }

        Point2D tipLocal = computeCannonTipLocal();

        if (mirrored) {
            double dx = tipLocal.getX() - pivotOffset.getX();
            tipLocal = new Point2D(pivotOffset.getX() - dx, tipLocal.getY());
        }

        Point2D tipWorld = mergedVisualPolygon.localToScene(tipLocal);

        Projectile projectile = new Projectile(tipWorld.getX(), tipWorld.getY(), parentTank);
        GameState.getWorld().addActor(projectile);

        if (projectile.physicsBody != null) {
            double angle = cannonBody.getAngle();
            if (mirrored) {
                angle = Math.PI - angle;
            }
            float forceX = (float) (Math.cos(angle) * initialSpeed);
            float forceY = (float) (Math.sin(angle) * initialSpeed);
            projectile.physicsBody.applyLinearImpulse(new Vec2(forceX, forceY),
                    projectile.physicsBody.getWorldCenter());
        }
    }




    @Override
    public void teleport(double newX, double newY) {
        if (cannonBody != null) {
            cannonBody.setTransform(new Vec2((float)(newX / Constant.SCALE), (float)(newY / Constant.SCALE)), cannonBody.getAngle());
            cannonBody.setAwake(true);
        }
    }

    public Body getPhysicsBody() {
        return cannonBody;
    }

    public Point2D getPivotOffset() {
        return pivotOffset;
    }
}
