package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Locale;

@TeleOp(name="Example: Blue Vision Demo")
public class ExampleBlueVisionDemo extends OpMode {

    private ExampleBlueVision blueVision;

    @Override
    public void init() {
        blueVision = new ExampleBlueVision();
        blueVision.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); // Replace with ActivityViewDisplay.getInstance() for fullscreen
        blueVision.setShowCountours(true);
        blueVision.enable(); // start vision system
    }

    @Override
    public void loop() {

        List<MatOfPoint> contours = blueVision.getContours(); // Get list of contours from the vision system

        for (int i = 0; i < contours.size(); i++) {

            // Get bounding rectangle of single contour and find x/y center (could do mass center using Imgproc.moments)
            Rect boundingRect = Imgproc.boundingRect(contours.get(i));

            String caption = "contour" + Integer.toString(i);
            String value = String.format(Locale.getDefault(), "(%d, %d)", (boundingRect.x + boundingRect.width) / 2, (boundingRect.y + boundingRect.height) / 2);
            telemetry.addData(caption, value);

        }
    }

    public void stop() {
        blueVision.disable(); // stop vision system
    }
}
