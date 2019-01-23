package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.corningrobotics.enderbots.endercv.CameraViewDisplay;

@Autonomous(name = "Detector Tester Auton")
public class DetectorTesterAuton extends LinearOpMode {

    private Detector rr2detector;

    @Override
    public void runOpMode() {

        rr2detector = new Detector(telemetry,true);
        rr2detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());

        while (!this.isStarted()) {}

        sleep(180_000);

        // rr2detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        rr2detector.enable(); // start vision system

        sleep(180_000);

        rr2detector.disable();

        while (opModeIsActive()) {}

    }
}
