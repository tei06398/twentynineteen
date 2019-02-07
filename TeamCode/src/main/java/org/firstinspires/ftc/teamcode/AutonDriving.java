package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import static org.firstinspires.ftc.teamcode.RR2Auton.*;

public class AutonDriving {

    public static void knockLeftCrater(LinearOpMode auton, DriverFunction.Steering steering) {

        // Knock

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        auton.sleep(1500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1300);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        auton.sleep(RETREAT_MS);
        steering.stopAllMotors();

        // Common right already attained
    }
    
    public static void knockCenterCrater(LinearOpMode auton, DriverFunction.Steering steering) {

        // Knock

        steering.move(90);
        steering.finishSteering();
        auton.sleep(2800);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        auton.sleep(RETREAT_MS);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Common right

        steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

        steering.move(180);
        steering.finishSteering();
        auton.sleep(convertDelay(1800, steering)); // 1500
        steering.stopAllMotors();
    }

    public static void knockRightCrater(LinearOpMode auton, DriverFunction.Steering steering) {

        // Knock

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(0);
        steering.finishSteering();
        auton.sleep(1700);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1300);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Retreat

        steering.move(270);
        steering.finishSteering();
        auton.sleep(RETREAT_MS);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Common right

        steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

        steering.move(180);
        steering.finishSteering();
        auton.sleep(convertDelay(3300, steering)); // 3000
        steering.stopAllMotors();
    }

    public static void knockLeftDepot(LinearOpMode auton, DriverFunction.Steering steering) {

        // Knock

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        auton.sleep(1900);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1300);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Common Position

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1500); // 1000
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.turnCounterclockwise();
        steering.finishSteering();
        auton.sleep(DEPOT_TURN_MS);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.moveDegrees(270);
        steering.finishSteering();
        auton.sleep(1000);
        steering.stopAllMotors();
    }

    public static void knockCenterDepot(LinearOpMode auton, DriverFunction.Steering steering) {

        // Knock

        steering.move(90);
        steering.finishSteering();
        auton.sleep(2800);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Common position

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.turnCounterclockwise();
        steering.finishSteering();
        auton.sleep(DEPOT_TURN_MS);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.moveDegrees(0);
        steering.finishSteering();
        auton.sleep(1100);
        steering.stopAllMotors();
    }

    public static void knockRightDepot(LinearOpMode auton, DriverFunction.Steering steering) {

        // Knock

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(0);
        steering.finishSteering();
        auton.sleep(1500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1300);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // Common position

        steering.move(90);
        steering.finishSteering();
        auton.sleep(1200);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.turnCounterclockwise();
        steering.finishSteering();
        auton.sleep(DEPOT_TURN_MS);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.moveDegrees(0);
        steering.finishSteering();
        auton.sleep(2500);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.moveDegrees(90);
        steering.finishSteering();
        auton.sleep(500);
        steering.stopAllMotors();
    }

    public static void craterSequence(LinearOpMode auton, DriverFunction.Steering steering, AutonFunction autonFunction) {

        steering.setSpeedRatio(MEDIUM_SPEED_RATIO);

        auton.sleep(MOVE_DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(1500, steering));
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        // ORIG CCW
        steering.turnClockwise();
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(950, steering));
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(180);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(2000, steering)); // 1500
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.move(0);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(300, steering));
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        steering.setSpeedRatio(FAST_SPEED_RATIO);

        steering.move(270);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(4800, steering));
        steering.stopAllMotors();

        autonFunction.dropMarker();
        auton.sleep(MARKER_DROP_DELAY_MS);

        /*
        steering.move(reflectAngle(90));
        steering.finishSteering();
        sleep(AutonDriving.convertDelay(6700, steering));
        steering.stopAllMotors();
        */

        // TODO: Perform a re-align right about here...

        steering.move(90);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(4000, steering));
        steering.stopAllMotors();

        // -------

        steering.move(180);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(800, steering));
        steering.stopAllMotors();

        steering.setSpeedRatio(NORMAL_SPEED_RATIO);

        // -------

        steering.move(90);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(2400, steering)); // 2700
        steering.stopAllMotors();
    }

    public static void depotSequence(LinearOpMode auton, DriverFunction.Steering steering, AutonFunction autonFunction) {

        auton.sleep(MOVE_DELAY_MS);

        steering.moveDegrees(0);
        steering.finishSteering();
        auton.sleep(1000);
        steering.stopAllMotors();

        auton.sleep(MOVE_DELAY_MS);

        autonFunction.dropMarker();
        auton.sleep(MARKER_DROP_DELAY_MS);

        steering.setSpeedRatio(FAST_SPEED_RATIO);

        steering.moveDegrees(90);
        steering.finishSteering();
        auton.sleep(AutonDriving.convertDelay(6500, steering));
        steering.stopAllMotors();
    }

    // Convert a delay from the intended speed ratio of 0.3 for a different speed ratio by multiplying
    public static long convertDelay(long originalDelay, DriverFunction.Steering steering) {
        return (long) (originalDelay * (NORMAL_SPEED_RATIO / steering.getSpeedRatio()));
    }
}
