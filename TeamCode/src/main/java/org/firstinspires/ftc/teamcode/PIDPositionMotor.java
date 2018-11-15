package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class PIDPositionMotor {

    public DcMotor motor;

    private int setPoint;

    private double kp;
    private double ki;
    private double kd;

    private static final int DEFAULT_SETPOINT = 0;

    private static final double DEFAULT_KP = 1;
    private static final double DEFAULT_KI = 1;
    private static final double DEFAULT_KD = 1;

    public PIDPositionMotor(DcMotor motor) {
        this(motor, DEFAULT_SETPOINT, DEFAULT_KP, DEFAULT_KI, DEFAULT_KD);
    }

    public PIDPositionMotor(DcMotor motor, int setPoint, double kp, double ki, double kd) {
        this.motor = motor;
        this.setPoint = setPoint;
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        resetEncoder();
    }

    private void resetEncoder() {
        if (motor.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
            motor.setMode(DcMotor.RunMode.RESET_ENCODERS);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

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
        return Math.abs(setPoint - motor.getCurrentPosition());
    }

    // TODO: Will this stop the runmode until the method returns, or will it run asynchronously?
    public void runToSetPoint() {

        while (getError() > 0) {
            System.out.println("This code does not work yet...");
        }

    }

}
