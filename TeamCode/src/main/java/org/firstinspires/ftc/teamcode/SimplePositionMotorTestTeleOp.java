package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Simple Position Motor Test Tele-Op", group="TeleOp OpMode")
public class SimplePositionMotorTestTeleOp extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    // Toggle locks
    private boolean leftTriggerToggleLock = false;
    private boolean rightTriggerToggleLock = false;
    private boolean leftStickToggleLock = false;
    private boolean rightStickXToggleLock = false;
    private boolean rightStickYToggleLock = false;
    private boolean leftBumperToggleLock = false;

    private SimplePositionMotor simplePositionMotor;

    private int position1 = -100;
    private int position2 = -430;

    private double maxSpeedIncrement = 0.05;
    private double marginOfErrorIncrement = 1;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        simplePositionMotor = new SimplePositionMotor(this.hardwareMap.dcMotor.get("armMotor"));
        simplePositionMotor.setSetPoint(position1);
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {}

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        runtime.reset();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        // Right Trigger: Toggle setpoint
        if (this.gamepad1.right_trigger > 0.1) {
            if (!rightTriggerToggleLock) {
                rightTriggerToggleLock = true;
                if (simplePositionMotor.getSetPoint() == position1) {
                    simplePositionMotor.setSetPoint(position2);
                }
                else {
                    simplePositionMotor.setSetPoint(position1);
                }
            }
        }
        else {
            rightTriggerToggleLock = false;
        }

        // Left Trigger: reset encoder
        if (this.gamepad1.left_trigger > 0.1) {
            if (!leftTriggerToggleLock) {
                leftTriggerToggleLock = true;
                simplePositionMotor.resetEncoder();
            }
        }
        else {
            leftTriggerToggleLock = false;
        }

        // Right Stick y: // TODO
        if (this.gamepad1.right_stick_y > 0.1) {
            if (!rightStickYToggleLock) {
                rightStickYToggleLock = true;
                // TODO
            }
        }
        else if (this.gamepad1.right_stick_y < -0.1) {
            if (!rightStickYToggleLock) {
                rightStickYToggleLock = true;
                // TODO
            }
        }
        else {
            rightStickYToggleLock = false;
        }

        // Right Stick x: Change margin of error
        if (this.gamepad1.right_stick_x > 0.1) {
            if (!rightStickXToggleLock) {
                rightStickXToggleLock = true;
                simplePositionMotor.changeMarginOfError(marginOfErrorIncrement);
            }
        }
        else if (this.gamepad1.right_stick_x < -0.1) {
            if (!rightStickXToggleLock) {
                rightStickXToggleLock = true;
                simplePositionMotor.changeMarginOfError(-1 * marginOfErrorIncrement);
            }
        }
        else {
            rightStickXToggleLock = false;
        }

        // Left Stick x: Change max speed
        if (this.gamepad1.left_stick_x > 0.1) {
            if (!leftStickToggleLock) {
                leftStickToggleLock = true;
                simplePositionMotor.changeMaxSpeed(maxSpeedIncrement);
            }
        }
        else if (this.gamepad1.left_stick_x < -0.1) {
            if (!leftStickToggleLock) {
                leftStickToggleLock = true;
                if (simplePositionMotor.getMaxSpeed() - maxSpeedIncrement >= 0)
                    simplePositionMotor.changeMaxSpeed(-1 * maxSpeedIncrement);
            }
        }
        else {
            leftStickToggleLock = false;
        }

        // Left bumper: // TODO
        if (this.gamepad1.left_bumper) {
            if (!leftBumperToggleLock) {
                leftBumperToggleLock = true;
                // TODO
            }
        }
        else {
            leftBumperToggleLock = false;
        }

        telemetry.addData("Setpoint", simplePositionMotor.getSetPoint());
        telemetry.addData("Current Position", simplePositionMotor.getPosition());
        telemetry.addData("Motor Power", simplePositionMotor.getMotor().getPower());
        telemetry.addData("Max Speed", simplePositionMotor.getMaxSpeed());
        telemetry.addData("Margin of Error", simplePositionMotor.getMarginOfError());
        telemetry.addData("OpMode Runtime", runtime.toString());
        telemetry.update();

        // Run an iteration of the algorithm!
        simplePositionMotor.runIteration();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {}

}
