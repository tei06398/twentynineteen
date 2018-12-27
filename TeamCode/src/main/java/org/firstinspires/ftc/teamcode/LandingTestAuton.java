package org.firstinspires.ftc.teamcode;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Landing Test Auton")
public class LandingTestAuton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;
    private AutonFunction autonFunction;

    public static final double MAX_COAST_SECONDS = 6;

    @Override
    public void runOpMode() {

        // --- Init ---
        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        autonFunction = new AutonFunction(hardwareMap, telemetry);

        telemetry.addData("Status", "Initialized");
        autonFunction.writeTelemetry();
        telemetry.update();

        driverFunction.resetAllEncoders();
        autonFunction.resetAllEncoders();

        autonFunction.lockServo();
        autonFunction.zeroPowerArm(); // We don't want to power the arm until we've retracted all the way

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        autonFunction.writeTelemetry();
        telemetry.update();

        // --- Wait for start ---
        waitForStart();
        runtime.reset();

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
        steering.setSpeedRatio(0.3);

        long tmpDelay = 500;

        // Move against lander
        steering.moveDegrees(270);
        steering.finishSteering();
        sleep(1000);
        steering.stopAllMotors();

        sleep(tmpDelay);

        // Move away slightly
        steering.moveDegrees(90);
        steering.finishSteering();
        sleep(300);
        steering.stopAllMotors();

        sleep(tmpDelay);

        // Strafe out
        steering.moveDegrees(0);
        steering.finishSteering();
        sleep(1000);
        steering.stopAllMotors();

        sleep(tmpDelay);

        // Re-align with lander
        steering.moveDegrees(270);
        steering.finishSteering();
        sleep(500);
        steering.stopAllMotors();

        sleep(tmpDelay);


        // Get the arm to hold its position
        // autonFunction.powerArm();
        // autonFunction.setArmTargetPosition(autonFunction.getArmPosition());

        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            autonFunction.writeTelemetry();
            telemetry.update();
        }

    }

}