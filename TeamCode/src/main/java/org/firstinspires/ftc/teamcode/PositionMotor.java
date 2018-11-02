package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class PositionMotor {

    // TODO: Use a weighted value for acceleration?

    public DcMotor motor;
    private double acceleration;
    private int currentPosition = 0;

    private static final int DEFAULT_POSITION = 0;
    private static final double DEFAULT_ACCELERATION = 0;

    public PositionMotor(DcMotor motor) {
        this(motor, DEFAULT_ACCELERATION, DEFAULT_POSITION);
    }

    public PositionMotor(DcMotor motor, double acceleration, int currentPosition) {
        this.motor = motor;
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        resetEncoder();
        this.acceleration = acceleration;
        this.currentPosition = currentPosition;
    }

    private void resetEncoder() {
        if (motor.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
            motor.setMode(DcMotor.RunMode.RESET_ENCODERS);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public int getRealPosition() {
        return motor.getCurrentPosition();
    }

    public int getTargetPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        runToPosition();
    }

    // TODO: Will this stop the runmode until the method returns, or will it run asynchronously?
    public void runToPosition() {
        // do something with currentPosition here
    }

}
