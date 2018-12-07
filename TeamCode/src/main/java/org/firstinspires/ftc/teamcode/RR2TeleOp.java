package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.DriverFunction.MAX_SPEED_RATIO;
import static org.firstinspires.ftc.teamcode.DriverFunction.MIN_SPEED_RATIO;
import static org.firstinspires.ftc.teamcode.DriverFunction.NORMAL_SPEED_RATIO;

@TeleOp(name="RR2 TeleOp", group="TeleOp OpMode")
public class RR2TeleOp extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;

    private GunnerFunction gunnerFunction;

    private Servo lockServo;
    private Servo sweepServo;
    private double lockServoPosition;
    private double servoUpperLimit = 1;
    private double servoLowerLimit = 0.0;
    private double sweepServoPosition;
    private double sweepUpperLimit = 1;
    private double sweepLowerLimit = 0.0;

    private final static double TURNING_SPEED_BOOST = 0.3;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();

        gunnerFunction = new GunnerFunction(
                hardwareMap.dcMotor.get("armMotor"),
                hardwareMap.dcMotor.get("winchMotor"),
                new GunnerFunction.TwoStateServo(hardwareMap.servo.get("lockServo"), .35, 1, 0, true),
                hardwareMap.servo.get("sweepServo"),
                hardwareMap.dcMotor.get("chainMotor"),
                hardwareMap.dcMotor.get("slideMotor")
        );

        this.lockServo = this.hardwareMap.servo.get("lockServo");
        this.sweepServo = this.hardwareMap.servo.get("sweepServo");
        lockServoPosition = servoLowerLimit;
        sweepServoPosition = sweepLowerLimit;

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
            gunnerFunction.toggleArm();
        }

        // Right Joystick Y: Winch motor up/down
        if (this.gamepad2.right_stick_y > 0.5) {
            gunnerFunction.winchForward();
        }
        else if (this.gamepad2.right_stick_y < -0.5) {
            gunnerFunction.winchReverse();
        }
        else {
            gunnerFunction.stopChainMotor();
        }

        // Right/Left Trigger: Lock/Unlock winch servo
        if (this.gamepad2.right_bumper) {
            gunnerFunction.lock();
        }
        else if (this.gamepad2.left_bumper) {
            gunnerFunction.unlock();
        }

        // Left Joystick Y: Move sweeper slide in/out
        if (this.gamepad2.left_stick_y > 0.5) {
            gunnerFunction.incrementSlideMotor();
            telemetry.log().add("Incrementing Slide Motor");
        }
        else if (this.gamepad2.left_stick_y < -0.5) {
            gunnerFunction.decrementSlideMotor();
            telemetry.log().add("Decrementing Slide Motor");
        }

        // D-pad Up/Down: Increment/Decrement sweep servo
        if (this.gamepad2.dpad_down) {
            if (sweepServoPosition <= sweepUpperLimit) {
                sweepServoPosition += 0.05;
                telemetry.log().add("Decrement Sweep Servo");
            }
        }
        if (this.gamepad2.dpad_up) {
            if (sweepServoPosition >= sweepLowerLimit) {
                sweepServoPosition -= 0.05;
                telemetry.log().add("Increment Sweep Servo");
            }
        }
        sweepServo.setPosition(sweepServoPosition);

        // A: Start/Stop chainMotor
        if (this.gamepad1.a) {
            if (gunnerFunction.getChainMotorPower() == 0) {
                gunnerFunction.runChainMotor();
            }
            else {
                gunnerFunction.stopChainMotor();
            }
        }

        // Finish steering, putting power into hardware, and update telemetry
        steering.finishSteering();
        gunnerFunction.runArmMotorIteration();
        gunnerFunction.doTelemetry(telemetry);
        telemetry.addData("lockServo Position", lockServoPosition);
        telemetry.addData("sweepServo Position", sweepServoPosition);
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
