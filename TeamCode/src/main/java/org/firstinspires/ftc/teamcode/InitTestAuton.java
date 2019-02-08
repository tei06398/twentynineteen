package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
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

        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

    }
}
