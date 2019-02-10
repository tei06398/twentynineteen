package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
@TeleOp(name="Latency Test Tele-Op", group="TeleOp OpMode")
public class LatencyTestTeleOp extends OpMode {

    private DcMotor rf;
    private DcMotor lf;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        rf = hardwareMap.dcMotor.get("rfMotor");
        lf = hardwareMap.dcMotor.get("lfMotor");

        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {}

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        telemetry.addData("Status", "Started");
        telemetry.update();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        if (this.gamepad1.right_stick_x > 0.1) {
            rf.setPower(0.5);
        }
        else {
            rf.setPower(0);
        }

        if (this.gamepad1.right_trigger > 0.5) {
            lf.setPower(0.5);
        }
        else {
            lf.setPower(0);
        }

    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {}
}
