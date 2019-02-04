package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Depot Knock Test Auton")
public class DepotKnockTestAuton extends LinearOpMode {

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
                    knockLeftDepot();
                    break;
                case 1:
                    knockCenterDepot();
                    break;
                case 2:
                    knockRightDepot();
                    break;
            }

            sleep(MOVE_DELAY_MS);

            // -----

            steering.moveDegrees(0);
            steering.finishSteering();
            sleep(1000);
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            autonFunction.dropMarker();
            sleep(MARKER_DROP_DELAY_MS);

            steering.moveDegrees(180);
            steering.finishSteering();
            sleep(500);
            steering.stopAllMotors();

            // -----

            steering.setSpeedRatio(FAST_SPEED_RATIO);

            steering.moveDegrees(90);
            steering.finishSteering();
            sleep(convertDelay(4000));
            steering.stopAllMotors();

            // ---

            steering.setSpeedRatio(NORMAL_SPEED_RATIO);

            steering.moveDegrees(0);
            steering.finishSteering();
            sleep(convertDelay(800));
            steering.stopAllMotors();

            steering.moveDegrees(180);
            steering.finishSteering();
            sleep(convertDelay(200));
            steering.stopAllMotors();

            /*
            steering.moveDegrees(180);
            steering.finishSteering();
            sleep(convertDelay(300));
            steering.stopAllMotors();
            */

            // ---

            steering.moveDegrees(90);
            steering.finishSteering();
            sleep(convertDelay(2500));
            steering.stopAllMotors();

            sleep(20_000);
        }

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    public void knockLeftDepot() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        sleep(1900);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Common Position

        steering.move(90);
        steering.finishSteering();
        sleep(1500); // 1000
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.turnCounterclockwise();
        steering.finishSteering();
        sleep(DEPOT_TURN_MS);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.moveDegrees(270);
        steering.finishSteering();
        sleep(1000);
        steering.stopAllMotors();
    }

    public void knockCenterDepot() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(2800);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Common position

        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.turnCounterclockwise();
        steering.finishSteering();
        sleep(DEPOT_TURN_MS);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.moveDegrees(0);
        steering.finishSteering();
        sleep(1100);
        steering.stopAllMotors();
    }

    public void knockRightDepot() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(0);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        // Common position

        steering.move(90);
        steering.finishSteering();
        sleep(1200);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.turnCounterclockwise();
        steering.finishSteering();
        sleep(DEPOT_TURN_MS);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.moveDegrees(0);
        steering.finishSteering();
        sleep(2500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.moveDegrees(90);
        steering.finishSteering();
        sleep(500);
        steering.stopAllMotors();
    }

    // Convert a delay from the intended speed ratio of 0.3 for a different speed ratio by multiplying
    public long convertDelay(long originalDelay) {
        return (long) (originalDelay * (NORMAL_SPEED_RATIO / steering.getSpeedRatio()));
    }

    public static double angleConvert(double angle) {
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
