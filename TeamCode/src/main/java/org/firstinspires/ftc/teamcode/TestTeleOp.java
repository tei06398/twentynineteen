package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.*;

@TeleOp(name="Test TeleOp", group="TeleOp OpMode")
public class TestTeleOp extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    /*
    private DcMotor testMotor;
    private double testMotorPower;
    */

    private Servo testServo;
    private double testServoPosition;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        /*
        this.testMotor = this.hardwareMap.dcMotor.get("testMotor");
        testMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        testMotorPower = 0;
        */

        this.testServo = this.hardwareMap.servo.get("testServo");
        testServoPosition = 0;
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        runtime.reset();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        /*
        // Motor power
        if (this.gamepad1.right_stick_x > 0.1) {
            testMotorPower = 0.5;
        }
        else if (this.gamepad1.right_stick_x < -0.1) {
            testMotorPower = -0.5;
        }
        else {
            testMotorPower = 0;
        }
        this.testMotor.setPower(testMotorPower);
        */

        // Servo position
        if (this.gamepad1.left_bumper) {
            testServoPosition += 0.01;
        }
        if (this.gamepad1.right_bumper) {
            testServoPosition -= 0.01;
        }
        testServo.setPosition(testServoPosition);

        // telemetry.addData("Test Motor Power", testMotorPower);
        telemetry.addData("Test Servo Position", testServoPosition);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.update();

    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
    }

}
