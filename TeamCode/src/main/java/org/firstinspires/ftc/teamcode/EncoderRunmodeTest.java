package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.*;

@TeleOp(name="EncoderRunmodeTest", group="TeleOp OpMode")
public class EncoderRunmodeTest extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor testMotor;
    private boolean leftToggleLock = false;
    private boolean rightToggleLock = false;

    private DcMotor.RunMode[] runmodes = {
        DcMotor.RunMode.RUN_USING_ENCODER,
        DcMotor.RunMode.RUN_WITHOUT_ENCODER,
        DcMotor.RunMode.RUN_USING_ENCODERS,
        DcMotor.RunMode.RUN_WITHOUT_ENCODERS
    };

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        this.testMotor = this.hardwareMap.dcMotor.get("armMotor");

        testMotor.setMode(DcMotor.RunMode.RESET_ENCODERS);
        testMotor.setMode(runmodes[0]);

        // testMotor.setPower(0);
        // testMotor.setTargetPosition(0);

    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {}

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        runtime.reset();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        // Right Trigger: Cycle through runmodes
        if (this.gamepad1.right_trigger > 0.1 && !leftToggleLock) {
            leftToggleLock = true;

            int position = 0;
            for (int i = 0; i < runmodes.length; i++) {
                if (runmodes[i] == testMotor.getMode()) {
                    position = i;
                }
            }
            int newPosition = (position + 1) % 4;
            testMotor.setMode(runmodes[newPosition]);

        }
        else {
            leftToggleLock = false;
        }

        // Left Trigger: Reset encoder
        if (this.gamepad1.left_trigger > 0.1 && !rightToggleLock) {
            rightToggleLock = true;
            DcMotor.RunMode currentMode = testMotor.getMode();
            // testMotor.setMode(DcMotor.RunMode.RESET_ENCODERS);
            testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            testMotor.setMode(currentMode);
        }
        else {
            rightToggleLock = false;
        }

        telemetry.addData("Motor Runmode", testMotor.getMode());
        telemetry.addData("Motor Position", testMotor.getCurrentPosition());
        telemetry.addData("Motor Power", testMotor.getPower());
        telemetry.addData("OpMode Runtime", runtime.toString());
        telemetry.update();

        // TODO: Test zero power behavior with DCMotor.getZeroPowerBehavior()
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {}

}
