package com.angrytanks.core;

import com.angrytanks.entity.custom.tank.TankController;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class InputActions implements EventHandler<KeyEvent> {

    private final TankController tankController;

    public InputActions(TankController tankController) {
        this.tankController = tankController;
        System.out.println("InputActions created for TankController.");
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getCode() == KeyCode.A) {
                System.out.println("InputActions: moveLeft");
                tankController.moveLeft();
            } else if (event.getCode() == KeyCode.D) {
                System.out.println("InputActions: moveRight");
                tankController.moveRight();
            } else if (event.getCode() == KeyCode.SPACE) {
                System.out.println("InputActions: fire");
                tankController.fire();
            } else if (event.getCode() == KeyCode.W) {
                System.out.println("InputActions: rotateTurretUp");
               // tankController.turretUp();
            } else if (event.getCode() == KeyCode.S) {
                System.out.println("InputActions: rotateTurretDown");
                //tankController.turretDown();
            }
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.D) {
                System.out.println("InputActions: stop");
                tankController.stop();
            }
        }
        event.consume();
    }
}
