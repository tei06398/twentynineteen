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

        // White threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(0, 0, 190), new Scalar(179, 60, 255), thresholdedWhite);
        Imgproc.erode(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedWhite,  thresholdedWhite, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // Yellow threshold, erode, and dilate
        Core.inRange(hsv, new Scalar(10, 180, 110), new Scalar(30, 255, 255), thresholdedYellow);
        Imgproc.erode(thresholdedYellow,  thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedYellow,  thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // --- Circle detection test #2 ---
        List<MatOfPoint> whiteContoursExternal = new ArrayList<>();
        Imgproc.findContours(thresholdedWhite, whiteContoursExternal, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Collections.sort(whiteContoursExternal, (c1, c2) -> Double.compare(Imgproc.contourArea(c1), Imgproc.contourArea(c2)));

        List<MatOfPoint> goodContours = new ArrayList<>();

        int loops = Math.min(2, whiteContoursExternal.size());

        for (int i = 0; i < loops; i++) {
            // goodContours.add(contours.get(i));
            goodContours.add(whiteContoursExternal.get(whiteContoursExternal.size() - 1 - i));
        }

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

        // --- Circle detection test ---
        Mat circles = new Mat();
        Imgproc.HoughCircles(thresholdedWhite, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, thresholdedWhite.rows() / 8.0, 100, 100, 0, 1000000000);
        List<MatOfPoint> circleContours = new ArrayList<>();
        Imgproc.findContours(circles, circleContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // --- Contour filtering placeholder ---
        for (int i = 0; i < whiteContours.size(); i++) {
            goodWhiteContours.add(whiteContours.get(i));
        }

        for (int i = 0; i < yellowContours.size(); i++) {
            goodYellowContours.add(yellowContours.get(i));
        }
        */

        for (MatOfPoint cont : goodContours) {
            float[] circleRadius = new float[1];
            Point circleCenter = new Point();
            Imgproc.minEnclosingCircle(new MatOfPoint2f(cont.toArray()), circleCenter, circleRadius);
            Imgproc.circle(rgba, circleCenter, (int)circleRadius[0], new Scalar(255, 0, 0), 2);
        }

        return rgba; // display image seen by the camera

    }

}
