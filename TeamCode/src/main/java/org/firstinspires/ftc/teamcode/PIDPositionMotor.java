package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.sun.tools.javac.tree.DCTree;

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

    private double previousValue;

    private static final DcMotor.RunMode DEFAULT_RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER;

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

    /*
    This method will run synchronously unless we get into multithreading, which I do not want to get in to...
    Thus, instead of having this class run the loop have this method only run one iteration of the method, and
    call the method once every loop of the main loop in the opmode.
    Downsides: Lower and potentially less consistent looptime - bad for the PID control system, since the
    coefficients are constant values.
    Upsides: It will hopefully avoid messing with multithreading.
    */
    public void runIteration() {
        // TODO
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
        return  motor.getCurrentPosition() - setPoint;
    }

    // -------- Access PID Coefficients --------

    public double getKp() {
        return kp;
    }
    public double getKi() {
        return ki;
    }
    public double getKd() {
        return kd;
    }

    public void setKp(double kp) {
        this.kp = kp;
    }
    public void setKi(double ki) {
        this.ki = ki;
    }
    public void setKd(double kd) {
        this.kd = kd;
    }

    public void changeKp(double delta) {
        kp += delta;
    }
    public void changeKi(double delta) {
        ki += delta;
    }
    public void changeKd(double delta) {
        kd += delta;
    }

}
