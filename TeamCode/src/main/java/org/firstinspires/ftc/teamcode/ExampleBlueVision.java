/*
Testing class using HSV color thresholding and edge detection to determine the position
of the cubes and balls within the camera frame.
*/

package org.firstinspires.ftc.teamcode;

import org.corningrobotics.enderbots.endercv.OpenCVPipeline;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ExampleBlueVision extends OpenCVPipeline {

    // Declare mats here to avoid re-instantiating on every call to processFrame
    private Mat hsv = new Mat();
    private Mat thresholdedWhite = new Mat();
    private Mat thresholdedYellow = new Mat();

    private List<MatOfPoint> whiteContours = new ArrayList<>();
    private List<MatOfPoint> yellowContours = new ArrayList<>();

    private List<MatOfPoint> goodWhiteContours = new ArrayList<>();
    private List<MatOfPoint> goodYellowContours = new ArrayList<>();

    public synchronized List<MatOfPoint> getContours() {
        return whiteContours; // TODO
    }

    // Called every camera frame.
    @Override
    public Mat processFrame(Mat rgba, Mat gray) {

        // Change colorspace from RGBA to HSV
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 3);

        // Blur thresholded image
        Imgproc.blur(hsv, hsv, new Size(10, 10));

        // White threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(0, 0, 190), new Scalar(179, 60, 255), thresholdedWhite);
        Imgproc.erode(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // Yellow threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(10, 180, 110), new Scalar(30, 255, 255), thresholdedYellow);
        Imgproc.erode(thresholdedYellow,  thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedYellow,  thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        /*
        // Use RETR_EXTERNAL instead of RETR_LIST to find only external contours
        Imgproc.findContours(thresholdedWhite, whiteContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(thresholdedYellow, yellowContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        */


        // --- Find Hough Circles ---

        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY);
        Mat circles = new Mat();

        int CannyUpperThreshold = 120;
        int Accumulator = 10;

        int minRadius = 15;
        int maxRadius = 30;

        Imgproc.HoughCircles(thresholdedWhite, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minRadius, CannyUpperThreshold, Accumulator, minRadius, maxRadius);
        System.out.println(circles);

        Imgproc.putText(rgba, "Circles: " + circles.size(), new Point(20, 30), 1, 2.5, new Scalar(0, 255, 0), 3);

        if (circles.cols() > 0) {
            for (int x = 0; x < circles.cols(); x++) {
                double currentCircle[] = circles.get(0, x);

                if (currentCircle != null) {
                    Point center = new Point(Math.round(currentCircle[0]), Math.round(currentCircle[1]));
                    int radius = (int) Math.round(currentCircle[2]);

                    // Draw circle perimeter
                    Imgproc.circle(rgba, center, radius, new Scalar(0, 255, 0), 2);
                    // Draw small circle to indicate center
                    Imgproc.circle(rgba, center, 2, new Scalar(0, 0, 255), 2);
                }
            }
        }

        return rgba; // display image seen by the camera

    }

}
