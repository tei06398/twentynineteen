package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

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

    private double previousError = 0;
    private double iTerm = 0; // errorIntegral

    private SimpleTimer loopTimer;
    private boolean firstIteration = true;

    private boolean clampOutput = true;

    public double MAX_SPEED = 0.4; // TODO: Make private and add accessors/mutators

    private static final DcMotor.RunMode DEFAULT_RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER; // DcMotor.RunMode.RUN_WITHOUT_ENCODER

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
        loopTimer = new SimpleTimer();
    }

    /*
    This method will run synchronously unless we get into multithreading, which I do not want to get in to...
    Thus, instead of having this class run the loop have this method only run one iteration of the method, and
    call the method once every loop of the main loop in the opmode.
    Downsides: Lower and potentially less consistent looptime - bad for the PID control system, since the
    coefficients are constant values - will have to attempt to time the loop control loop.
    Upsides: It will hopefully avoid messing with multithreading.
    */
    public void runIteration() {

        /*
        Because the timer is initialized when the PIDPositionMotor object is created in the opMode init, but
        significant time elapses between the first run of the control loop and this start of the timer, the timer
        should be reset on the first run of the loop.
         */
        if (firstIteration)
            loopTimer.reset();
        firstIteration = false;

        double dt = loopTimer.getElapsedSeconds();

        double error = getError();

        double pTerm = kp * error;

        iTerm += ki * (error - previousError) * dt;

        double dTerm = (error - previousError) / dt;

        previousError = error;

        double speed  = pTerm + iTerm + dTerm;

        // Force the speed to be less than or equal to max speed
        if (clampOutput) {
            speed = Math.max(Math.min(speed, MAX_SPEED), MAX_SPEED * -1);
        }

        motor.setPower(speed);

        loopTimer.reset();
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

    // -------- Nested Timer Class for determining looptimes  --------

    public class SimpleTimer {

        private long startTime;

        // Calling the default constructor starts the timer
        public SimpleTimer() {
            reset();
        }

        public void reset() {
            startTime = System.nanoTime();
        }

        public double getElapsedNanoseconds() {
            return System.nanoTime() - startTime;
        }

        public double getElapsedSeconds() {
            return getElapsedNanoseconds() / Math.pow(10, 9);
        }

    }

}
