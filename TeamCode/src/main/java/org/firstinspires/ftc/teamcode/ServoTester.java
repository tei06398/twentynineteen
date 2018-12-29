package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Servo Tester", group="TeleOp OpMode")
public class ServoTester extends OpMode {

    private Servo servo;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        servo = hardwareMap.servo.get("markerDropperServo");
        servo.setPosition(0.5);
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {}

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {}

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        if (this.gamepad1.left_bumper) {
            servo.setPosition(servo.getPosition() - 0.05);
        }
        else if (this.gamepad1.right_bumper) {
            servo.setPosition(servo.getPosition() + 0.05);
        }

        if (this.gamepad1.left_trigger > 0.5) {
            servo.setPosition(0);
        }
        else if (this.gamepad1.right_trigger > 0.5) {
            servo.setPosition(1);
        }

        telemetry.addData("Position", servo.getPosition());
        telemetry.update();

    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {}

}
