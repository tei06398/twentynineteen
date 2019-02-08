package org.firstinspires.ftc.teamcode;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.corningrobotics.enderbots.endercv.CameraViewDisplay;

@Autonomous(name = "RR2 Auton")
public class RR2Auton extends LinearOpMode {

    private static SharedPreferences sharedPrefs;
    private static AutonPosition startPos;

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;
    private AutonFunction autonFunction;

    private Detector colorDetector;

    public static final double MAX_COAST_SECONDS = 6;

    public static final double NORMAL_SPEED_RATIO = 0.3;
    public static final double MEDIUM_SPEED_RATIO = 0.5;
    public static final double FAST_SPEED_RATIO = 0.7;

    public static final long MOVE_DELAY_MS = 50;
    public static final long LONG_DELAY_MS = 300;
    private static final long RETREAT_MS = 1000;
    private static final long DEPOT_TURN_MS = 3000;
    private static final long MARKER_DROP_DELAY_MS = 800;

    private static final int CV_ITERATIONS = 5;
    private static final long CV_LOOP_DELAY = 200;

    @Override
    public void runOpMode() {

        // --- Init ---

        // Initial telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Get shared preferences for starting position (crater/depot)
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        String position = sharedPrefs.getString("auton_start_position", "ERROR");
        if (position.equals("CRATER")) {
            startPos = AutonPosition.CRATER;
        }
        else if (position.equals("DEPOT")) {
            startPos = AutonPosition.DEPOT;
        }
        else {
            startPos = AutonPosition.NULL;
        }

        // Initialize utilities
        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        autonFunction = new AutonFunction(hardwareMap, telemetry);

        // To have this called ASAP
        autonFunction.undropMarker();

        // Initialize CV
        colorDetector = new Detector(telemetry, true);
        colorDetector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());

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
        // waitForStart();

        // --- Start ---

        // Potentially reset the encoders here

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

        // COLOR DETECTION

        colorDetector.enable(); // TODO: Find optimal time to call this
        sleep(1500); //NOTE: Time for Color Detector to Run

        int[] blockPositions = new int[CV_ITERATIONS];
        for (int i = 0; i < blockPositions.length; i++) {
            blockPositions[i] = colorDetector.getPosition();
            sleep(CV_LOOP_DELAY);
        }
        colorDetector.disable();
        int[] counts = new int[3];
        for (int blockPosition : blockPositions) {
            if (blockPosition >= 0 && blockPosition <= 2) {
                counts[blockPosition]++;
            }
        }
        MineralPosition mineralPosition;
        if (counts[0] > counts[1] && counts[0] > counts[2]) {
            mineralPosition = MineralPosition.LEFT;
        }
        else if (counts[1] > counts[0] && counts[1] > counts[2]) {
            mineralPosition = MineralPosition.CENTER;
        }
        else {
            mineralPosition = MineralPosition.RIGHT;
        }

        // BALL KNOCKING

        if (startPos == AutonPosition.CRATER) {

            if (mineralPosition == MineralPosition.LEFT) {
                knockLeftCrater();
            }
            else if (mineralPosition == MineralPosition.CENTER) {
                knockCenterCrater();
            }
            else {
                knockRightCrater();
            }

            // TODO: Comment this section to make it more readable

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

        }
        else if (startPos == AutonPosition.DEPOT) {

            if (mineralPosition == MineralPosition.LEFT) {
                knockLeftDepot();
            }
            else if (mineralPosition == MineralPosition.CENTER) {
                knockCenterDepot();
            }
            else {
                knockRightDepot();
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

        }
        else {
            telemetry.addLine("Error: no start position specified");
            telemetry.update();
        }

        // run until opmode ends
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

        // To attempt to prevent the auton from dropping the marker if we don't reach the depot
        autonFunction.undropMarker();
    }

    // --- Crater Side Knocking Methods ---

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
        sleep(RETREAT_MS);
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
        sleep(1500 + RETREAT_MS);
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
        sleep(RETREAT_MS);
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

    // --- Depot Side Knocking Methods ---

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

    private enum AutonPosition {
        CRATER, DEPOT, NULL
    }

    private enum MineralPosition {
        LEFT, CENTER, RIGHT
    }

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
