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
    private Mat thresholded = new Mat();

    private List<MatOfPoint> contours = new ArrayList<>(); // Here so we can expose it later through getContours

    public synchronized void setShowCountours(boolean enabled) {
        showContours = enabled;
    }

    public synchronized List<MatOfPoint> getContours() {
        return contours;
    }

    // Called every camera frame.
    @Override
    public Mat processFrame(Mat rgba, Mat gray) {

        // Change colorspace from RGBA to HSV
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 3);

        // Threshold hsv image to b/w binary image where white is where there is blue within in specified range of values
        Core.inRange(hsv, new Scalar(0, 0, 200), new Scalar(179, 45, 255), thresholded);

        // Blur thresholded image to remove noise, other blur types include box or gaussian
        Imgproc.blur(thresholded, thresholded, new Size(3, 3));

        // List to hold contours, w/ single contour for outline of every blue object - could iterate over them to find objects of interest
        // Imgproc module has functions to analyze individual contours by their area, avg position, etc.
        contours = new ArrayList<>();

        // this function fills our contours variable with the outlines of blue objects we found
        Imgproc.findContours(thresholded, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // display binary threshold on screen - draws green outlines of blue contours over original image
        if (showContours) {
            Imgproc.drawContours(rgba, contours, -1, new Scalar(0, 255, 0), 2, 15); // Comment if testing polydp
        }

        // return thresholded; // For better visualization
        return rgba; // display image seen by the camera
    }
}
