package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Encoder Tester", group="TeleOp OpMode")
public class TestEncoder extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DriverFunction driverFunction;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

        driverFunction = new DriverFunction(hardwareMap, telemetry);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        runtime.reset();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP

    @Override
    public void loop() {

        if (this.gamepad1.right_trigger > 0.5) {
            driverFunction.resetAllEncoders();
        }

        telemetry.addData("LB", driverFunction.getLbPosition());
        telemetry.addData("LF", driverFunction.getLfPosition());
        telemetry.addData("RB", driverFunction.getRbPosition());
        telemetry.addData("RF", driverFunction.getRfPosition());
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();

    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        driverFunction.resetAllEncoders();
    }

}
