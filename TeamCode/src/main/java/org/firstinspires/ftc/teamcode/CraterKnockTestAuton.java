package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
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
        autonFunction.centerSweepServos();

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

            steering.move(180);
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

            steering.move(180);
            steering.finishSteering();
            sleep(convertDelay(2000)); // 1500
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            steering.move(0);
            steering.finishSteering();
            sleep(convertDelay(300));
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            steering.setSpeedRatio(FAST_SPEED_RATIO);

            steering.move(270);
            steering.finishSteering();
            sleep(convertDelay(4800));
            steering.stopAllMotors();

            autonFunction.dropMarker();
            sleep(MARKER_DROP_DELAY_MS);

            /*
            steering.move(reflectAngle(90));
            steering.finishSteering();
            sleep(convertDelay(6700));
            steering.stopAllMotors();
            */

            // TODO: Perform a re-align right about here...

            // -----

            steering.move(180);
            steering.finishSteering();
            sleep(convertDelay(1000));
            steering.stopAllMotors();

            steering.setSpeedRatio(NORMAL_SPEED_RATIO);

            steering.move(0);
            steering.finishSteering();
            sleep(convertDelay(500));
            steering.stopAllMotors();

            // -----

            steering.setSpeedRatio(FAST_SPEED_RATIO);

            steering.move(90);
            steering.finishSteering();
            sleep(convertDelay(4000));
            steering.stopAllMotors();

            // ---

            steering.move(180);
            steering.finishSteering();
            sleep(convertDelay(800));
            steering.stopAllMotors();

            steering.setSpeedRatio(NORMAL_SPEED_RATIO);

            // ---

            steering.move(90);
            steering.finishSteering();
            sleep(convertDelay(2400)); // 2700
            steering.stopAllMotors();

            sleep(20_000);
        }

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    public void knockLeftCrater() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        // Common right already attained
    }

    public void knockCenterCrater() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(2800);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Common right

        steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

        steering.move(180);
        steering.finishSteering();
        sleep(convertDelay(1800)); // 1500
        steering.stopAllMotors();
    }

    public void knockRightCrater() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(0);
        steering.finishSteering();
        sleep(1700);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Common right

        steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

        steering.move(180);
        steering.finishSteering();
        sleep(convertDelay(3300)); // 3000
        steering.stopAllMotors();
    }

    // Convert a delay from the intended speed ratio of 0.3 for a different speed ratio by multiplying
    public long convertDelay(long originalDelay) {
        return (long) (originalDelay * (NORMAL_SPEED_RATIO / steering.getSpeedRatio()));
    }
}
