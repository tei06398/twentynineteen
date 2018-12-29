package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Yet Another Test Auton")
public class YetAnotherTestAuton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;
    private AutonFunction autonFunction;

    private static final double SPEED_RATIO = 0.3;

    private static final double FAST_SPEED_RATIO = 0.7;

    private static final long RETREAT_MS = 1300;

    private static final long DELAY_MS = 100;

    private static final long MARKER_DROP_DELAY_MS = 800;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        autonFunction = new AutonFunction(hardwareMap, telemetry);

        autonFunction.undropMarker();

        while (!this.isStarted()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
        runtime.reset();

        // steering.setSpeedRatio(SPEED_RATIO);

        // ------------------------------------------------

        for (int i = 0; i < 3; i++) {

            steering.setSpeedRatio(SPEED_RATIO);
            autonFunction.undropMarker();

            switch (i) {
                case 0:
                    knockLeft();
                    break;
                case 1:
                    knockCenter();
                    break;
                case 2:
                    knockRight();
                    break;
            }

            sleep(DELAY_MS);

            steering.move(0);
            steering.finishSteering();
            sleep(1500);
            steering.stopAllMotors();

            sleep(DELAY_MS);

            steering.turnCounterclockwise();
            steering.finishSteering();
            sleep(950);
            steering.stopAllMotors();

            sleep(DELAY_MS);

            steering.move(0);
            steering.finishSteering();
            sleep(2000); // 1500
            steering.stopAllMotors();

            sleep(DELAY_MS);

            // --- option 1 ---
            /*
            steering.move(270);
            steering.finishSteering();
            sleep(6700);
            steering.stopAllMotors();

            sleep(DELAY_MS);

            steering.move(90);
            steering.finishSteering();
            sleep(7000);
            steering.stopAllMotors();
            */
            // --- option 2 ---

            steering.setSpeedRatio(FAST_SPEED_RATIO);

            steering.move(270);
            steering.finishSteering();
            sleep((long) ((SPEED_RATIO / steering.getSpeedRatio()) * 4800));
            steering.stopAllMotors();

            autonFunction.dropMarker();
            sleep(MARKER_DROP_DELAY_MS);

            steering.move(90);
            steering.finishSteering();
            sleep((long) ((SPEED_RATIO / steering.getSpeedRatio()) * 6700));
            steering.stopAllMotors();

            /*
            TODO: ~1-2 seconds before end of return to crater, stop and re-butt against the wall, and then
            TODO: move towards the crater, to insure we don't hit the farthest right mineral
             */

            sleep(20_000);
        }

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    public void knockCenter() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(2800);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        // Common right

        steering.move(0);
        steering.finishSteering();
        sleep(1800); // 1500
        steering.stopAllMotors();
    }

    public void knockRight() {
        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        steering.move(0);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        // (Common right already attained)
    }

    public void knockLeft() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        sleep(1700);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(DELAY_MS);

        // Common right

        steering.move(0);
        steering.finishSteering();
        sleep(3300); // 3000
        steering.stopAllMotors();
    }

    // Convert a delay from the intended speed ratio of 0.3 for a different speed ratio by multiplying
    public long convertDelay(long originalDelay) {
        return (long) (originalDelay * (SPEED_RATIO / steering.getSpeedRatio()));
    }
}
