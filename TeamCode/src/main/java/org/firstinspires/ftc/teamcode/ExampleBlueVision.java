package org.firstinspires.ftc.teamcode;

import org.corningrobotics.enderbots.endercv.OpenCVPipeline;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ExampleBlueVision extends OpenCVPipeline {

    private boolean showContours = true;

    // Declare mats here to avoid re-instantiating on every call to processFrame
    private Mat hsv = new Mat();
    private Mat thresholdedWhite = new Mat();
    private Mat thresholdedYellow = new Mat();

    private List<MatOfPoint> whiteContours = new ArrayList<>(); // Here so we can expose it later through getContours
    private List<MatOfPoint> yellowContours = new ArrayList<>(); // Here so we can expose it later through getContours

    public synchronized List<MatOfPoint> getContours() {
        return whiteContours; // TODO
    }

    // Called every camera frame.
    @Override
    public Mat processFrame(Mat rgba, Mat gray) {

        // Change colorspace from RGBA to HSV
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 3);

        // --- Yellow detection section ---

        // Threshold to find yellow colors
        Core.inRange(hsv, new Scalar(10, 180, 110), new Scalar(30, 255, 255), thresholdedYellow);

        // Blur thresholded image
        Imgproc.blur(thresholdedYellow, thresholdedYellow, new Size(3, 3));

        // --- White detection section ---

        // Threshold to find white colors
        Core.inRange(hsv, new Scalar(0, 0, 200), new Scalar(179, 45, 255), thresholdedWhite);

        // Blur thresholded image
        Imgproc.blur(thresholdedWhite, thresholdedWhite, new Size(3, 3));

        whiteContours = new ArrayList<>();
        yellowContours = new ArrayList<>();

        Imgproc.findContours(thresholdedYellow, yellowContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(thresholdedWhite, whiteContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        Imgproc.drawContours(rgba, yellowContours, -1, new Scalar(0, 0, 255), 2, 15);
        Imgproc.drawContours(rgba, whiteContours, -1, new Scalar(0, 0, 0), 2, 15);

        return rgba; // display image seen by the camera

    }

}
