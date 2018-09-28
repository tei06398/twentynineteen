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
        blueVision.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); // can replace with ActivityViewDisplay.getInstance() for fullscreen
        blueVision.setShowCountours(true);
        blueVision.enable(); // start the vision system
    }

    @Override
    public void loop() {

        List<MatOfPoint> contours = blueVision.getContours(); // get a list of contours from the vision system

        for (int i = 0; i < contours.size(); i++) {

            Rect boundingRect = Imgproc.boundingRect(contours.get(i)); // get the bounding rectangle of a single contour, we use it to get the x/y center - yes there's a mass center using Imgproc.moments but w/e

            telemetry.addData("contour" + Integer.toString(i), String.format(Locale.getDefault(), "(%d, %d)", (boundingRect.x + boundingRect.width) / 2, (boundingRect.y + boundingRect.height) / 2));
        }
    }

    public void stop() {
        blueVision.disable(); // stop the vision system
    }
}
