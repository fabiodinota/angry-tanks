package com.angrytanks.entity.custom.tank.components;

import com.angrytanks.entity.Actor;
import com.angrytanks.entity.Decoration;
import com.angrytanks.entity.custom.tank.Tank;
import com.angrytanks.util.Constant;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.List;

public class TankTurret extends Actor {


    private final Polygon turretPolygon;
    private TankCannon cannon;
    private Body turretBody;
    private RevoluteJoint cannonJoint;
    private Tank parentTank;

    public TankTurret(TankData tankData, Tank parentTank) {
        super(tankData.getTurretVertices().isEmpty() ? 0 : tankData.getTurretVertices().get(0).getX(),
                tankData.getTurretVertices().isEmpty() ? 0 : tankData.getTurretVertices().get(0).getY());

        turretPolygon = new Polygon();
        for (Point2D pt : tankData.getTurretVertices()) {
            turretPolygon.getPoints().addAll(pt.getX(), pt.getY());
        }
        turretPolygon.setFill(tankData.getTurretColor() != null ? tankData.getTurretColor() : Color.DARKSLATEGRAY);
        visuals.getChildren().add(turretPolygon);

        if (!tankData.getCannonVertices().isEmpty() && !tankData.getCannonAnchor().isEmpty()) {
            Point2D pivotOffset = tankData.getCannonAnchor().isEmpty()
                    ? new Point2D(35, 0)
                    : tankData.getCannonAnchor().getFirst();

            cannon = new TankCannon(tankData.getCannonVertices(), tankData.getCannonColor(), pivotOffset, parentTank);
            visuals.getChildren().add(cannon.getVisuals());
        }
    }

    @Override
    public void addToPhysics(World physicsWorld) {

        BodyDef turretDef = new BodyDef();
        turretDef.type = BodyType.STATIC;
        turretDef.position.set((float) (getPosition().getX() / Constant.SCALE), (float) (getPosition().getY() / Constant.SCALE));
        turretBody = physicsWorld.createBody(turretDef);
        turretBody.setUserData(this);

        if (cannon != null) {
            cannon.addToPhysics(physicsWorld);
        }

        if (cannon != null && cannon.getPhysicsBody() != null) {
            RevoluteJointDef rjd = new RevoluteJointDef();
            rjd.bodyA = turretBody;
            rjd.bodyB = cannon.getPhysicsBody();

            Vec2 worldPivot = new Vec2(
                    (float) (cannon.getPivotOffset().getX() / Constant.SCALE),
                    (float) (cannon.getPivotOffset().getY() / Constant.SCALE)
            );

            rjd.localAnchorA.set(worldPivot);
            rjd.localAnchorB.set(0, 0);
            rjd.enableMotor = true;
            rjd.motorSpeed = 0f;
            rjd.maxMotorTorque = 1900f;
            rjd.lowerAngle = (float) Math.toRadians(-45);
            rjd.upperAngle = (float) Math.toRadians(45);
            rjd.enableLimit = true;

            cannonJoint = (RevoluteJoint) physicsWorld.createJoint(rjd);
            turretBody.setUserData(parentTank);

            cannon.setCannonJoint(cannonJoint);
        }
    }


    public void rotateCannonUp() {
        if (cannon != null) {

            cannon.rotateUp();
        }
    }

    public void rotateCannonDown() {
        if (cannon != null) {
            cannon.rotateDown();
        }
    }

    public void stopCannonRotation() {
        if (cannon != null) {
            cannon.stopRotation();
        }
    }

    @Override
    public void render() {
        if (turretBody != null) {
            Vec2 pos = turretBody.getPosition();

//            visuals.setLayoutX(pos.x * Constant.SCALE);
//            visuals.setLayoutY(pos.y * Constant.SCALE);


            if (cannon != null) {
                cannon.render();

            }
        }
    }


    @Override
    public void teleport(double newX, double newY) {
        turretPolygon.setTranslateX(0);
        turretPolygon.setTranslateY(0);
    }

    public TankCannon getCannon() {
        return cannon;
    }


}
