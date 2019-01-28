package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Crater Knock Test Auton")
public class CraterKnockTestAuton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;
    private AutonFunction autonFunction;

    public static final double MAX_COAST_SECONDS = 6;

    public static final double NORMAL_SPEED_RATIO = 0.3;
    public static final double MEDIUM_SPEED_RATIO = 0.5;
    public static final double FAST_SPEED_RATIO = 0.7;

    public static final long MOVE_DELAY_MS = 500;

    public static final long LONG_DELAY_MS = 300;
    private static final long RETREAT_MS = 1300;
    private static final long DEPOT_TURN_MS = 3000;
    private static final long MARKER_DROP_DELAY_MS = 800;

    private static final int CV_ITERATIONS = 5;
    private static final long CV_LOOP_DELAY = 200;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        autonFunction = new AutonFunction(hardwareMap, telemetry);

        driverFunction.resetAllEncoders();
        autonFunction.resetAllEncoders();

        autonFunction.undropMarker();

        while (!this.isStarted()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
        runtime.reset();

        for (int i = 0; i < 3; i++) {

            steering.setSpeedRatio(NORMAL_SPEED_RATIO);
            autonFunction.undropMarker();

            switch (i) {
                case 0:
                    knockLeftCrater();
                    break;
                case 1:
                    knockCenterCrater();
                    break;
                case 2:
                    knockRightCrater();
                    break;
            }

            steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

            sleep(MOVE_DELAY_MS);

            steering.move(reflectAngle(0));
            steering.finishSteering();
            sleep(convertDelay(1500));
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            // ORIG CCW
            steering.turnClockwise();
            steering.finishSteering();
            sleep(convertDelay(950));
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            steering.move(reflectAngle(0));
            steering.finishSteering();
            sleep(convertDelay(2000)); // 1500
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            steering.move(reflectAngle(180));
            steering.finishSteering();
            sleep(convertDelay(200));
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            steering.setSpeedRatio(FAST_SPEED_RATIO);

            steering.move(reflectAngle(270));
            steering.finishSteering();
            sleep(convertDelay(4800));
            steering.stopAllMotors();

            autonFunction.dropMarker();
            sleep(MARKER_DROP_DELAY_MS);

            steering.move(reflectAngle(90));
            steering.finishSteering();
            sleep(convertDelay(6700));
            steering.stopAllMotors();

            sleep(20_000);
        }

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    // ORIG LEFT
    public void knockRightCrater() {

        // Knock

        steering.move(reflectAngle(90));
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(reflectAngle(180));
        steering.finishSteering();
        sleep(1700);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(reflectAngle(90));
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(reflectAngle(270));
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Common right

        steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

        steering.move(reflectAngle(0));
        steering.finishSteering();
        sleep(convertDelay(3300)); // 3000
        steering.stopAllMotors();
    }

    // ORIG CENTER
    public void knockCenterCrater() {

        // Knock

        steering.move(reflectAngle(90));
        steering.finishSteering();
        sleep(2800);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(reflectAngle(270));
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Common right

        steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

        steering.move(reflectAngle(0));
        steering.finishSteering();
        sleep(convertDelay(1800)); // 1500
        steering.stopAllMotors();
    }

    // ORIG RIGHT
    public void knockLeftCrater() {

        // Knock

        steering.move(reflectAngle(90));
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(reflectAngle(0));
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(reflectAngle(90));
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(reflectAngle(270));
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        // Common right already attained
    }

    // Convert a delay from the intended speed ratio of 0.3 for a different speed ratio by multiplying
    public long convertDelay(long originalDelay) {
        return (long) (originalDelay * (NORMAL_SPEED_RATIO / steering.getSpeedRatio()));
    }

    // Reflect angle over the y-axis... hope it works
    public static double reflectAngle(double angle) {
        angle = angle % 360;
        angle = angle < 0 ? angle + 360 : angle;
        if (0 <= angle && angle < 180) {
            return 180 - angle;
        }
        else if (180 <= angle && angle <= 360) {
            return 540 - angle;
        }
        else {
            return angle;
        }
    }
}
