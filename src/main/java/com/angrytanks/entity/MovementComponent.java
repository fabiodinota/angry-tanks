package com.angrytanks.entity;

import com.angrytanks.enums.MovementMode;

public class MovementComponent {

    private MovementMode movementMode;
    private float gravityScale;
    private float maxAcceleration;


    public MovementComponent() {
        this.movementMode = MovementMode.BALLISTIC;
        this.gravityScale = 1.0f;
        this.maxAcceleration = 100f;
    }

    public void setMovementMode(MovementMode mode) {
        this.movementMode = mode;
    }

    public MovementMode getMovementMode() {
        return movementMode;
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
    }

    public float getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(float maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
    }


}