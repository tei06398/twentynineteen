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

    private static final double DEFAULT_KP = 0;
    private static final double DEFAULT_KI = 0;
    private static final double DEFAULT_KD = 0;

    private double previousValue;

    private static final DcMotor.RunMode DEFAULT_RUNMODE = DcMotor.RunMode.RUN_WITHOUT_ENCODER; // DcMotor.RunMode.RUN_USING_ENCODER

    public PIDPositionMotor(DcMotor motor) {
        this(motor, DEFAULT_SETPOINT, DEFAULT_KP, DEFAULT_KI, DEFAULT_KD);
    }

    public PIDPositionMotor(DcMotor motor, int setPoint, double kp, double ki, double kd) {
        this.motor = motor;
        this.setPoint = setPoint;
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        motor.setMode(DEFAULT_RUNMODE);
        resetEncoder();
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

        // Currently: only proportional gain

        double pTerm = kp * getError();

        motor.setPower(pTerm);

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
        if (kp >= 0)
            this.kp = kp;
    }
    public void setKi(double ki) {
        if (ki >= 0)
            this.ki = ki;
    }
    public void setKd(double kd) {
        if (kd >= 0)
            this.kd = kd;
    }

    public void changeKp(double delta) {
        if (kp + delta >= 0)
            kp += delta;
    }
    public void changeKi(double delta) {
        if (ki + delta >= 0)
            ki += delta;
    }
    public void changeKd(double delta) {
        if (kd + delta >= 0)
            kd += delta;
    }

}
