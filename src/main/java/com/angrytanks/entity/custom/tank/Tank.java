package com.angrytanks.entity.custom.tank;

import com.angrytanks.entity.custom.tank.components.*;

import com.angrytanks.util.Decomposable;
import com.angrytanks.util.Constant;
import com.angrytanks.world.PhysicsWorld;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.application.Platform;


public class Tank extends com.angrytanks.entity.Actor implements Decomposable {

    private TankHull hull;
    private TankTracks tracks;
    private TankTurret turret;
    private TankDecor decor;
    private TankData tankData;
    private final Group tankGroup = new Group();

    private Line leftRayLine = new Line();
    private Line midRayLine = new Line();
    private Line rightRayLine = new Line();
    private boolean debugLinesAdded = false;


    public Tank(TankData tankData, double spawnX, double spawnY) {
        super(spawnX, spawnY);


        setupDebugLines();

        this.tankData = tankData;
        hull = new TankHull(tankData.getHullVertices(), tankData.getHullColor());

        if (!tankData.getTrackVertices().isEmpty() && !tankData.getWheelVertices().isEmpty()) {
            tracks = new TankTracks(tankData.getTrackVertices(), tankData.getWheelVertices(),
                    tankData.getTrackColor(), tankData.getWheelColor());
        }

        if (!tankData.getDecorVertices().isEmpty()) {
            decor = new TankDecor(tankData.getDecorVertices(), tankData.getDecorColor());
        }
        if (!tankData.getTurretVertices().isEmpty()) {
            turret = new TankTurret(tankData.getTurretVertices(), tankData.getTurretColor());
        }
        if (hull != null) {
            tankGroup.getChildren().add(hull.getVisuals());
        }
        if (tracks != null) {
            tankGroup.getChildren().add(tracks.getVisuals());
        }
        if (decor != null) {
            tankGroup.getChildren().add(decor.getVisuals());
        }
        if (turret != null) {
            tankGroup.getChildren().add(turret.getVisuals());
        }
        visuals.getChildren().add(tankGroup);

        teleport(spawnX, spawnY);
    }

    @Override
    public void addToPhysics(World physicsWorld) {
        if (hull != null) {
            hull.addToPhysics(physicsWorld);
            if (hull.getPhysicsBody() != null) {
                hull.getPhysicsBody().setTransform(
                        new org.jbox2d.common.Vec2((float)(getPosition().getX() / Constant.SCALE),
                                (float)(getPosition().getY() / Constant.SCALE)),
                        hull.getPhysicsBody().getAngle());
            }
        }
        if (tracks != null) {
            tracks.addToPhysics(physicsWorld);
        }
    }

    @Override
    public void render() {
        if (hull != null && hull.getPhysicsBody() != null) {
            float xPos = hull.getPhysicsBody().getPosition().x * (float) Constant.SCALE;
            float yPos = hull.getPhysicsBody().getPosition().y * (float) Constant.SCALE;
            double angleDegrees = Math.toDegrees(hull.getPhysicsBody().getAngle());

            tankGroup.setLayoutX(xPos);
            tankGroup.setLayoutY(yPos);
            tankGroup.setRotate(angleDegrees);
            setPosition(new Point2D(xPos, yPos));

            alignWithSurface(PhysicsWorld.getPhysicsWorld());


            // System.out.println("Tank.render() - Global position: (" + xPos + ", " + yPos + "), angle: " + angleDegrees);
        }
    }

    //copied from stackoverflow and github box2d
    public void alignWithSurface(World physicsWorld) {
        setupDebugLines();

        Body tankBody = hull.getPhysicsBody();
        HullBounds bounds = getHullBounds();
        float hullWidthMeters = (float)(bounds.width / Constant.SCALE);
        float hullHeightMeters = (float)(hull.getHullHeight() / Constant.SCALE);
        float centerOffsetX = (float)(bounds.centerX / Constant.SCALE);
        float halfTankWidth = hullWidthMeters / 2.0f;

        float yOffest = 0.4f;
        Vec2 leftLocal = new Vec2(-halfTankWidth + centerOffsetX, hullHeightMeters + yOffest);
        Vec2 midLocal = new Vec2(centerOffsetX, hullHeightMeters + yOffest);
        Vec2 rightLocal = new Vec2(halfTankWidth + centerOffsetX, hullHeightMeters + yOffest);

        float angle = tankBody.getAngle();
        Vec2 leftOffset = rotate(leftLocal, angle);
        Vec2 midOffset = rotate(midLocal, angle);
        Vec2 rightOffset = rotate(rightLocal, angle);

        Vec2 tankPosition = tankBody.getPosition();
        Vec2 leftRayStart = tankPosition.add(leftOffset);
        Vec2 midRayStart = tankPosition.add(midOffset);
        Vec2 rightRayStart = tankPosition.add(rightOffset);

        float rayLength = 0.2f;
        Vec2 rayDown = new Vec2(0, rayLength);
        Vec2 leftRayEnd = leftRayStart.add(rayDown);
        Vec2 midRayEnd = midRayStart.add(rayDown);
        Vec2 rightRayEnd = rightRayStart.add(rayDown);


        final Vec2[] leftHit = {null};
        final Vec2[] rightHit = {null};

        physicsWorld.raycast((fixture, point, normal, fraction) -> {
            leftHit[0] = point;
            return fraction;
        }, leftRayStart, leftRayEnd);

        physicsWorld.raycast((fixture, point, normal, fraction) -> {
            rightHit[0] = point;
            return fraction;
        }, rightRayStart, rightRayEnd);

        if (leftHit[0] != null && rightHit[0] != null) {
            Vec2 groundVector = rightHit[0].sub(leftHit[0]);
            float desiredAngle = (float)Math.atan2(groundVector.y, groundVector.x);
            float currentAngle = tankBody.getAngle();
            float angleDifference = desiredAngle - currentAngle;
            angleDifference = (float)Math.atan2((float)Math.sin(angleDifference), (float)Math.cos(angleDifference));
            float correctionFactor = 0.5f;
            tankBody.setAngularVelocity(angleDifference * correctionFactor);
            tankBody.setFixedRotation(true);
        } else {
            tankBody.setFixedRotation(false);
        }

        Platform.runLater(() -> {
            leftRayLine.setStartX(leftRayStart.x * Constant.SCALE);
            leftRayLine.setStartY(leftRayStart.y * Constant.SCALE);
            leftRayLine.setEndX(leftRayEnd.x * Constant.SCALE);
            leftRayLine.setEndY(leftRayEnd.y * Constant.SCALE);

            midRayLine.setStartX(midRayStart.x * Constant.SCALE);
            midRayLine.setStartY(midRayStart.y * Constant.SCALE);
            midRayLine.setEndX(midRayEnd.x * Constant.SCALE);
            midRayLine.setEndY(midRayEnd.y * Constant.SCALE);

            rightRayLine.setStartX(rightRayStart.x * Constant.SCALE);
            rightRayLine.setStartY(rightRayStart.y * Constant.SCALE);
            rightRayLine.setEndX(rightRayEnd.x * Constant.SCALE);
            rightRayLine.setEndY(rightRayEnd.y * Constant.SCALE);
        });
    }

    private Vec2 rotate(Vec2 v, float angle) {
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        return new Vec2(v.x * cos - v.y * sin, v.x * sin + v.y * cos);
    }



    @Override
    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));
        tankGroup.setLayoutX(newX);
        tankGroup.setLayoutY(newY);

        if (hull != null) {
            hull.teleport(newX, newY);
        }
        if (tracks != null) {
            tracks.teleport(newX, newY);
        }
        if (decor != null) {
            decor.teleport(newX, newY);
        }
        System.out.println("Tank.teleport() - Teleporting tank to (" + newX + ", " + newY + ")");
    }

    @Override
    public void showDecompositionOutline() {
        if (hull instanceof Decomposable) {
            ((Decomposable) hull).showDecompositionOutline();
        }
        if (tracks != null && tracks instanceof Decomposable) {
            ((Decomposable) tracks).showDecompositionOutline();
        }
    }

    @Override
    public void hideDecompositionOutline() {
        if (hull instanceof Decomposable) {
            ((Decomposable) hull).hideDecompositionOutline();
        }
        if (tracks != null && tracks instanceof Decomposable) {
            ((Decomposable) tracks).hideDecompositionOutline();
        }
    }

    public TankHull getHull() {
        return hull;
    }

    public double getHullWidth() {
        if (tankData.getHullVertices().isEmpty()) return 0;

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (Point2D vertex : tankData.getHullVertices()) {
            double x = vertex.getX();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
        }

        return maxX - minX;
    }
    public class HullBounds {
        public double minX, maxX, width, centerX;

        public HullBounds(double minX, double maxX) {
            this.minX = minX;
            this.maxX = maxX;
            this.width = maxX - minX;
            this.centerX = minX + width / 2.0;
        }
    }

    public HullBounds getHullBounds() {
        if (tankData.getHullVertices().isEmpty())
            return new HullBounds(0, 0);

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (Point2D vertex : tankData.getHullVertices()) {
            double x = vertex.getX();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
        }

        return new HullBounds(minX, maxX);
    }

    private void setupDebugLines() {
        if (debugLinesAdded) return;
        Platform.runLater(() -> {
            leftRayLine.setStroke(Color.RED);
            midRayLine.setStroke(Color.GREEN);
            rightRayLine.setStroke(Color.BLUE);
            leftRayLine.setStrokeWidth(2);
            midRayLine.setStrokeWidth(2);
            rightRayLine.setStrokeWidth(2);
            visuals.getChildren().addAll(leftRayLine, midRayLine, rightRayLine);
            debugLinesAdded = true;
        });
    }
}