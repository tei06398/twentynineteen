package org.firstinspires.ftc.teamcode;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "RR2 Auton")
public class RR2Auton extends LinearOpMode {

    private ElapsedTime runtime;
    private DriverFunction driverFunction;
    private DriverFunction.Steering steering;
    private  AutonFunction autonFunction;

    public static final double LANDING_SPEED_RATIO = 0.3;
    public static final double LANDER_ESCAPE_SPEED_RATIO = 0.5;
    public static final double MAX_COAST_SECONDS = 6;
    private static SharedPreferences sharedPrefs;
    private static AutonPosition startPos;

    @Override
    public void runOpMode() {

        // --- Init ---
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        String position = sharedPrefs.getString("auton_start_position", "ERROR");
        if (position.equals("CRATER")) {
            startPos = AutonPosition.CRATER;
        } else if (position.equals("DEPOT")) {
            startPos = AutonPosition.DEPOT;
        } else {
            startPos = AutonPosition.NULL;
        }
        runtime = new ElapsedTime();
        driverFunction = new DriverFunction(hardwareMap, telemetry);
        steering = driverFunction.getSteering();
        autonFunction = new AutonFunction(hardwareMap, telemetry);

        telemetry.addData("Status", "Initialized");
        autonFunction.writeTelemetry();
        telemetry.update();

        driverFunction.resetAllEncoders();
        autonFunction.resetAllEncoders();

        autonFunction.lockServo();
        autonFunction.zeroPowerArm(); // We don't want to power the arm until we've retracted all the way

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        autonFunction.writeTelemetry();
        telemetry.update();

        // --- Wait for start ---
        waitForStart();
        runtime.reset();

        //  TODO: Grab an encoder position here, then add an empirical constant to obtain a reliable target position for arm up
        // TODO: Alternatively, test out the arm reset method in tele-op (ideally, we would have both)

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

        // Run drive motors to insure we are down
        //steering.moveDegrees(270, LANDING_SPEED_RATIO);
        //steering.finishSteering();
        //sleep(2000);
        steering.stopAllMotors();

        // Get the arm to hold its position
        autonFunction.powerArm();
        autonFunction.setArmTargetPosition(autonFunction.getArmPosition());

        sleep(500);

        // ----- ESCAPE FROM LANDER -----

        //Ram into Lander
        /*
        steering.move(270);
        steering.finishSteering();
        sleep(1500);
        */
        // Move away from Lander
        steering.move(90);
        steering.finishSteering();
        sleep(350);

        // Strafe out of Lander
        steering.move(180);
        steering.finishSteering();
        sleep(1500); //TODO: Maybe 2000?

        // Get uncaught from nut
        steering.move(0);
        steering.finishSteering();
        sleep(250);

//        steering.stopAllMotors();
//        sleep(5000); //TODO Remember to Comment this Out!!

        //Centrally align to Lander
        /*
        steering.move(315);
        steering.finishSteering();
        sleep(500);
        steering.stopAllMotors();
        */

        // ----- Perform other auton functions -----

        /*if (startPos == AutonPosition.DEPOT) {
            //Forward up to just before Depot, knocking off the center Mineral
            steering.move(90);
            steering.finishSteering();
            sleep(2000);
            //Turn to be Parallel to Depot
            steering.turnClockwise(0.5);
            steering.finishSteering();
            sleep(500);
            //Move deeper towards Depot
            steering.move(90);
            steering.finishSteering();
            sleep(500);
            //Drop Team Marker
            autonFunction.dropMarker();
            sleep(100);
            //Move shallower out of Depot
            steering.move(270);
            steering.finishSteering();
            sleep(500);
            //Move left along Depot towards Wall
            steering.move(0);
            steering.finishSteering();
            sleep(1500);
            //Move backwards towards Crater
            steering.move(270);
            steering.finishSteering();
            sleep(4000);
        } else if (startPos == AutonPosition.CRATER) {
            //Drive forward, knocking off the center Mineral and parking in the Crater
            steering.move(90);
            steering.finishSteering();
            sleep(1000);
            steering.stopAllMotors();
            steering.finishSteering();
        } else {
            telemetry.addData("HURR", "DURR");
        }
        // TODO: Replace all the driving stuff here with AutonDriving, if that seems like the right decision

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            autonFunction.writeTelemetry();
            telemetry.update();
        }*/
    }

    private enum AutonPosition {
        CRATER, DEPOT, NULL
    }
}
