package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class SimplePositionMotor {

    public DcMotor motor;
    private static final DcMotor.RunMode DEFAULT_RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER;
    private static final DcMotor.ZeroPowerBehavior DEFAULT_ZERO_POWER_BEHAVIOR = DcMotor.ZeroPowerBehavior.BRAKE;

    private int setPoint;
    private double maxSpeedForward;
    private double maxSpeedReverse;
    private double marginOfError;
    private double setPointSwitchTolerance;

    private int positionUp;
    private int positionDown;

    private static final int DEFAULT_SETPOINT = 0;
    private static final double DEFAULT_MAX_SPEED_FORWARD = 0.2;
    private static final double DEFAULT_MAX_SPEED_REVERSE = 0.2;
    private static final double DEFAULT_MARGIN_OF_ERROR = 5;
    private static final int DEFAULT_SETPOINT_SWITCH_TOLERANCE = 100;

    private boolean reachedSetPoint = false;

    public SimplePositionMotor(DcMotor motor, int positionUp, int positionDown) {
        this(motor, positionUp, positionDown, DEFAULT_SETPOINT, DEFAULT_MAX_SPEED_FORWARD, DEFAULT_MAX_SPEED_REVERSE, DEFAULT_MARGIN_OF_ERROR, DEFAULT_SETPOINT_SWITCH_TOLERANCE);
    }

    public SimplePositionMotor(DcMotor motor, int positionUp, int positionDown, int setPoint, double maxSpeedForward, double maxSpeedReverse, double marginOfError, int setPointSwitchTolerance) {
        this.motor = motor;
        this.positionUp = positionUp;
        this.positionDown = positionDown;
        this.setPoint = setPoint;
        this.maxSpeedForward = maxSpeedForward;
        this.maxSpeedReverse = maxSpeedReverse;
        this.marginOfError = marginOfError;
        this.setPointSwitchTolerance = setPointSwitchTolerance;
        motor.setMode(DEFAULT_RUNMODE);
        motor.setZeroPowerBehavior(DEFAULT_ZERO_POWER_BEHAVIOR);
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

        // true if position order needs to be corrected
        // boolean positionReverseCorrector = positionDown > positionUp;

        // If we float to the other setpoint, change the setpoint to the other of the two positions
        if (reachedSetPoint) {
            if (setPoint == positionUp) {
                // if (Math.abs(getErrorDown()) < setPointSwitchTolerance || (positionReverseCorrector ? getErrorDown() < 0 : getErrorDown() > 0)) {
                if (Math.abs(getErrorDown()) < setPointSwitchTolerance) {
                    setPoint = positionDown;
                }
            }
            else if (setPoint == positionDown) {
                // if (Math.abs(getErrorUp()) < setPointSwitchTolerance || (positionReverseCorrector ? getErrorUp() > 0 : getErrorUp() < 0)) {
                if (Math.abs(getErrorUp()) < setPointSwitchTolerance) {
                    setPoint = positionUp;
                }
            }
        }

        motor.setPower(speed);
    }

    // -------- Utilities --------

    public void resetEncoder() {
        if (motor.getMode() == DEFAULT_RUNMODE) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DEFAULT_RUNMODE);
        }
    }

    // -------- Access Process Variable and Setpoint --------

    public int getPosition() {
        return motor.getCurrentPosition();
    }

    public double getPower() {
        return motor.getPower();
    }

    public int getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(int setPoint) {
        reachedSetPoint = false;
        this.setPoint = setPoint;
    }

    public void toggleSetPoint() {
        if (setPoint == positionUp) {
            setSetPoint(positionDown);
        }
        else if (setPoint == positionDown) {
            setSetPoint(positionUp);
        }
        // If the arm is not currently in up or down position
        else {
            setSetPoint(positionUp);
        }
    }

    public double getError() {
        return setPoint - motor.getCurrentPosition();
    }

    public double getErrorUp() {
        return positionUp - motor.getCurrentPosition();
    }

    public double getErrorDown() {
        return positionDown - motor.getCurrentPosition();
    }

    public boolean getReachedSetpoint() {
        return reachedSetPoint;
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
