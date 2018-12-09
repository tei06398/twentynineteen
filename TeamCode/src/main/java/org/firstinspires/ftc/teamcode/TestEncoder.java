package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp(name="Encoder Tester", group="TeleOp OpMode")
public class TestEncoder extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private GunnerFunction gunnerFunction;
    private DriverFunction driverFunction;

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        gunnerFunction = new GunnerFunction(hardwareMap.dcMotor.get("armMotor"), hardwareMap.dcMotor.get("winchMotor"), new GunnerFunction.TwoStateServo(hardwareMap.servo.get("lockServo"), 1, 0), hardwareMap.servo.get("sweepServo"), hardwareMap.dcMotor.get("chainMotor"), hardwareMap.dcMotor.get("slideMotor"));
        driverFunction = new DriverFunction(hardwareMap, telemetry);

        telemetry.addData("Status:", "Initialized:");
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

        if (this.gamepad1.right_bumper) {
            if (gunnerFunction.isLocked()) {
                gunnerFunction.lock();
            } else {
                gunnerFunction.unlock();
            }
        }

        if (this.gamepad1.x) {
            if (gunnerFunction.isArmUp()) {
                gunnerFunction.armDown();
            } else {
                gunnerFunction.armUp();
            }
        }

        telemetry.addData("LB", driverFunction.getLbPosition());
        telemetry.addData("LF", driverFunction.getLfPosition());
        telemetry.addData("RB", driverFunction.getRbPosition());
        telemetry.addData("RF", driverFunction.getRfPosition());
        gunnerFunction.doTelemetry(telemetry);
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();

    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        driverFunction.resetAllEncoders();
        gunnerFunction.resetEncoders();
    }

}
