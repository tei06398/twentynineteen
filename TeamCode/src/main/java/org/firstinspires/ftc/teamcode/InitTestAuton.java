package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Init Test Auton")
public class InitTestAuton extends LinearOpMode {

    private ElapsedTime runtime;

    @Override
    public void runOpMode() {

        // --- Init ---

        // Initial telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        runtime = new ElapsedTime();

        while (!this.isStarted()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

        // --- Start ---

        runtime.reset();

        telemetry.addData("Status", "Started");
        telemetry.update();

        sleep(180_000);

        telemetry.addData("Status", "3 Minutes Elapsed");
        telemetry.update();

        sleep(180_000);

        telemetry.addData("Status", "6 Minutes Elapsed");
        telemetry.update();

        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

    }
}
