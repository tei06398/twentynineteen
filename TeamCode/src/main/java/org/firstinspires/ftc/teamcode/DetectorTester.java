package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Locale;

@TeleOp(name="Rowechen's Detector Algorithm")
public class DetectorTester extends OpMode {

    private Detector rr2detector;

    @Override
    public void init() {
        rr2detector = new Detector();
        rr2detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); // Replace with ActivityViewDisplay.getInstance() for fullscreen
        rr2detector.enable(); // start vision system
    }

    @Override
    public void loop() {

        List<MatOfPoint> contours = rr2detector.getContours(); // Get list of contours from the vision system

        for (int i = 0; i < contours.size(); i++) {

            // Get bounding rectangle of single contour and find x/y center (could do mass center using Imgproc.moments)
            Rect boundingRect = Imgproc.boundingRect(contours.get(i));

            String caption = "contour" + Integer.toString(i);
            String value = String.format(Locale.getDefault(), "(%d, %d)", (boundingRect.x + boundingRect.width) / 2, (boundingRect.y + boundingRect.height) / 2);
            telemetry.addData(caption, value);

        }
    }

    public void stop() {
        rr2detector.disable(); // stop vision system
    }

}
