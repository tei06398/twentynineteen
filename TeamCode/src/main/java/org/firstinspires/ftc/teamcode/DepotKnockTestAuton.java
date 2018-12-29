package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "DEPOT KNOCK AUTON")
public class DepotKnockTestAuton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;

    private static final double SPEED_RATIO = 0.3;
    private static final double FAST_SPEED_RATIO = 0.7;

    private static final long MOVE_DELAY_MS = 100;

    private static final long DEPOT_TURN_MS = 3000;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();

        while (!this.isStarted()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
        runtime.reset();

        for (int i = 0; i < 3; i++) {

            steering.setSpeedRatio(SPEED_RATIO);

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

            steering.moveDegrees(0);
            steering.finishSteering();
            sleep(1000);
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);

            steering.setSpeedRatio(FAST_SPEED_RATIO);

            steering.moveDegrees(90);
            steering.finishSteering();
            sleep((long) ((SPEED_RATIO / steering.getSpeedRatio()) * 6500));
            steering.stopAllMotors();

            sleep(MOVE_DELAY_MS);


            sleep(20_000);
        }

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    public void knockLeft() {

        // Knock

        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(MOVE_DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        sleep(1900); // TODO
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

    public void knockCenter() {

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

        sleep(MOVE_DELAY_MS);

    }

    public void knockRight() {

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
        return (long) (originalDelay * (SPEED_RATIO / steering.getSpeedRatio()));
    }
}
