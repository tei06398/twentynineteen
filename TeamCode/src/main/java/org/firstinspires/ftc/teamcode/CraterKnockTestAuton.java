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
                    AutonDriving.knockLeftCrater(this, steering);
                    break;
                case 1:
                    AutonDriving.knockCenterCrater(this, steering);
                    break;
                case 2:
                    AutonDriving.knockRightCrater(this, steering);
                    break;
            }

            AutonDriving.craterSequence(this, steering, autonFunction);

            sleep(20_000);
        }

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}
