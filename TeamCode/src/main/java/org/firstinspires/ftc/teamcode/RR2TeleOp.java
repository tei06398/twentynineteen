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

    // Change the orientation of the robot for the driver
    boolean directionReverse = false;

    // Toggle locks
    private boolean gamepad1BToggleLock = false;
    private boolean gamepad2RlBumperToggleLock = false;
    private boolean gamepad1XToggleLock = false;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        gunnerFunction = new GunnerFunction(hardwareMap, telemetry);

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
        gunnerFunction.powerArmMotor();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        // ----- Gamepad 1: Driving Functions -----

        // X Button: Toggle forwards direction

        if (this.gamepad1.x) {
            if (!gamepad1XToggleLock) {
                gamepad1XToggleLock = true;
                directionReverse = !directionReverse;
            }
        }
        else {
            gamepad1XToggleLock = false;
        }

        // D-Pad: Compass rose drive
        if (!directionReverse) {
            if (this.gamepad1.dpad_right) {
                steering.moveDegrees(0, MIN_SPEED_RATIO);
            }
            if (this.gamepad1.dpad_up) {
                steering.moveDegrees(90, MIN_SPEED_RATIO);
            }
            if (this.gamepad1.dpad_left) {
                steering.moveDegrees(180, MIN_SPEED_RATIO);
            }
            if (this.gamepad1.dpad_down) {
                steering.moveDegrees(270, MIN_SPEED_RATIO);
            }
        }
        else {
            if (this.gamepad1.dpad_right) {
                steering.moveDegrees(180, MIN_SPEED_RATIO);
            }
            if (this.gamepad1.dpad_up) {
                steering.moveDegrees(270, MIN_SPEED_RATIO);
            }
            if (this.gamepad1.dpad_left) {
                steering.moveDegrees(0, MIN_SPEED_RATIO);
            }
            if (this.gamepad1.dpad_down) {
                steering.moveDegrees(90, MIN_SPEED_RATIO);
            }
        }

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
            double angle;
            if (!directionReverse) {
                angle = Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x);
            }
            else {
                angle = Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x);
            }
            telemetry.addData("Angle", angle);
            steering.moveRadians(angle);
        }
        else {
            telemetry.addData("Angle", 0);
        }

        // Y Button: Arm Up
        if (this.gamepad1.y) {
            gunnerFunction.armUp();
        }

        // A Button: Arm Down
        if (this.gamepad1.a) {
            gunnerFunction.armDown();
        }

        /*
        // TODO: WARNING: This will lock up the tele-op for ~4 seconds
        // Left Bumper: Re-zero the arm position
        if (this.gamepad1.left_bumper) {
            if (!gamepad1LBumperToggleLock) {
                gamepad1LBumperToggleLock = true;

                gunnerFunction.reZeroArm(this);
            }
        }
        else {
            gamepad1LBumperToggleLock = false;
        }
        */

        // A: Toggle the pivot arm motor power
        if (this.gamepad1.b) {
            if (!gamepad1BToggleLock) {
                gamepad1BToggleLock = true;
                gunnerFunction.toggleArmPower();
                // gunnerFunction.zeroPowerArmMotor();
            }
        }
        else {
            gamepad1BToggleLock = false;
            // gunnerFunction.powerArmMotor();
        }

        // ----- Gamepad 2: Gunner Functions -----

        // Right Joystick Y: Move sweeper slide in/out
        if (this.gamepad2.right_stick_y > 0.5) {
            gunnerFunction.runSlideMotorReverse();
        }
        else if (this.gamepad2.right_stick_y < -0.5) {
            gunnerFunction.runSlideMotor();
        }
        else {
            gunnerFunction.stopSlideMotor();
        }

        // Right/Left Trigger: Lock/Unlock winch servo
        if (this.gamepad2.right_trigger > 0.5) {
            gunnerFunction.lock();
        }
        else if (this.gamepad2.left_trigger > 0.5) {
            gunnerFunction.unlock();
            gunnerFunction.stopSlideMotor();
        }

        // Left Joystick Y: Winch motor up/down
        if (this.gamepad2.left_stick_y > 0.5) {
            gunnerFunction.winchForward();
        }
        else if (this.gamepad2.left_stick_y < -0.5) {
            gunnerFunction.winchReverse();
        }
        else {
            gunnerFunction.winchStop();
        }

        // D-pad Y: Increment/Decrement sweep servo
        if (this.gamepad2.dpad_up) {
            gunnerFunction.sweepServoReverse();
        }
        else if (this.gamepad2.dpad_down) {
            gunnerFunction.sweepServoForward();
        }
        else {
            gunnerFunction.sweepServoStop();
        }

        // Left/Right Bumper: Start/Reverse/Stop chainMotor
        if (this.gamepad2.right_bumper) {
            if (!gamepad2RlBumperToggleLock) {
                gamepad2RlBumperToggleLock = true;
                gunnerFunction.runChainMotor();
            }
        }
        else if (this.gamepad2.left_bumper) {
            if (!gamepad2RlBumperToggleLock) {
                gamepad2RlBumperToggleLock = true;
                gunnerFunction.runChainMotorReverse();
            }
        }
        else {
            gamepad2RlBumperToggleLock = false;
            gunnerFunction.stopChainMotor();
        }

        // Finish steering, putting power into hardware
        steering.finishSteering();

        // Update telemetry
        telemetry.addData("Runtime", runtime.toString());
        telemetry.addData("Orientation", !directionReverse ? "Normal" : "Reversed");
        telemetry.addData("Pivot Motor", gunnerFunction.armMotorPowered() ? "Powered" : "Zero Power");
        telemetry.addLine();
        telemetry.addData("Slide Motor Position", gunnerFunction.getSlideMotorPosition());
        telemetry.addData("Slide Min, Max", gunnerFunction.getSLIDE_POSITION_MIN() + ", " + gunnerFunction.getSLIDE_POSITION_MAX());
        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        // driverFunction.resetAllEncoders();
        // gunnerFunction.resetEncoders();
    }

}
