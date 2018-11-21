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
    private boolean rightStickToggleLock = false;

    private PIDPositionMotor testPIDMotor;

    private int position1 = -100;
    private int position2 = -300;

    private double gainIncrement = 0.001;

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

        // Right Stick: Change p gain
        if (this.gamepad1.right_stick_x > 0.1) {
            if (!rightStickToggleLock) {
                rightStickToggleLock = true;
                testPIDMotor.changeKp(gainIncrement);
            }
        }
        else if (this.gamepad1.right_stick_x < -0.1) {
            if (!rightStickToggleLock) {
                rightStickToggleLock = true;
                testPIDMotor.changeKp(-1 * gainIncrement);
            }
        }
        else {
            rightStickToggleLock = false;
        }

        telemetry.addData("P gain", testPIDMotor.getKp());
        telemetry.addData("Setpoint", testPIDMotor.getSetPoint());
        telemetry.addData("Current Position", testPIDMotor.getPosition());
        telemetry.addData("Motor Power", testPIDMotor.getMotor().getPower());
        telemetry.addData("OpMode Runtime", runtime.toString());
        telemetry.update();

        // Run an iteration of the algorithm!
        testPIDMotor.runIteration();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {}

}
