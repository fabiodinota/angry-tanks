package com.angrytanks.core;

import com.angrytanks.entity.custom.tank.TankController;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class InputActions implements EventHandler<KeyEvent> {

    private final TankController tankController;
    private boolean canFire = true;

    public InputActions(TankController tankController) {
        this.tankController = tankController;

    }

    @Override
    public void handle(KeyEvent event) {


        if (tankController.getTank() != GameState.getCurrentPlayer()) {
            return;
        }

        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getCode() == KeyCode.A) {
                tankController.moveLeft();
            } else if (event.getCode() == KeyCode.D) {
                tankController.moveRight();
            } else if (event.getCode() == KeyCode.G) {

                tankController.fire();
                GameState.switchTurn();
                tankController.setTank(GameState.getCurrentPlayer());


            } else if (event.getCode() == KeyCode.W) {
                tankController.turretUp();
            } else if (event.getCode() == KeyCode.S) {
                tankController.turretDown();
            }
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.D) {

                tankController.stop();
            }
        }
        event.consume();
    }


}
