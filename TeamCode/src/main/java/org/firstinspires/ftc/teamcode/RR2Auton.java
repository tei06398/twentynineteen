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

    /*
    public static final double MAX_COAST_SECONDS = 6;

    public static final double NORMAL_SPEED_RATIO = 0.3;
    public static final double MEDIUM_SPEED_RATIO = 0.5;
    public static final double FAST_SPEED_RATIO = 0.7;

    public static final long MOVE_DELAY_MS = 50;
    public static final long LONG_DELAY_MS = 300;
    private static final long RETREAT_MS = 1300;
    private static final long DEPOT_TURN_MS = 3000;
    private static final long MARKER_DROP_DELAY_MS = 800;
    */

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

        // --- Start ---

        // Potentially reset the encoders here

        runtime.reset();

        telemetry.addData("Status", "Started");
        autonFunction.writeTelemetry();
        telemetry.update();

        AutonDriving.landingSequence(this, steering, autonFunction, runtime, telemetry);

        // COLOR DETECTION

        colorDetector.enable(); // TODO: Find optimal time to call this
        sleep(500);

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
                AutonDriving.knockLeftCrater(this, steering);
            }
            else if (mineralPosition == MineralPosition.CENTER) {
                AutonDriving.knockCenterCrater(this, steering);
            }
            else {
                AutonDriving.knockRightCrater(this, steering);
            }

            AutonDriving.craterSequence(this, steering, autonFunction);
        }
        else if (startPos == AutonPosition.DEPOT) {

            if (mineralPosition == MineralPosition.LEFT) {
                AutonDriving.knockLeftDepot(this, steering);
            }
            else if (mineralPosition == MineralPosition.CENTER) {
                AutonDriving.knockCenterDepot(this, steering);
            }
            else {
                AutonDriving.knockRightDepot(this, steering);
            }

            AutonDriving.depotSequence(this, steering, autonFunction);
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

    private enum AutonPosition {
        CRATER, DEPOT, NULL
    }

    private enum MineralPosition {
        LEFT, CENTER, RIGHT
    }
}
