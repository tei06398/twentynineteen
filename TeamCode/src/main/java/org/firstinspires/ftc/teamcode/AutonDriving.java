package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AutonDriving {

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private DcMotor lf;
    private DcMotor lb;
    private DcMotor rf;
    private DcMotor rb;

    public AutonDriving() {
        lf = hardwareMap.dcMotor.get("lfMotor");
        lb = hardwareMap.dcMotor.get("lbMotor");
        rf = hardwareMap.dcMotor.get("rfMotor");
        rb = hardwareMap.dcMotor.get("rbMotor");
        resetEncoders();
    }

    public void resetEncoders() {
        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setAllPowers(double power) {
        lf.setPower(power);
        lb.setPower(power);
        rf.setPower(power);
        rb.setPower(power);
    }

    public void setAllTargets(int target) {
        lf.setTargetPosition(target);
        lb.setTargetPosition(target);
        rf.setTargetPosition(target);
        rb.setTargetPosition(target);
    }

    public boolean isBusy() {
        return lf.isBusy() || lb.isBusy() || rf.isBusy() || rb.isBusy();
    }

    public void addTargetsForward(int distance) {
        lf.setTargetPosition(lf.getTargetPosition() + distance);
        lb.setTargetPosition(lb.getTargetPosition() + distance);
        rf.setTargetPosition(rf.getTargetPosition() - distance);
        rb.setTargetPosition(rf.getTargetPosition() - distance);
    }

    public void addTargetsBackwards(int distance) {
        lf.setTargetPosition(lf.getTargetPosition() - distance);
        lb.setTargetPosition(lb.getTargetPosition() - distance);
        rf.setTargetPosition(rf.getTargetPosition() + distance);
        rb.setTargetPosition(rf.getTargetPosition() + distance);
    }

    public void addTargetsRight(int distance) {
        lf.setTargetPosition(lf.getTargetPosition() - distance);
        lb.setTargetPosition(lb.getTargetPosition() + distance);
        rf.setTargetPosition(rf.getTargetPosition() - distance);
        rb.setTargetPosition(rf.getTargetPosition() + distance);
    }

    public void addTargetsLeft(int distance) {
        lf.setTargetPosition(lf.getTargetPosition() + distance);
        lb.setTargetPosition(lb.getTargetPosition() - distance);
        rf.setTargetPosition(rf.getTargetPosition() + distance);
        rb.setTargetPosition(rf.getTargetPosition() - distance);
    }

    public void writeTelemetry() {
        telemetry.addData("LF Target", lf.getTargetPosition());
        telemetry.addData("LB Target", lf.getTargetPosition());
        telemetry.addData("RF Target", lf.getTargetPosition());
        telemetry.addData("RB Target", lf.getTargetPosition());
        telemetry.addData("LF Current", lf.getCurrentPosition());
        telemetry.addData("LB Current", lf.getCurrentPosition());
        telemetry.addData("RF Current", lf.getCurrentPosition());
        telemetry.addData("RB Current", lf.getCurrentPosition());
    }
}
