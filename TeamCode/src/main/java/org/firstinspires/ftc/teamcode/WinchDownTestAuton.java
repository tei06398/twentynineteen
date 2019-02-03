package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Winch Down Test Auton")
public class WinchDownTestAuton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;
    private AutonFunction autonFunction;

    public static final double MAX_COAST_SECONDS = 6;

    public static final double NORMAL_SPEED_RATIO = 0.3;

    public static final long MOVE_DELAY_MS = 50;
    public static final long LONG_DELAY_MS = 300;

    @Override
    public void runOpMode() {

        // --- Init ---

        // Initial telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize utilities
        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        autonFunction = new AutonFunction(hardwareMap, telemetry);

        // To have this called ASAP
        autonFunction.undropMarker();
        autonFunction.centerSweepServos();

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

        runtime.reset();

        telemetry.addData("Status", "Started");
        autonFunction.writeTelemetry();
        telemetry.update();

        // LANDING

        AutonDriving.landingSequence(this, steering, autonFunction, runtime, telemetry);

        // run until opmode ends
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

        // To attempt to prevent the auton from dropping the marker if we don't reach the depot
        autonFunction.undropMarker();
    }
}
