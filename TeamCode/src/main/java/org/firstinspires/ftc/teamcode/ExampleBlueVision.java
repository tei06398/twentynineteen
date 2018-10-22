/*
Testing class using HSV color thresholding and edge detection to determine the position
of the cubes and balls within the camera frame.
*/

package org.firstinspires.ftc.teamcode;

import org.corningrobotics.enderbots.endercv.OpenCVPipeline;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

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

    private List<MatOfPoint> goodYellowContours = new ArrayList<>();

    private Point yellowCenter = null;

    private Mat circles = new Mat();

    public synchronized List<MatOfPoint> getContours() {
        List<MatOfPoint> whiteContoursCopy = new ArrayList<>();
        for (MatOfPoint contourMat : whiteContours) {
            MatOfPoint tempMat = new MatOfPoint();
            contourMat.copyTo(tempMat);
            whiteContoursCopy.add(tempMat);
        }
        return whiteContoursCopy;
    }

    public synchronized int getPosition() {
        ArrayList<double[]> sortableCircles = new ArrayList<>();
        for (int i = 0; i < circles.cols(); i++) {
            double[] currentCircle = circles.get(0, i); // [0] is x, [1] is y, [2] is radius
            sortableCircles.add(currentCircle);
        }
        Collections.sort(sortableCircles, (c1, c2) -> Double.compare(c1[2], c2[2]));

        if (sortableCircles.size() < 2) {
            return 10;
        }
        else if (yellowCenter == null) {
            return 9;
        }
        else {
            double yellowX = yellowCenter.x;
            double c1X = sortableCircles.get(sortableCircles.size() - 1)[0]; // [0] is x
            double c2X = sortableCircles.get(sortableCircles.size() - 2)[0]; // [0] is x

            if (yellowX < c1X && yellowX < c2X) {
                return -1;
            }
            else if ( (yellowX < c1X && yellowX > c2X) || (yellowX > c1X && yellowX < c2X) ) {
                return 0;
            }
            else {
                return 1;
            }

        }
    }

    public synchronized double[] test() {
        ArrayList<double[]> sortableCircles = new ArrayList<>();
        for (int i = 0; i < circles.cols(); i++) {
            double[] currentCircle = circles.get(0, i); // [0] is x, [1] is y, [2] is radius
            sortableCircles.add(currentCircle);
        }
        Collections.sort(sortableCircles, (c1, c2) -> Double.compare(c1[2], c2[2]));

        if (sortableCircles.size() < 2) {
            return new double[]{20, 20, 20};
        }
        else if (yellowCenter == null) {
            return new double[]{30, 30, 30};
        }
        else {

            double[] toReturn = new double[3];

            toReturn[0] = yellowCenter.x;
            toReturn[1] = sortableCircles.get(sortableCircles.size() - 1)[0];
            toReturn[2] = sortableCircles.get(sortableCircles.size() - 2)[0];

            return toReturn;
        }
    }

    // Called every camera frame.
    @Override
    public Mat processFrame(Mat rgba, Mat gray) {

        // --- Clear contour lists ---

        yellowContours.clear();
        whiteContours.clear();
        goodYellowContours.clear();

        // --- Preliminary filtering ---

        // Change colorspace from RGBA to HSV
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 3);

        // Blur thresholded image
        Imgproc.blur(hsv, hsv, new Size(10, 10));

        // White threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(0, 0, 185), new Scalar(179, 70, 255), thresholdedWhite);
        Imgproc.erode(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // Modify mask to fill holes
        // Use Imgproc.RETR_EXTERNAL instead of Imgproc.RETR_LIST to find only external contours
        Imgproc.findContours(thresholdedWhite, whiteContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.fillPoly(thresholdedWhite, whiteContours, new Scalar(255, 255, 255));

        // Yellow threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(10, 180, 200), new Scalar(35, 255, 255), thresholdedYellow); // Conservative
        // Core.inRange(hsv, new Scalar(0, 160, 180), new Scalar(40, 255, 255), thresholdedYellow); // Liberal
        Imgproc.erode(thresholdedYellow, thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedYellow, thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // --- Block Detection ---

        // TODO: Search for 4-6 sided polygon (a cube viewed from different angles could have between 4 and 6 sides)
        Imgproc.findContours(thresholdedYellow, yellowContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Collections.sort(yellowContours, (c1, c2) -> Double.compare(Imgproc.contourArea(c1), Imgproc.contourArea(c2)));
        for (int i = 0; i < Math.min(1, yellowContours.size()); i++) {
            goodYellowContours.add(yellowContours.get(yellowContours.size() - 1 - i));
        }
        Imgproc.drawContours(rgba, goodYellowContours, -1, new Scalar(255, 0, 0), 2, 15); // Draw the largest yellow contour
        if (goodYellowContours.size() > 0) {
            for (MatOfPoint cont : goodYellowContours) {
                Moments moments = Imgproc.moments(cont);
                int centerX = (int) (moments.get_m10() / moments.get_m00());
                int centerY = (int) (moments.get_m01() / moments.get_m00());
                yellowCenter = new Point(centerX, centerY);
                // TODO: Consider whether the point needs to be reset to null if this loop does not run
                Imgproc.circle(rgba, yellowCenter, 3, new Scalar(255, 0, 255), 2); // Draw a circle at center of contour
            }
        }
        else {
            yellowCenter = null;
        }

        // --- Wiffle Ball Detection ---

        int minDist = 50;
        int cannyUpperThreshold = 120;
        int accumulator = 10;
        int minRadius = 30;
        int maxRadius = 80;

        // Mat circles = new Mat();
        circles = new Mat(); // IMPORTANT!

        Imgproc.HoughCircles(thresholdedWhite, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minDist, cannyUpperThreshold, accumulator, minRadius, maxRadius);

        if (circles.cols() > 0) {
            for (int x = 0; x < circles.cols(); x++) {
                double[] currentCircle = circles.get(0, x);
                if (currentCircle != null) {
                    Point center = new Point(Math.round(currentCircle[0]), Math.round(currentCircle[1]));
                    int radius = (int) Math.round(currentCircle[2]);
                    Imgproc.circle(rgba, center, radius, new Scalar(0, 255, 0), 2); // Draw circle perimeter
                    Imgproc.circle(rgba, center, 3, new Scalar(0, 0, 255), 2); // Draw small circle to indicate center
                }
            }
        }

        // --- Add image overlays ---

        Imgproc.putText(rgba, "Circles: " + circles.cols(), new Point(20, 30), 1, 2.5, new Scalar(0, 255, 0), 3);

        // --- Return image ---

        return rgba;

    }


}
