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

    private GunnerFunction gunnerFunction;

    // Toggle locks
    private boolean gamepad2YToggleLock = false;
    private boolean gamepad2ABToggleLock = false;
    private boolean gamepad2LeftJoyYToggleLock = false;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();

        gunnerFunction = new GunnerFunction(
                hardwareMap.dcMotor.get("armMotor"),
                hardwareMap.dcMotor.get("winchMotor"),
                new GunnerFunction.TwoStateServo(hardwareMap.servo.get("lockServo"), .35, 0.9, 0, true),
                hardwareMap.servo.get("sweepServo"),
                hardwareMap.dcMotor.get("chainMotor"),
                hardwareMap.dcMotor.get("slideMotor")
        );

        // TEST
        gunnerFunction.sweepServoStop();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {}

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        runtime.reset();
        gunnerFunction.resetEncoders();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        // ----- Gamepad 1: Driving Functions -----

        // Left/Right Triggers: Set driving speed ratio
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

        // ----- Gamepad 2: Gunner Functions -----

        // Y Button: Toggle Arm Up/Down
        if (this.gamepad2.y) {
            if (!gamepad2YToggleLock) {
                gamepad2YToggleLock = true;
                gunnerFunction.toggleArm();
            }
        }
        else {
            gamepad2YToggleLock = false;
        }

        /*
        // Right Joystick Y: Winch motor up/down
        if (this.gamepad2.right_stick_y > 0.5) {
            gunnerFunction.winchForward();
            telemetry.addData("Winch Control", "FORWARD");
        }
        else if (this.gamepad2.right_stick_y < -0.5) {
            gunnerFunction.winchReverse();
            telemetry.addData("Winch Control", "REVERSE");
        }
        else {
            gunnerFunction.winchStop();
            telemetry.addData("Winch Control", "STOP");
        }
        */
        // TODO: gunnerFunction.winchStop();
        gunnerFunction.winchStop();

        // Right/Left Trigger: Lock/Unlock winch servo
        if (this.gamepad2.right_trigger > 0.5) {
            gunnerFunction.lock();
        }
        else if (this.gamepad2.left_trigger > 0.5) {
            gunnerFunction.unlock();
        }

        // Left Joystick Y: Move sweeper slide in/out
        if (this.gamepad2.left_stick_y > 0.5) {
            if (!gamepad2LeftJoyYToggleLock) {
                gamepad2LeftJoyYToggleLock = true;
                gunnerFunction.incrementSlideMotor();
            }
        }
        else if (this.gamepad2.left_stick_y < -0.5) {
            if (!gamepad2LeftJoyYToggleLock) {
                gamepad2LeftJoyYToggleLock = true;
                gunnerFunction.decrementSlideMotor();
            }
        }
        else {
            gamepad2LeftJoyYToggleLock = false;
        }
        gunnerFunction.runSlideMotorToTarget();
        gunnerFunction.powerSlideMotor();

        // D-pad Y: Increment/Decrement sweep servo
        if (this.gamepad2.dpad_up) {
            gunnerFunction.sweepServoForward();
        }
        else if (this.gamepad2.dpad_down) {
            gunnerFunction.sweepServoReverse();
        }
        else {
            gunnerFunction.sweepServoStop();
        }

        // A/B: Start/Reverse/Stop chainMotor
        if (this.gamepad2.a) {
            if (!gamepad2ABToggleLock) {
                gamepad2ABToggleLock = true;
                gunnerFunction.runChainMotor();
            }
        }
        else if (this.gamepad2.b) {
            if (!gamepad2ABToggleLock) {
                gamepad2ABToggleLock = true;
                gunnerFunction.runChainMotorReverse();
            }
        }
        else {
            gamepad2ABToggleLock = false;
            gunnerFunction.stopChainMotor();
        }

        // Finish steering, putting power into hardware, and update arm motor power
        steering.finishSteering();
        gunnerFunction.runArmMotorIteration();

        // Update telemetry
        gunnerFunction.doTelemetry(telemetry);
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        driverFunction.resetAllEncoders();
        gunnerFunction.resetEncoders();
    }

}
