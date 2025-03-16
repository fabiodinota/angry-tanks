package com.angrytanks.entity.custom;

import com.angrytanks.entity.Actor;
import com.angrytanks.entity.MovementComponent;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.util.Constant;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

public class Projectile extends Actor {

    private final Rectangle hitbox;
    private final ImageView imageView;
    public Body physicsBody;
    private Tank shooter;

    public Projectile(double x, double y, Tank shooter) {
        super(x, y);
        this.shooter = shooter;
        Image projectileImage = new Image(getClass().getResourceAsStream("/tanks/projectile/projectile.png"));
        imageView = new ImageView(projectileImage);
        imageView.setX(x);
        imageView.setY(y);

        imageView.setFitWidth(12.5);
        imageView.setFitHeight(4);

        hitbox = new Rectangle(18, 6);
        hitbox.setFill(Color.TRANSPARENT);

        hitbox.setX(x);
        hitbox.setY(y);

        visuals.getChildren().addAll(imageView, hitbox);
    }


    @Override
    public void render() {
        if (physicsBody != null) {
            float xPos = (float) (physicsBody.getPosition().x * Constant.SCALE);
            float yPos = (float) (physicsBody.getPosition().y * Constant.SCALE);

            double halfWidth = imageView.getFitWidth() / 2.0;
            double halfHeight = imageView.getFitHeight() / 2.0;
            imageView.setX(xPos - halfWidth);
            imageView.setY(yPos - halfHeight);
            hitbox.setX(xPos - halfWidth);
            hitbox.setY(yPos - halfHeight);


            Vec2 velocity = physicsBody.getLinearVelocity();
            double angleRadians = Math.atan2(velocity.y, velocity.x);
            double angleDegrees = Math.toDegrees(angleRadians);
            imageView.setRotate(angleDegrees);

            setPosition(new Point2D(xPos, yPos));
        } else {
            visuals.getChildren().clear();
        }
    }



    @Override
    public void addToPhysics(World physicsWorld) {

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

    public Tank getShooter() {
        return shooter;
    }

}
