package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AutonFunction {

    private Telemetry telemetry;

    private DcMotor armMotor;
    private DcMotor winchMotor;

    private static final double WINCH_POWER = 0.5; // 0.2

    // TODO: Get values
    private static final int WINCH_COAST_POSITION = 200; // 3000 // Two wheels hit ground after coasting, robot stops
    private static final int WINCH_RUN_POSITION = 5000; // All four wheels hit ground

    private static final double ARM_POWER = 0.1;

    private static final int ARM_POSITION_DOWN = 180;

    private DcMotor.RunMode ARM_MOTOR_RUNMODE = DcMotor.RunMode.RUN_TO_POSITION;
    private DcMotor.RunMode WINCH_MOTOR_RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER;

    private Servo lockServo;
    private Servo markerDropperServo;

    private static final double WINCH_SERVO_LOCK_POSITION = .35;
    private static final double WINCH_SERVO_UNLOCK_POSITION = 0.9;

    private static final double DROPPER_OPEN_POSITION = 0.6;
    private static final double DROPPER_CLOSED_POSITION = 1.0;

    private Servo rightSweepServo;
    private Servo leftSweepServo;

    // TODO: (Potentially) better zero-movement values
    private double RIGHT_SWEEP_CENTER = 0.5;
    private double LEFT_SWEEP_CENTER = 0.5;

    public AutonFunction(HardwareMap hardwareMap, Telemetry telemetry) {
        this.armMotor = hardwareMap.dcMotor.get("armMotor");
        this.winchMotor = hardwareMap.dcMotor.get("winchMotor");
        this.lockServo = hardwareMap.servo.get("lockServo");
        this.markerDropperServo = hardwareMap.servo.get("markerDropperServo");

        // This is also called in RR2Auton... for redundancy (or so we tell ourselves)
        undropMarker();

        this.leftSweepServo = hardwareMap.servo.get("leftSweepServo");
        this.rightSweepServo = hardwareMap.servo.get("rightSweepServo");

        this.armMotor.setMode(ARM_MOTOR_RUNMODE);
        this.winchMotor.setMode(WINCH_MOTOR_RUNMODE);

        this.telemetry = telemetry;

        // Enable servo controller pwm
        ServoController servoController = hardwareMap.servoController.get("Servo Controller 1");
        servoController.pwmEnable();
    }

    // --- Lock Servo ---

    public void lockServo() {
        lockServo.setPosition(WINCH_SERVO_LOCK_POSITION);
    }

    public void unlockServo() {
        lockServo.setPosition(WINCH_SERVO_UNLOCK_POSITION);
    }

    // --- Team Marker Dropper Servo ---

    public void dropMarker() {
        markerDropperServo.setPosition(DROPPER_OPEN_POSITION);
    }

    public void undropMarker() {
        markerDropperServo.setPosition(DROPPER_CLOSED_POSITION);
    }

    // --- Winch ---

    // NOTE: Direction winch is wound in will be important
    // TODO: May have to flip comparators if positions are negative

    public boolean winchCoastFinished() {
        return winchMotor.getCurrentPosition() > WINCH_COAST_POSITION;
    }

    public boolean winchRunFinished() {
        return winchMotor.getCurrentPosition() > WINCH_RUN_POSITION;
    }

    public void runWinch() {
        winchMotor.setPower(WINCH_POWER);
    }

    public void stopWinch() {
        winchMotor.setPower(0);
    }

    public int getWinchPosition() {
        return winchMotor.getCurrentPosition();
    }

    public void setWinchPower(double power) {
        winchMotor.setPower(power);
    }

    public void setWinchTarget(int position) {
        winchMotor.setTargetPosition(position);
    }

    public void slackWinch() {
        winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void brakeWinch() {
        winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

     // --- Arm ---

    public void powerArm() {
        armMotor.setPower(ARM_POWER);
    }

    public void zeroPowerArm() {
        armMotor.setPower(0);
    }

    public int getArmPosition() {
        return armMotor.getCurrentPosition();
    }

    public void setArmTargetPosition(int target) {
        armMotor.setTargetPosition(target);
    }

    public void armDown() {
        armMotor.setTargetPosition(ARM_POSITION_DOWN);
    }

    // --- Sweeper servo ---

    public void centerSweepServos() {
        leftSweepServo.setPosition(LEFT_SWEEP_CENTER);
        rightSweepServo.setPosition(RIGHT_SWEEP_CENTER);
    }

    // --- Util ---

    public void resetAllEncoders() {
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        winchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(ARM_MOTOR_RUNMODE);
        winchMotor.setMode(WINCH_MOTOR_RUNMODE);
    }

    public void writeTelemetry() {
        telemetry.addData("Lock Servo Position", lockServo.getPosition());
        telemetry.addData("Marker Dropper Servo Position", markerDropperServo.getPosition());
        telemetry.addData("Winch Position", winchMotor.getCurrentPosition());
        telemetry.addData("Arm Position", armMotor.getCurrentPosition());
    }
}
