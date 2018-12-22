package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Yet Another Test Auton")
public class YetAnotherTestAuton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;

    private static final double SPEED_RATIO = 0.3;

    private static final long RETREAT_MS = 1300;

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

        steering.setSpeedRatio(SPEED_RATIO);

        // ------------------------------------------------

        for (int i = 0; i < 3; i++) {
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

            sleep(500);

            steering.turnCounterclockwise();
            steering.finishSteering();
            sleep(500);
            steering.stopAllMotors();

            sleep(500);

            steering.move(0);
            steering.finishSteering();
            sleep(3000);
            steering.stopAllMotors();

            sleep(500);

            steering.move(270);
            steering.finishSteering();
            sleep(7000);
            steering.stopAllMotors();

            sleep(500);

            steering.move(90);
            steering.finishSteering();
            sleep(7500);
            steering.stopAllMotors();

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

        sleep(500);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(500);

        // Common right

        steering.move(0);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();
    }

    public void knockRight() {
        steering.move(90);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(500);

        steering.move(0);
        steering.finishSteering();
        sleep(1500);
        steering.stopAllMotors();

        sleep(500);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(500);

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

        sleep(500);

        steering.move(180);
        steering.finishSteering();
        sleep(1700);
        steering.stopAllMotors();

        sleep(500);

        steering.move(90);
        steering.finishSteering();
        sleep(1300);
        steering.stopAllMotors();

        sleep(500);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        sleep(RETREAT_MS);
        steering.stopAllMotors();

        sleep(500);

        // Common right

        steering.move(0);
        steering.finishSteering();
        sleep(3000);
        steering.stopAllMotors();
    }
}
