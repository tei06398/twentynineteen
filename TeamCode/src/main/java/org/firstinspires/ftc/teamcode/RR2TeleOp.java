package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.DriverFunction.MAX_SPEED_RATIO;
import static org.firstinspires.ftc.teamcode.DriverFunction.MIN_SPEED_RATIO;
import static org.firstinspires.ftc.teamcode.DriverFunction.NORMAL_SPEED_RATIO;

@TeleOp(name="RR2 TeleOp", group="TeleOp OpMode")
public class RR2TeleOp extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;

    private final static double TURNING_SPEED_BOOST = 0.3;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
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

        // --- GAMEPAD 1 ---

        // Set speed ratio depending on triggers pressed
        if (this.gamepad1.right_trigger > 0.5) {
            steering.setSpeedRatio(MAX_SPEED_RATIO); // Left trigger: minimum speed ratio
        }
        else if (this.gamepad1.left_trigger > 0.5) {
            steering.setSpeedRatio(MIN_SPEED_RATIO); // Right trigger: maximum speed ratio
        }
        else {
            steering.setSpeedRatio(NORMAL_SPEED_RATIO);
        }

        // Right Stick: Turn/Rotate
        if (this.gamepad1.right_stick_x > 0.1) {
            steering.turnClockwise();
            steering.setSpeedRatio(Math.min(1, steering.getSpeedRatio() + TURNING_SPEED_BOOST));
        }
        else if (this.gamepad1.right_stick_x < -0.1) {
            steering.turnCounterclockwise();
            steering.setSpeedRatio(Math.min(1, steering.getSpeedRatio() + TURNING_SPEED_BOOST));
        }

        // Left Stick: Drive/Strafe
        if (Math.abs(this.gamepad1.left_stick_x) > 0.1 || Math.abs(this.gamepad1.left_stick_y) > 0.1) {
            double angle = Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x);
            telemetry.addData("Angle", angle);
            steering.moveRadians(angle);
        }
        else {
            telemetry.addData("Angle", 0);
        }

        // --- GAMEPAD 2 ---

        // Nothing here yet...

        // Finish steering, putting power into hardware, and update telemetry
        steering.finishSteering();
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
    }

}
