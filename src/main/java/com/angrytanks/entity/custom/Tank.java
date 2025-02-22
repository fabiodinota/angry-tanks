package com.angrytanks.entity.custom;

import com.angrytanks.entity.Actor;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;


public class Tank extends Actor {

    private final ImageView tankImage;
    private Body physicsBody;

    public Tank(double x, double y) {
        super(x, y);

        this.tankImage = new ImageView(new Image("tank.png"));
        this.tankImage.setX(x);
        this.tankImage.setY(y);
        this.tankImage.setFitWidth(60);
        this.tankImage.setFitHeight(40);

        this.visuals.getChildren().add(tankImage);
    }

    public void teleport(double newX, double newY) {
        setPosition(new Point2D(newX, newY));

        if (physicsBody != null) {
            physicsBody.setTransform(
                    new org.jbox2d.common.Vec2((float) (newX / 10.0), (float) (newY / 10.0)),
                    physicsBody.getAngle()
            );
        }
    }

    public void addToPhysics(World physicsWorld) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((float)(position.getX() / 10.0), (float)(position.getY() / 10.0));
        bodyDef.type = BodyType.DYNAMIC;
        physicsBody = physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(3f, 3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        physicsBody.createFixture(fixtureDef);
    }

    @Override
    public void render() {
        if (physicsBody != null && tankImage != null ) {
            float xPos = physicsBody.getPosition().x * 10;
            float yPos = physicsBody.getPosition().y * 10;
            tankImage.setX(xPos);
            tankImage.setY(yPos);
            setPosition(new Point2D(xPos, yPos));
        }
    }

    @Override
    public void setPosition(Point2D newPos) {
        super.setPosition(newPos);

        tankImage.setX(newPos.getX());
        tankImage.setY(newPos.getY());

        if (physicsBody != null) {
            physicsBody.setTransform(
                    new Vec2((float) (newPos.getX() / 10.0), (float) (newPos.getY() / 10.0)),
                    physicsBody.getAngle()
            );

            physicsBody.setAwake(true);

        }
    }
}
