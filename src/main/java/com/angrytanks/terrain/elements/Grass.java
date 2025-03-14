package com.angrytanks.terrain.elements;

import com.angrytanks.terrain.DestructibleTerrain;
import com.angrytanks.util.Constant;
import com.angrytanks.util.ConvexDecomposer;
import com.angrytanks.util.CraterParameters;
import com.angrytanks.util.PolygonUtil;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

public class Grass extends DestructibleTerrain {

    private Body physicsBody;

    public Grass(double x, double y, List<Point2D> vertices, Color fill) {
        super(vertices, fill);


        this.craterParameters = new CraterParameters(30, 30, 0, 16);
    }

    private static List<Point2D> simplifyAndPrepare(List<Point2D> vertices, double tol) {
        List<Point2D> copy = new ArrayList<>(vertices);
        copy = ConvexDecomposer.simplifyVertices(copy, tol);
        copy = ConvexDecomposer.normalizePolygon(copy);
        PolygonUtil.ensureClockwiseOrder(copy);
        return copy;
    }

    @Override
    public void addToPhysics(World jbox2dWorld) {
        BodyDef bd = new BodyDef();
        bd.position.set(0, 0);
        bd.type = BodyType.STATIC;
        physicsBody = jbox2dWorld.createBody(bd);
        physicsBody.setUserData(this);
        for (List<Point2D> part : convexParts) {
            PolygonShape shape = new PolygonShape();
            int count = part.size();
            Vec2[] vecs = new Vec2[count];
            for (int i = 0; i < count; i++) {
                Point2D pt = part.get(i);
                vecs[i] = new Vec2((float) (pt.getX() / Constant.SCALE), (float) (pt.getY() / Constant.SCALE));
            }
            shape.set(vecs, count);
            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 1.0f;
            fd.friction = 0.5f;
            physicsBody.createFixture(fd);
        }
    }

    @Override
    public Body getPhysicsBody() {
        return physicsBody;
    }

    @Override
    public void render() {
//        if (physicsBody != null) {
//            float xPos = physicsBody.getPosition().x * 10;
//            float yPos = physicsBody.getPosition().y * 10;
//            mergedVisual.setLayoutX(xPos);
//            mergedVisual.setLayoutY(yPos);
//            setPosition(new Point2D(xPos, yPos));
//        }
    }

    @Override
    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));
        if (physicsBody != null) {
            physicsBody.setTransform(new Vec2((float) (newX / Constant.SCALE), (float) (newY / Constant.SCALE)), physicsBody.getAngle());
        }
    }
}
