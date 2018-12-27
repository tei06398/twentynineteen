/*
Uses HSV color thresholding and edge detection to determine the position
of the cubes and balls within the camera frame.
*/

package org.firstinspires.ftc.teamcode;

import org.corningrobotics.enderbots.endercv.OpenCVPipeline;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class WhiteYellowDetector extends OpenCVPipeline {

    // Declare mats here to avoid re-instantiating on every call to processFrame
    private Mat hsv = new Mat();
    private Mat thresholdedWhite = new Mat();
    private Mat thresholdedYellow = new Mat();
    private Mat circles = new Mat();

    private List<MatOfPoint> whiteContours = new ArrayList<>();
    private List<MatOfPoint> yellowContours = new ArrayList<>();
    private List<MatOfPoint> goodYellowContours = new ArrayList<>();

    // Hough circles settings.
    private int minDist = 50;
    private int cannyUpperThreshold = 120;
    private int accumulator = 10;
    private int minRadius = 30;
    private int maxRadius = 80;

    private static final double[] y_bounds = {0.35, 0.65};
    private static int history = 1;

    // Final output
    private int position;

    // Display data on driver phone
    private Telemetry telemetry;
    private boolean showUI;

    WhiteYellowDetector(Telemetry telemetry, boolean showUI) {
        super();
        this.telemetry = telemetry;
        this.showUI = showUI;
    }

    // Copy contour list so client class doesn't throw errors if actual contour list gets updated
    public synchronized List<MatOfPoint> getContours() {
        List<MatOfPoint> whiteContoursCopy = new ArrayList<>();
        for (MatOfPoint contourMat : whiteContours) {
            MatOfPoint tempMat = new MatOfPoint();
            contourMat.copyTo(tempMat);
            whiteContoursCopy.add(tempMat);
        }
        return whiteContoursCopy;
    }

    // Called from the Auton/TeleOp every time we need output
    public int getPosition(){
        return position;
    }

    // Called every camera frame, used within the OCV pipeline
    @Override
    public Mat processFrame(Mat rgba, Mat gray) {

        telemetry.addLine("---Detection Algorithm Begins---");
        System.out.println("---Detection Algorithm Begins---");

        Size imageSize = rgba.size();
        double w = imageSize.width;
        double h = imageSize.height;

        // Clear contour lists
        yellowContours.clear();
        whiteContours.clear();
        goodYellowContours.clear();

        // Change colorspace from RGBA to HSV
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 3);

        // Blur thresholded image
        Imgproc.blur(hsv, hsv, new Size(10, 10));

        // White threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(0, 0, 185), new Scalar(179, 70, 255), thresholdedWhite);
        Imgproc.erode(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // Modify mask to fill holes (use Imgproc.RETR_EXTERNAL instead of Imgproc.RETR_LIST to find only external contours)
        Imgproc.findContours(thresholdedWhite, whiteContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.fillPoly(thresholdedWhite, whiteContours, new Scalar(255, 255, 255));

        // Yellow threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(10, 180, 200), new Scalar(35, 255, 255), thresholdedYellow); // Conservative
        // Core.inRange(hsv, new Scalar(0, 160, 180), new Scalar(40, 255, 255), thresholdedYellow); // Liberal
        Imgproc.erode(thresholdedYellow, thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedYellow, thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // Draw bounding lines on image
        if (showUI) {
            Imgproc.line(rgba, new Point(0, y_bounds[0] * h), new Point(w, y_bounds[0] * h), new Scalar(255, 255, 255));
            Imgproc.line(rgba, new Point(0, y_bounds[1] * h), new Point(w, y_bounds[1] * h), new Scalar(255, 255, 255));
        }

        // --- Cube Detection (Largest Contour Area) ---

        // If it exists, add largest yellow contour (only one) to list of good yellow contours
        Imgproc.findContours(thresholdedYellow, yellowContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Collections.sort(yellowContours, (c1, c2) -> Double.compare(Imgproc.contourArea(c1), Imgproc.contourArea(c2)));
        for (int i = 0; i < Math.min(1, yellowContours.size()); i++) {
            goodYellowContours.add(yellowContours.get(yellowContours.size() - 1 - i));
        }

        // Draw yellow contour on original image
        if (showUI) {
            Imgproc.drawContours(rgba, goodYellowContours, -1, new Scalar(255, 0, 0), 2, 15);
        }

        // TODO: Since there will only every be 0 or 1 good yellow contours, the averaging is unnecessary
        // Average of all yellow positions
        int yellowCenterAverage = 0;
        // If there are no good yellow contours, we guess that the yellow position is at the center of the screen
        int yellowCenterFinal = (int) w / 2;

        if (goodYellowContours.size() > 0) {
            for (MatOfPoint cont : goodYellowContours) {

                // Find mass center of contour
                Moments moments = Imgproc.moments(cont);
                int centerX = (int) (moments.get_m10() / moments.get_m00());
                int centerY = (int) (moments.get_m01() / moments.get_m00());

                // If contour center is within boundaries, add center to average and draw the center on the image
                if (y_bounds[0] * h <= centerY && y_bounds[1] * h >= centerY) {
                    yellowCenterAverage += centerX;
                    if (showUI) {
                        Point center = new Point(centerX, centerY);
                        Imgproc.circle(rgba, center, 3, new Scalar(255, 0, 255), 2);
                    }
                }

            }
            yellowCenterFinal = yellowCenterAverage / goodYellowContours.size();
        }

        // Write number of yellow contours on image
        if (showUI) {
            Imgproc.putText(rgba, "Yellow: " + goodYellowContours.size(), new Point(0, 30), 1, 2.5, new Scalar(255, 0, 255), 3);
        }

        telemetry.addData("Yellow", goodYellowContours.size());
        System.out.println("Yellow: " + goodYellowContours.size());

        // --- Ball Detection (Hough Circles) ---

        // Reset circle mat every loop (otherwise circles persist when the mat is not updated in a loop)
        circles = new Mat();

        // Create list of circles within specified parameters
        Imgproc.HoughCircles(thresholdedWhite, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minDist, cannyUpperThreshold, accumulator, minRadius, maxRadius);

        // Write number of circles on image
        if (showUI) {
            Imgproc.putText(rgba, "Circles: " + circles.cols(), new Point(0, 60), 1, 2.5, new Scalar(0, 255, 0), 3);
        }

        telemetry.addData("Circles", circles.cols());
        System.out.println("Circles: " + circles.cols());

        // List of circles, sorted by radius
        TreeMap<Integer, Point> sortedCircles = new TreeMap<>();

        if (circles.cols() > 0) {
            for (int x = 0; x < circles.cols(); x++) {

                double currentCircle[] = circles.get(0, x);

                if (currentCircle != null) {

                    // Get center point and radius of current circle
                    Point center = new Point(Math.round(currentCircle[0]), Math.round(currentCircle[1]));
                    int radius = (int) Math.round(currentCircle[2]);

                    // If center of current circle is within bounds, add to list of circles
                    if (y_bounds[0] * h <= center.y && y_bounds[1] * h >= center.y) {
                        sortedCircles.put(radius, center);
                    }

                    // Draw circle perimeter and center
                    if (showUI) {
                        Imgproc.circle(rgba, center, radius, new Scalar(0, 255, 0), 2);
                        Imgproc.circle(rgba, center, 3, new Scalar(0, 0, 255), 2);
                    }
                }
            }
        }

        // Sort the circles by radii
        Object[] radiiSorted = sortedCircles.descendingKeySet().toArray();

        // Result if no circles are present
        int result = -1;

        if (radiiSorted.length > 0) {

            // Circle of largest radius
            Point c0 = sortedCircles.get(radiiSorted[0]);

            // Draw perimeter of largest circle in white, slightly larger radius
            if (showUI) {
                Imgproc.circle(rgba, c0, (int) radiiSorted[0] + 10, new Scalar(0, 255, 255), 2);
            }

            if (radiiSorted.length > 1) {

                // Circle of second largest radius
                Point c1 = sortedCircles.get(radiiSorted[1]);

                // Draw perimeter of second largest circle in white, slightly larger radius
                if (showUI) {
                    Imgproc.circle(rgba, c1, (int) radiiSorted[1] + 10, new Scalar(0, 255, 255), 2);
                }

                // Determine object ordering; only happens if both circles present

                // "What on earth is happening?" version
                // result = yellowCenterFinal <= c0.x ? yellowCenterFinal > c1.x ? 1 : 0 : yellowCenterFinal < c1.x ? 1 : 2;

                // De-obfuscated version
                if (yellowCenterFinal <= c0.x) {
                    if (yellowCenterFinal > c1.x) {
                        result = 1;
                    }
                    else {
                        result = 0;
                    }
                }
                else {
                    if (yellowCenterFinal < c1.x) {
                        result = 1;
                    }
                    else {
                        result = 2;
                    }
                }
            }
        }

        // Draw number of circles within the y-bound
        if (showUI) {
            Imgproc.putText(rgba, "Y-bound: " + radiiSorted.length, new Point(0, 90), 1, 2.5, new Scalar(0, 255, 255), 3);
        }

        telemetry.addData("Y-Bound", radiiSorted.length);
        System.out.println("Y-Bound: " + radiiSorted.length);

        //--- Circle GUI ---

        // Draw white ordering visualization circles
        if (showUI) {
            Imgproc.circle(rgba, new Point(45, h - 45), 30, new Scalar(255, 255, 255), 30);
            Imgproc.circle(rgba, new Point(w / 2, h - 45), 30, new Scalar(255, 255, 255), 30);
            Imgproc.circle(rgba, new Point(w - 45, h - 45), 30, new Scalar(255, 255, 255), 30);
        }

        // Show ordering prediction
        telemetry.addData("Detector Result", result);
        System.out.println("Detector Result: " + result);

        // If no objects detected, keep the old result, else update history
        if (result == -1) {
            result = history;
        }
        else {
            history = result;
        }

        // Update current position
        position = result;

        // Draw the yellow ordering visualization circles
        if (showUI) {
            Imgproc.circle(rgba, new Point(result == 0 ? 45 : (result == 1 ? w / 2 : w - 45), h - 45), 30, new Scalar(255, 255, 0), 30);
        }

        telemetry.addLine("---Detection Algorithm Ends---");
        System.out.println("---Detection Algorithm Ends---");

        /*
        Telemetry will only work properly in a linear opmode, where this pipeline is the only thing updating telemetry
        (In loop-based opmodes, telemetry.update() is automatically called at the end of the loop)
        However, this prevents the opmode from providing any telemetry
        */
        telemetry.update();

        return rgba; // display image seen by the camera
    }
}
