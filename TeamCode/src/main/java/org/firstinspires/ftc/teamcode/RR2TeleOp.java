package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.DriverFunction.MAX_SPEED_RATIO;
import static org.firstinspires.ftc.teamcode.DriverFunction.MIN_SPEED_RATIO;
import static org.firstinspires.ftc.teamcode.DriverFunction.NORMAL_SPEED_RATIO;

@TeleOp(name="RR2 TeleOp", group="TeleOp OpMode")
public class RR2TeleOp extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private GunnerFunction.ArmController armController;

    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;

    private Servo lockServo;
    private double lockServoPosition;
    private double servoUpperLimit = 1;
    private double servoLowerLimit = 0.0;

    private final static double TURNING_SPEED_BOOST = 0.3;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();

        armController = new GunnerFunction.ArmController(hardwareMap.dcMotor.get("armMotor"), hardwareMap.dcMotor.get("winchMotor"), new GunnerFunction.TwoStateServo(hardwareMap.servo.get("lockServo"), 1, 0));
        driverFunction = new DriverFunction(hardwareMap, telemetry);

        this.lockServo = this.hardwareMap.servo.get("lockServo");
        lockServoPosition = servoLowerLimit;

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {
        armController.resetEncoders();
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

        // Right and Left Bumper: Locking/Unlocking Servo with Increments
        if (this.gamepad1.left_bumper) {
            if (lockServoPosition <= servoUpperLimit) {
                lockServoPosition += 0.05;
                telemetry.log().add("Decrement Servo");
            }
        }
        if (this.gamepad1.right_bumper) {
            if (lockServoPosition >= servoLowerLimit) {
                lockServoPosition -= 0.05;
                telemetry.log().add("Increment Servo");
            }
        }
        lockServo.setPosition(lockServoPosition);

        // if (this.gamepad1.right_bumper) {
        //     if (armController.isLocked()) {
        //         armController.lock();
        //     } else {
        //         armController.unlock();
        //     }
        // }

        // X Button: Toggles Arm Up/Down
        if (this.gamepad1.x) {
            if (armController.isArmUp()) {
                armController.armDown();
                telemetry.log().add("Lower Arm");
            } else {
                armController.armUp();
                telemetry.log().add("Raise Arm");
            }
        }

        // A Button: Resets Arm to Starting Position
        if (this.gamepad1.a) {
            armController.armReset();
            telemetry.log().add("Reset Arm Position");
        }

        // B Button: Slacks Arm
        if (this.gamepad1.b) {
            armController.slackArm();
            telemetry.log().add("Slack Arm");
        }

        // Y Button: Reset Motor Encoders
        if (this.gamepad1.y) {
            armController.resetEncoders();
            telemetry.log().add("Reset Motor Encoders");
        }

        // Slacking Winch
        armController.slackWinch();

        // --- GAMEPAD 2 ---

        // Nothing here yet...

        // Finish steering, putting power into hardware, and update telemetry
        steering.finishSteering();
        armController.doTelemetry(telemetry);
        telemetry.addData("lockServo Position", lockServoPosition);
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        driverFunction.resetAllEncoders();
        armController.resetEncoders();
    }

}
