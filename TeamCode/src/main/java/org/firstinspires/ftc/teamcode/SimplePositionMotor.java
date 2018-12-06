package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class SimplePositionMotor {

    public DcMotor motor;
    private static final DcMotor.RunMode DEFAULT_RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER;

    private int setPoint;
    private double maxSpeedForward;
    private double maxSpeedReverse;
    private double marginOfError;

    private static final int DEFAULT_SETPOINT = 0;
    private static final double DEFAULT_MAX_SPEED_FORWARD = 0.2;
    private static final double DEFAULT_MAX_SPEED_REVERSE = 0.2;
    private static final double DEFAULT_MARGIN_OF_ERROR = 5;

    private boolean reachedSetPoint = false;

    public SimplePositionMotor(DcMotor motor) {
        this(motor, DEFAULT_SETPOINT, DEFAULT_MAX_SPEED_FORWARD, DEFAULT_MAX_SPEED_REVERSE, DEFAULT_MARGIN_OF_ERROR);
    }

    public SimplePositionMotor(DcMotor motor, int setPoint, double maxSpeedForward, double maxSpeedReverse, double marginOfError) {
        this.motor = motor;
        this.setPoint = setPoint;
        this.maxSpeedForward = maxSpeedForward;
        this.maxSpeedReverse = maxSpeedReverse;
        this.marginOfError = marginOfError;
        motor.setMode(DEFAULT_RUNMODE);
        resetEncoder();
    }

    public void runIteration() {

        double error = getError();

        double speed = 0;

        // Going down
        if (error > marginOfError && !reachedSetPoint) {
            speed = maxSpeedForward;
        }
        // Going up
        else if (error < -1 * marginOfError && !reachedSetPoint) {
            speed = -1 * maxSpeedReverse;
        }
        else if (!reachedSetPoint) {
            reachedSetPoint = true;
        }

        motor.setPower(speed);
    }

    // -------- Utilities --------

    public void resetEncoder() {
        if (motor.getMode() == DEFAULT_RUNMODE) {
            motor.setMode(DcMotor.RunMode.RESET_ENCODERS);
            motor.setMode(DEFAULT_RUNMODE);
        }
    }

    // -------- Access Process Variable and Setpoint --------

    public int getPosition() {
        return motor.getCurrentPosition();
    }

    public int getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(int setPoint) {
        if (this.setPoint != setPoint)
            reachedSetPoint = false;
        this.setPoint = setPoint;
    }

    public double getError() {
        return  setPoint - motor.getCurrentPosition();
    }

    // -------- Access Encapsulated Motor --------

    public DcMotor getMotor() {
        return motor;
    }

    // -------- Access Max Speeds --------

    public double getMaxSpeedForward() {
        return maxSpeedForward;
    }

    public void setMaxSpeedForward(double maxSpeed) {
        if (maxSpeed >= 0)
            this.maxSpeedForward = maxSpeed;
    }

    public void changeMaxSpeedForward(double delta) {
        if (maxSpeedForward + delta >= 0)
            this.maxSpeedForward += delta;
    }

    public double getMaxSpeedReverse() {
        return maxSpeedReverse;
    }

    public void setMaxSpeedReverse(double maxSpeedReverse) {
        if (maxSpeedReverse >= 0)
            this.maxSpeedReverse = maxSpeedReverse;
    }

    public void changeMaxSpeedReverse(double delta) {
        if (maxSpeedReverse + delta >= 0)
            this.maxSpeedReverse += delta;
    }

    // -------- Access Margin of Error --------

    public double getMarginOfError() {
        return marginOfError;
    }

    public void setMarginOfError(double marginOfError) {
        if (marginOfError >= 0)
            this.marginOfError = marginOfError;
    }

    public void changeMarginOfError(double delta) {
        if (marginOfError + delta >= 0)
            marginOfError += delta;
    }

}