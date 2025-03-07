package com.angrytanks.terrain.elements;

import com.angrytanks.terrain.DestructibleTerrain;
import com.angrytanks.util.Constant;
import com.angrytanks.util.CraterParameters;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.List;
import java.util.Random;

public class Landscape extends DestructibleTerrain {

    private Body physicsBody;

    public Landscape(List<Point2D> vertices, Color fill) {
        super(vertices, fill);


        this.craterParameters = new CraterParameters(70, 45, 0, 100);

    }

    @Override
    public void addToPhysics(World jbox2dWorld) {
        this.worldRef = jbox2dWorld;
        BodyDef bd = new BodyDef();
        bd.position.set(0, 0);
        bd.type = BodyType.STATIC;
        physicsBody = jbox2dWorld.createBody(bd);
        physicsBody.setUserData(this);

        for (List<Point2D> part : convexParts) {
            PolygonShape shape = new PolygonShape();
            Vec2[] vecs = new Vec2[part.size()];
            for (int i = 0; i < part.size(); i++) {
                Point2D p = part.get(i);
                vecs[i] = new Vec2((float)(p.getX() / Constant.SCALE), (float)(p.getY() / Constant.SCALE));
            }
            shape.set(vecs, part.size());
            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 0.0f;
            fd.friction = 0.9f;
            physicsBody.createFixture(fd);
        }
    }

    @Override
    public Body getPhysicsBody() {
        return physicsBody;
    }

    @Override
    public void render() {
    }

    @Override
    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));
        visualGroup.setLayoutX(newX);
        visualGroup.setLayoutY(newY);
    }
}
