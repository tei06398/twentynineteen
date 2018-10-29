package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.DriverFunction.*;

@TeleOp(name="Encoder Tester", group="TeleOp OpMode")
public class TestEncoder extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {

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

        //DriverFunction driverFunction = new DriverFunction(hardwareMap, telemetry);

        //telemetry.addData("LB", driverFunction.getLbPosition());
        //telemetry.addData("LF", driverFunction.getLfPosition());
        //telemetry.addData("RB", driverFunction.getRbPosition());
        //telemetry.addData("RF", driverFunction.getRfPosition());
        GunnerFunction.ArmController armController = new GunnerFunction.ArmController(hardwareMap.dcMotor.get("armMotor"), hardwareMap.dcMotor.get("winchMotor"), new GunnerFunction.TwoStateServo(hardwareMap.servo.get("servo"), 0, 0));
        telemetry.addData("Runtime", runtime.toString());
        telemetry.update();

    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
    }

}
