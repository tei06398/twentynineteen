package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="PID Test Tele-Op", group="TeleOp OpMode")
public class PIDTestTeleOp extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    // Toggle locks
    private boolean leftTriggerToggleLock = false;
    private boolean rightTriggerToggleLock = false;
    private boolean leftStickToggleLock = false;
    private boolean rightStickXToggleLock = false;
    private boolean rightStickYToggleLock = false;

    private PIDPositionMotor testPIDMotor;

    private int position1 = -100;
    private int position2 = -430; // -300

    private double pGainIncrement = 0.001;
    private double iGainIncrement = 0.001;
    private double dGainIncrement = 0.001;

    private int currentGainAdjustmentMode = 0;
    private static final int totalGainAdjustmentModes = 3;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        testPIDMotor = new PIDPositionMotor(this.hardwareMap.dcMotor.get("armMotor"));
        testPIDMotor.setSetPoint(position1);
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
                if (testPIDMotor.getSetPoint() == position1) {
                    testPIDMotor.setSetPoint(position2);
                }
                else {
                    testPIDMotor.setSetPoint(position1);
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
                testPIDMotor.resetEncoder();
            }
        }
        else {
            leftTriggerToggleLock = false;
        }

        // Right Stick y: Change current gain adjustment mode
        if (this.gamepad1.right_stick_y > 0.1) {
            if (!rightStickYToggleLock) {
                rightStickYToggleLock = true;
                currentGainAdjustmentMode = (currentGainAdjustmentMode + 1) % totalGainAdjustmentModes;
            }
        }
        else if (this.gamepad1.right_stick_y < -0.1) {
            if (!rightStickYToggleLock) {
                rightStickYToggleLock = true;
                currentGainAdjustmentMode = (currentGainAdjustmentMode - 1) % totalGainAdjustmentModes;
                if (currentGainAdjustmentMode < 0) {
                    currentGainAdjustmentMode += totalGainAdjustmentModes;
                }
            }
        }
        else {
            rightStickYToggleLock = false;
        }

        // Right Stick x: Adjust gain of current gain adjustment mode
        if (this.gamepad1.right_stick_x > 0.1) {
            if (!rightStickXToggleLock) {
                rightStickXToggleLock = true;

                switch (currentGainAdjustmentMode) {
                    case 0:
                        testPIDMotor.changeKp(pGainIncrement);
                        break;
                    case 1:
                        testPIDMotor.changeKi(iGainIncrement);
                        break;
                    case 2:
                        testPIDMotor.changeKd(dGainIncrement);
                        break;
                }

            }
        }
        else if (this.gamepad1.right_stick_x < -0.1) {
            if (!rightStickXToggleLock) {
                rightStickXToggleLock = true;

                switch (currentGainAdjustmentMode) {
                    case 0:
                        testPIDMotor.changeKp(-1 * pGainIncrement);
                        break;
                    case 1:
                        testPIDMotor.changeKi(-1 * iGainIncrement);
                        break;
                    case 2:
                        testPIDMotor.changeKd(-1 * dGainIncrement);
                        break;
                }

            }
        }
        else {
            rightStickXToggleLock = false;
        }

        // Left Stick x: Change max speed
        if (this.gamepad1.left_stick_x > 0.1) {
            if (!leftStickToggleLock) {
                leftStickToggleLock = true;
                testPIDMotor.MAX_SPEED += 0.1;
            }
        }
        else if (this.gamepad1.left_stick_x < -0.1) {
            if (!leftStickToggleLock) {
                leftStickToggleLock = true;
                if (testPIDMotor.MAX_SPEED - 0.1 >= 0)
                    testPIDMotor.MAX_SPEED -= 0.1;
            }
        }
        else {
            leftStickToggleLock = false;
        }

        String adjusting = "";
        switch (currentGainAdjustmentMode) {
            case 0:
                adjusting = "P Gain";
                break;
            case 1:
                adjusting = "I Gain";
                break;
            case 2:
                adjusting = "D Gain";
                break;
        }

        telemetry.addData("Adjusting", adjusting);
        telemetry.addData("P gain", testPIDMotor.getKp());
        telemetry.addData("I gain", testPIDMotor.getKi());
        telemetry.addData("D gain", testPIDMotor.getKd());
        telemetry.addData("Setpoint", testPIDMotor.getSetPoint());
        telemetry.addData("Current Position", testPIDMotor.getPosition());
        telemetry.addData("Motor Power", testPIDMotor.getMotor().getPower());
        telemetry.addData("Max Speed", testPIDMotor.MAX_SPEED);
        telemetry.addData("OpMode Runtime", runtime.toString());
        telemetry.update();

        // Run an iteration of the algorithm!
        testPIDMotor.runIteration();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {}

}
