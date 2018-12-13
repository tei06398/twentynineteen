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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Tmp Auton")
public class TmpAuton extends LinearOpMode {

    private DcMotor lf; // stands for left front
    private DcMotor lb; // stands for left back
    private DcMotor rf; // stands for right front
    private DcMotor rb; // stands for right back

    private static final double DRIVE_POWER = 0.3;

    private ElapsedTime runtime;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        runtime = new ElapsedTime();

        lf = hardwareMap.dcMotor.get("lfMotor");
        lb = hardwareMap.dcMotor.get("lbMotor");
        rf = hardwareMap.dcMotor.get("rfMotor");
        rb = hardwareMap.dcMotor.get("rbMotor");

        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.update();

        waitForStart();
        runtime.reset();

        lf.setPower(DRIVE_POWER);
        lb.setPower(DRIVE_POWER);
        rf.setPower(DRIVE_POWER);
        rb.setPower(DRIVE_POWER);

        lf.setTargetPosition(0);
        lb.setTargetPosition(0);
        rf.setTargetPosition(0);
        rb.setTargetPosition(0);

        sleep(1000);

        int targetPosition1 = 1500;

        lf.setTargetPosition(targetPosition1);
        lb.setTargetPosition(targetPosition1);
        rf.setTargetPosition(-1 * targetPosition1);
        rb.setTargetPosition(-1 * targetPosition1);

        while (lf.isBusy() || lb.isBusy() || rf.isBusy() || rb.isBusy()) {
            telemetry.addData("Motor status", "Busy");
        }
        telemetry.addData("Motor status", "GET HYPED!");

        // run until driver presses stop
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}
