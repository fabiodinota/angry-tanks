package com.angrytanks.entity.custom.tank.components;
import com.angrytanks.entity.Actor;
import com.angrytanks.util.Constant;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.ArrayList;
import java.util.List;

public class TankTracks extends Actor {

    private final Group trackVisuals;
    private final List<Body> trackSegments;
    private final int numSegments;
    private final float segmentLength;
    private final float segmentWidth;
    private World physicsWorld;

    public TankTracks(double x, double y) {
        super(x, y);
        trackVisuals = new Group();
        visuals.getChildren().add(trackVisuals);
        trackSegments = new ArrayList<>();

        numSegments = 20;
        segmentLength = 8f;
        segmentWidth  = 2f;
    }

    @Override
    public void addToPhysics(World physicsWorld) {
        this.physicsWorld = physicsWorld;

        float radius = 15f;

        for (int i = 0; i < numSegments; i++) {
            double angle = 2 * Math.PI * i / numSegments;
            double segX = getPosition().getX() + radius * Math.cos(angle);
            double segY = getPosition().getY() + radius * Math.sin(angle);

            BodyDef bd = new BodyDef();
            bd.type = BodyType.DYNAMIC;
            bd.position.set((float)(segX / Constant.SCALE), (float)(segY / Constant.SCALE));
            Body segment = physicsWorld.createBody(bd);

            PolygonShape shape = new PolygonShape();
            // half-length, half-width
            shape.setAsBox(segmentLength/2f / (float)Constant.SCALE, segmentWidth/2f / (float)Constant.SCALE);

            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 1.0f;
            fd.friction = 0.8f;
            segment.createFixture(fd);
            segment.setUserData(this);

            trackSegments.add(segment);
        }

        for (int i = 0; i < numSegments; i++) {
            Body current = trackSegments.get(i);
            Body next    = trackSegments.get((i+1) % numSegments);

            RevoluteJointDef jointDef = new RevoluteJointDef();
            Vec2 anchor = current.getPosition().add(next.getPosition()).mul(0.5f);
            jointDef.initialize(current, next, anchor);
            jointDef.collideConnected = false;
            physicsWorld.createJoint(jointDef);
        }
    }

    @Override
    public void render() {
        trackVisuals.getChildren().clear();

        for (Body segment : trackSegments) {
            float xPos = segment.getPosition().x * (float)Constant.SCALE;
            float yPos = segment.getPosition().y * (float)Constant.SCALE;

            Rectangle rect = new Rectangle(segmentLength, segmentWidth);
            rect.setFill(Color.DARKGRAY);
            rect.setX(xPos - segmentLength/2);
            rect.setY(yPos - segmentWidth/2);
            rect.setRotate(Math.toDegrees(segment.getAngle()));

            trackVisuals.getChildren().add(rect);
        }
    }

    @Override
    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));
        for (Body segment : trackSegments) {
            segment.setTransform(
                    new Vec2((float)(newX/Constant.SCALE), (float)(newY/Constant.SCALE)),
                    segment.getAngle()
            );
        }
    }
}