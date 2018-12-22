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

        knockRight();

        sleep(500);

        steering.move(270);
        steering.finishSteering();
        sleep(1000);
        steering.stopAllMotors();

        // ------------------------------------------------

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    public void knockCenter() {
        steering.move(90);
        steering.finishSteering();
        sleep(2800);
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
    }

    public void knockLeft() {

    }
}
