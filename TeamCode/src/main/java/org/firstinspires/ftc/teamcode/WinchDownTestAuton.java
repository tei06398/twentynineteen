package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Winch Down Test Auton")
public class WinchDownTestAuton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;
    private AutonFunction autonFunction;

    public static final double MAX_COAST_SECONDS = 6;

    public static final double NORMAL_SPEED_RATIO = 0.3;

    public static final long MOVE_DELAY_MS = 50;
    public static final long LONG_DELAY_MS = 300;

    @Override
    public void runOpMode() {

        // --- Init ---

        // Initial telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize utilities
        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        autonFunction = new AutonFunction(hardwareMap, telemetry);

        // To have this called ASAP
        autonFunction.undropMarker();
        autonFunction.centerSweepServos();

        // Reset encoders
        driverFunction.resetAllEncoders();
        autonFunction.resetAllEncoders();

        // Set motors to initial positions/powers
        autonFunction.centerSweepServos();
        autonFunction.lockServo();
        autonFunction.zeroPowerArm(); // We don't want to power the arm until we've retracted all the way

        while (!this.isStarted()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

        // --- Start ---

        runtime.reset();

        telemetry.addData("Status", "Started");
        autonFunction.writeTelemetry();
        telemetry.update();

        // LANDING

        autonFunction.unlockServo();

        // Coast winch until winch position reaches a relatively steady state
        while (opModeIsActive() && !autonFunction.winchCoastFinished() && runtime.seconds() < MAX_COAST_SECONDS) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            autonFunction.writeTelemetry();
            telemetry.update();
        }

        // Run winch to get us all the way down
        while (opModeIsActive() && !autonFunction.winchRunFinished()) {
            autonFunction.runWinch();
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            autonFunction.writeTelemetry();
            telemetry.update();
        }
        autonFunction.stopWinch();

        // Set a relatively slow speed ratio
        steering.setSpeedRatio(NORMAL_SPEED_RATIO);

        sleep(MOVE_DELAY_MS);

        // Move against lander
        steering.moveDegrees(270);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Move away slightly
        steering.moveDegrees(90);
        steering.finishSteering();
        sleep(200);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Strafe out
        steering.moveDegrees(0);
        steering.finishSteering();
        sleep(350);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Re-align with lander
        steering.moveDegrees(270);
        steering.finishSteering();
        sleep(400);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Negative means arm above flat, positive means arm below flat
        if (autonFunction.getArmPosition() < 0) {
            autonFunction.powerArm();
            autonFunction.armDown();
        }

        sleep(LONG_DELAY_MS);

        // Strafe to align center of lander
        steering.moveDegrees(180);
        steering.finishSteering();
        sleep(350);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // TODO: Consider if this should be commented, or the time adjusted again
        // Re-align with lander
        steering.moveDegrees(270);
        steering.finishSteering();
        sleep(200); // 600
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // run until opmode ends
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

        // To attempt to prevent the auton from dropping the marker if we don't reach the depot
        autonFunction.undropMarker();
    }
}
