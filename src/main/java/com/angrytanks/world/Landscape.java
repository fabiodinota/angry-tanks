package com.angrytanks.world;

import com.angrytanks.entity.Actor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;



public class Landscape extends Actor {

    private Body physicsBody;
    private final Rectangle groundVisual;


    public Landscape(double startX, double startY, double width, double thickness) {
        super(startX, startY);

        this.groundVisual = new Rectangle(width, thickness, Color.SADDLEBROWN);
        groundVisual.setX(startX);
        groundVisual.setY(startY);

        visuals.getChildren().add(groundVisual);
    }



    public void addToPhysics(World jbox2dWorld) {

        float halfWidth = (float) (groundVisual.getWidth() / 2.0 / 10.0);
        float halfThickness = (float) (groundVisual.getHeight() / 2.0 / 10.0);

        float centerX = (float) ((position.getX() + groundVisual.getWidth() / 2.0) / 10.0);
        float centerY = (float) ((position.getY() + groundVisual.getHeight() / 2.0) / 10.0);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(centerX, centerY);
        bodyDef.type = BodyType.STATIC;
        physicsBody = jbox2dWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfWidth, halfThickness);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.friction = 0.9f;
        physicsBody.createFixture(fixtureDef);
    }



    @Override
    public void render() {
    }
}
