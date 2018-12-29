package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;

@TeleOp(name="Detector Tester TeleOp")
public class DetectorTesterTeleOp extends OpMode {

    private Detector rr2detector;

    @Override
    public void init() {
        rr2detector = new Detector(telemetry,true);
        rr2detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); // Replace with ActivityViewDisplay.getInstance() for fullscreen
        rr2detector.enable(); // start vision system
    }

    @Override
    public void loop() {
    }

    public void stop() {
        rr2detector.disable(); // stop vision system
    }
}
