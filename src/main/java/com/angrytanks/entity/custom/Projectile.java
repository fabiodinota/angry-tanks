package com.angrytanks.entity.custom;

import com.angrytanks.entity.Actor;
import com.angrytanks.entity.MovementComponent;
import com.angrytanks.util.Constant;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

public class Projectile extends Actor {
    private final Rectangle hitbox;
   // private MovementComponent movement;
    //private Point2D impactLocation;
    public Body physicsBody;

    public Projectile(double x, double y){
        super(x, y);
        this.hitbox = new Rectangle(20, 20);

        this.hitbox.setX(x);
        this.hitbox.setY(y);

        this.visuals.getChildren().add(hitbox);

       // this.movement = new MovementComponent();
        //this.impactLocation = null;
    }


    @Override
    public void render() {
        if (physicsBody != null) {
            float xPos = (float) (physicsBody.getPosition().x * Constant.SCALE);
            float yPos = (float) (physicsBody.getPosition().y * Constant.SCALE);
            hitbox.setX(xPos);
            hitbox.setY(yPos);

            setPosition(new Point2D(xPos, yPos));
        }else {
            visuals.getChildren().clear();
        }
    }

    @Override
    public void addToPhysics(World physicsWorld) {
        System.out.println("new bullet" + position);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set((float) (position.getX() / Constant.SCALE), (float) (position.getY() / Constant.SCALE));
        physicsBody = physicsWorld.createBody(bodyDef);

        physicsBody.setUserData(this);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.2f;
        physicsBody.createFixture(fixtureDef);
    }

    public Point2D getLocation() {
        return getPosition();
    }

//    public Point2D getImpactLocation() {
//        return impactLocation;
//    }

    public List<Point2D> calculatePath() {
        return new ArrayList<>();
    }

    @Override
    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));

        if (physicsBody != null) {
            physicsBody.setTransform(
                    new Vec2((float) (newX / Constant.SCALE), (float) (newY / Constant.SCALE)),
                    physicsBody.getAngle()
            );
        }
    }
    @Override
    public void setPosition(Point2D newPos) {
        super.setPosition(newPos);

        hitbox.setX(newPos.getX());
        hitbox.setY(newPos.getY());

        if (physicsBody != null) {
            physicsBody.setTransform(
                    new Vec2((float) (newPos.getX() / Constant.SCALE), (float) (newPos.getY() / Constant.SCALE)),
                    physicsBody.getAngle()
            );

            physicsBody.setAwake(true);

        }
    }
}
