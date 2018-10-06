/*
Testing class using HSV color thresholding and edge detection to determine the position
of the cubes and balls within the camera frame.
*/

package org.firstinspires.ftc.teamcode;

import org.corningrobotics.enderbots.endercv.OpenCVPipeline;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
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



        // --- White detection section ---

        // Threshold to find white colors
        Core.inRange(hsv, new Scalar(0, 0, 190), new Scalar(179, 60, 255), thresholdedWhite);

        Imgproc.erode(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);



        // --- Yellow detection section ---

        // Threshold to find yellow colors
        Core.inRange(hsv, new Scalar(10, 180, 110), new Scalar(30, 255, 255), thresholdedYellow);



        // --- Circle detection test #2 ---
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresholdedWhite, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = 0;

        float[] circleRadius = new float[1];
        Point circleCenter = new Point();

        Collections.sort(contours, (c1, c2) -> Double.compare(Imgproc.contourArea(c1), Imgproc.contourArea(c2)));

        /*
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            if (Imgproc.contourArea(currentContour) > maxArea) {
                Imgproc.minEnclosingCircle(new MatOfPoint2f(currentContour.toArray()), circleCenter, circleRadius);
            }
        }
        */


        // --- Find the white and yellow contours
        whiteContours = new ArrayList<>();
        yellowContours = new ArrayList<>();

        // Use RETR_EXTERNAL instead of RETR_LIST to find only external contours
        Imgproc.findContours(thresholdedWhite, whiteContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(thresholdedYellow, yellowContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        goodWhiteContours = new ArrayList<>();
        goodYellowContours = new ArrayList<>();

        /*
        // --- PolyDB test ---
        for (int i = 0; i < whiteContours.size(); i++) {

            MatOfPoint2f current = new MatOfPoint2f();
            MatOfPoint2f approx = new MatOfPoint2f();

            whiteContours.get(i).convertTo(current, CvType.CV_32F);

            Imgproc.approxPolyDP(current, approx, 0.01 * Imgproc.arcLength(current, true), true);

            double area = Imgproc.contourArea(current);

            int approxArrayLength = approx.toArray().length;

            if (approxArrayLength > 8 && approxArrayLength < 23 && area > 30)
                goodWhiteContours.add(whiteContours.get(i));

        }
        */

        /*
        // --- Circle detection test ---
        Mat circles = new Mat();
        Imgproc.HoughCircles(thresholdedWhite, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, thresholdedWhite.rows() / 8.0, 100, 100, 0, 1000000000);
        List<MatOfPoint> circleContours = new ArrayList<>();
        Imgproc.findContours(circles, circleContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        */

        /*
        // --- Contour filtering placeholder ---
        for (int i = 0; i < whiteContours.size(); i++) {
            goodWhiteContours.add(whiteContours.get(i));
        }

        for (int i = 0; i < yellowContours.size(); i++) {
            goodYellowContours.add(yellowContours.get(i));
        }

        Imgproc.drawContours(rgba, goodWhiteContours, -1, new Scalar(0, 0, 0), 2, 15);
        Imgproc.drawContours(rgba, goodYellowContours, -1, new Scalar(0, 0, 255), 2, 15);

        Imgproc.putText(rgba, "All: " + whiteContours.size(), new Point(20, 30), 1, 2.5, new Scalar(0, 255, 0), 3);
        Imgproc.putText(rgba, "Filter: " + goodWhiteContours.size(), new Point(25, 70), 1, 2.5, new Scalar(0, 255, 0), 3);
        */

        Imgproc.circle(thresholdedWhite, circleCenter, (int)circleRadius[0], new Scalar(255, 0, 0), 2);

        return thresholdedWhite; // display image seen by the camera

    }

}
