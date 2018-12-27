package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.corningrobotics.enderbots.endercv.CameraViewDisplay;

@Autonomous(name = "Detector Tester Auton")
public class DetectorTesterAuton extends LinearOpMode {

    private WhiteYellowDetector rr2detector;

    @Override
    public void runOpMode() {

        rr2detector = new WhiteYellowDetector(telemetry,true);
        rr2detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); // Replace with ActivityViewDisplay.getInstance() for fullscreen
        rr2detector.enable(); // start vision system

        while (!this.isStarted()) {}

        while (opModeIsActive()) {}
    }
}
