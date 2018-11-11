/*
Troubleshooting the controller:
- Restart the robot (app, not phone)
- Unplug and replug the controller(s) from the USB hub
- Press start+A or start+B
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.*;

@TeleOp(name="Winch Test TeleOp", group="TeleOp OpMode")
public class WinchTestTeleOp extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor toggleMotor;
    private double position_1 = 0;
    private double position_2 = 1;
    boolean currentState = false;
    boolean toggle = false;

    private Servo testServo;
    private double testServoPosition;

    private double servoUpperLimit = 1;
    private double servoLowerLimit = 0.0;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        this.toggleMotor = this.hardwareMap.dcMotor.get("testMotor");
        toggleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        toggleMotor.setPower(position_1);

        this.testServo = this.hardwareMap.servo.get("testServo");
        testServoPosition = servoLowerLimit;
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

        // Motor power
        if (this.gamepad1.right_trigger > 0.1) {
            if (currentState == false) {
                currentState = true;
                toggle = !toggle;
            }
        }
        else {
            currentState = false;
        }

        if (toggle) {
            toggleMotor.setPower(position_1);
        }
        else {
            toggleMotor.setPower(position_2);
        }

        // Servo position
        if (this.gamepad1.left_bumper) {
            if (testServoPosition < servoUpperLimit) {
                testServoPosition += 0.05;
            }
        }
        else if (this.gamepad1.right_bumper) {
            if (testServoPosition > servoLowerLimit) {
                testServoPosition -= 0.05;
            }
        }
        testServo.setPosition(testServoPosition);

        telemetry.addData("Toggle", toggle);
        telemetry.addData("Test Servo Position", testServoPosition);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {}

}
