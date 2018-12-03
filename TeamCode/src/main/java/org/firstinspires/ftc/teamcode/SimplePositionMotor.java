package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class SimplePositionMotor {

    public DcMotor motor;
    private static final DcMotor.RunMode DEFAULT_RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER;

    private int setPoint;
    private double maxSpeed;
    private double marginOfError;

    private static final int DEFAULT_SETPOINT = 0;
    private static final double DEFAULT_MAX_SPEED = 0.2;
    private static final double DEFAULT_MARGINOFERROR = 10;

    public SimplePositionMotor(DcMotor motor) {
        this(motor, DEFAULT_SETPOINT, DEFAULT_MAX_SPEED, DEFAULT_MARGINOFERROR);
    }

    public SimplePositionMotor(DcMotor motor, int setPoint, double maxSpeed, double marginOfError) {
        this.motor = motor;
        this.setPoint = setPoint;
        this.maxSpeed = maxSpeed;
        this.marginOfError = marginOfError;
        motor.setMode(DEFAULT_RUNMODE);
        resetEncoder();
    }

    public void runIteration() {

        double error = getError();

        double speed = 0;

        if (error > marginOfError) {
            speed = maxSpeed;
        }
        else if (error < -1 * marginOfError) {
            speed = -1 * maxSpeed;
        }

        // speed = Math.max(Math.min(speed, maxSpeed), maxSpeed * -1);

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
        this.setPoint = setPoint;
    }

    public double getError() {
        return  setPoint - motor.getCurrentPosition();
    }

    // -------- Access Encapsulated Motor --------

    public DcMotor getMotor() {
        return motor;
    }

    // -------- Access Max Speed --------

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        if (maxSpeed >= 0)
            this.maxSpeed = maxSpeed;
    }

    public void changeMaxSpeed(double delta) {
        if (maxSpeed + delta >= 0)
            this.maxSpeed += delta;
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
