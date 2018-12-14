/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

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


    // The arm has to move a long way for us to be confident it has retracted
    public static final int ARM_RETRACT_SUCCESS_THRESHOLD = 100;

    @Override
    public void runOpMode() {

        // --- Init ---
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

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        autonFunction.writeTelemetry();
        telemetry.update();

        // --- Wait for start ---
        waitForStart();
        runtime.reset();

        autonFunction.unlockServo();

        // Coast winch until winch position reaches a relatively steady state
        while (opModeIsActive() && !autonFunction.winchCoastFinished()) {
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
        steering.moveDegrees(270, LANDING_SPEED_RATIO);
        steering.finishSteering();
        sleep(2000);
        steering.stopAllMotors();

        int armPosition = autonFunction.getArmPosition(); // Get initial value in case arm falls after escape attempt
        boolean armRetractSuccess = false;

        while (opModeIsActive() && !armRetractSuccess) {
            attemptLanderEscape();
            armRetractSuccess = attemptRetractArm(armPosition); // Attempt to retract arm
        }

        // Move away from the lander
        /*
        steering.moveDegrees(270, LANDING_SPEED_RATIO);
        steering.finishSteering();
        sleep(2000);
        steering.stopAllMotors();
        */



        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            autonFunction.writeTelemetry();
            telemetry.update();
        }
    }

    // Return true if arm retract was successful
    public boolean attemptRetractArm(int startPosition) {
        /*
        So that the arm doesn't move if it is not in fact released. If we used RUN_USING_ENCODERS,
        the I-term would quickly increase, and the arm might move, even if we were still attached
        to the lander.
         */
        autonFunction.setArmRunWithoutEncoders();

        int currentPosition = autonFunction.getArmPosition();

        if (Math.abs(currentPosition - startPosition) > ARM_RETRACT_SUCCESS_THRESHOLD) {
            autonFunction.setArmDefaultRunmode();
            return true;
        }

        autonFunction.runArm();
        sleep(1000);
        autonFunction.stopArm();
        sleep(500);

        currentPosition = autonFunction.getArmPosition();

        return Math.abs(currentPosition - startPosition) > ARM_RETRACT_SUCCESS_THRESHOLD;
    }

    // Move to the side - try to escape from lander
    public void attemptLanderEscape() {
        steering.turnClockwise(LANDER_ESCAPE_SPEED_RATIO);
        steering.finishSteering();
        sleep(2000);
        steering.stopAllMotors();

        driverFunction.rf.applyPower(-0.5);
        driverFunction.rb.applyPower(-0.5);
        sleep(2000);
        steering.stopAllMotors();
    }
}
