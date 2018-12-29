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

public class Detector extends OpenCVPipeline {

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
    private int minRadius = 10;
    private int maxRadius = 80;
    
    private static final double[] y_bounds={0.35,0.65};
    private static int history= 1;

    private int Position; //Final Output

    //UI
    private Telemetry dTelemetry;

    private boolean showUI;

    Detector(Telemetry tele,boolean ui){
        super();
        dTelemetry=tele;
        showUI=ui;
    }

    public synchronized List<MatOfPoint> getContours() {
        // Copy the contour list so that the client class doesn't throw errors if the actual contour list gets updated
        List<MatOfPoint> whiteContoursCopy = new ArrayList<>();
        for (MatOfPoint contourMat : whiteContours) {
            MatOfPoint tempMat = new MatOfPoint();
            contourMat.copyTo(tempMat);
            whiteContoursCopy.add(tempMat);
        }
        return whiteContoursCopy;
    }
    public int getPosition(){
        // Called every time we need output
        // Called from the Auton/TeleOp.
        return Position;
    }
    // Called every camera frame.
    // Used within the OCV pipeline.
    @Override
    public Mat processFrame(Mat rgba, Mat gray) {
        dTelemetry.addLine("---Detection Algorithm Begins---");
        System.out.println("---Detection Algorithm Begins---");
        Size this_size=rgba.size();
        double w=this_size.width;
        double h=this_size.height;

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

        // Modify mask to fill holes
        // Use Imgproc.RETR_EXTERNAL instead of Imgproc.RETR_LIST to find only external contours
        Imgproc.findContours(thresholdedWhite, whiteContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.fillPoly(thresholdedWhite, whiteContours, new Scalar(255, 255, 255));

        // Yellow threshold, erode, and dilate
        //Core.inRange(hsv, new Scalar(10, 180, 200), new Scalar(35, 255, 255), thresholdedYellow); // Conservative
        Core.inRange(hsv, new Scalar(0, 160, 160), new Scalar(40, 255, 255), thresholdedYellow); // Liberal
        Imgproc.erode(thresholdedYellow, thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);
        Imgproc.dilate(thresholdedYellow, thresholdedYellow, new Mat(), new Point(-1, -1), 5, Core.BORDER_CONSTANT);

        // Preliminary block location algorithm
        Imgproc.findContours(thresholdedYellow, yellowContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Collections.sort(yellowContours, (c1, c2) -> Double.compare(Imgproc.contourArea(c1), Imgproc.contourArea(c2)));
        for (int i = 0; i < yellowContours.size(); i++) {
            Moments moments = Imgproc.moments(yellowContours.get(yellowContours.size() - 1 - i));
            //int centerX = (int) (moments.get_m10() / moments.get_m00());
            int centerY = (int) (moments.get_m01() / moments.get_m00());
            if(y_bounds[0]*h<=centerY&&y_bounds[1]*h>=centerY) {
                goodYellowContours.add(yellowContours.get(yellowContours.size() - 1 - i));
            }
        }

        if(goodYellowContours.size()>0) {
            goodYellowContours = goodYellowContours.subList(0, 1); //gets largest contour within y bounds
        }

        if(showUI) {
            Imgproc.drawContours(rgba, goodYellowContours, -1, new Scalar(255, 0, 0), 2, 15);
        }
        int avgtmp=0;// avg tmp variable
        int yellow_final= (int)w/2;
        int yellow_count = 0;
        if (goodYellowContours.size() > 0) {
            for (MatOfPoint cont : goodYellowContours) {
                Moments moments = Imgproc.moments(cont);
                int centerX = (int) (moments.get_m10() / moments.get_m00());
                int centerY = (int) (moments.get_m01() / moments.get_m00());
                if(y_bounds[0]*h<=centerY&&y_bounds[1]*h>=centerY) {
                    yellow_count++;
                    Point center = new Point(centerX, centerY);

                    avgtmp+=centerX;
                    Imgproc.circle(rgba, center, 3, new Scalar(255, 0, 255), 2);
                }

            }
            if (yellow_count > 0) {
                yellow_final = avgtmp / yellow_count;
            }
        }
        if(yellow_count == 0){
            yellow_final=Integer.MAX_VALUE;
        }
        if(showUI) {
            Imgproc.putText(rgba, "Yellow: " + goodYellowContours.size(), new Point(0, 30), 1, 2.5, new Scalar(255, 0, 255), 3);
        }
        dTelemetry.addData("Yellow", goodYellowContours.size());
        System.out.println("Yellow"+goodYellowContours.size());

        // --- Hough Circles ---

        circles = new Mat();
        Imgproc.HoughCircles(thresholdedWhite, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minDist, cannyUpperThreshold, accumulator, minRadius, maxRadius);
        if(showUI) {
            Imgproc.putText(rgba, "Circles: " + circles.cols(), new Point(0, 60), 1, 2.5, new Scalar(0, 255, 0), 3);
        }
        dTelemetry.addData("Circles", circles.cols());
        //System.out.println("Circles"+goodYellowContours.size());

        TreeMap<Integer, Point> sorted_circles= new TreeMap<>();//Treemaps are basically python lists but worse.
        int[] tmp=new int[2];
        if(showUI) {
            Imgproc.line(rgba, new Point(0, y_bounds[0] * h), new Point(w, y_bounds[0] * h), new Scalar(255, 255, 255));
            Imgproc.line(rgba, new Point(0, y_bounds[1] * h), new Point(w, y_bounds[1] * h), new Scalar(255, 255, 255));
        }
        if (circles.cols() > 0) {
            for (int x = 0; x < circles.cols(); x++) {
                double currentCircle[] = circles.get(0, x);
                if (currentCircle != null) {
                    Point center = new Point(Math.round(currentCircle[0]), Math.round(currentCircle[1]));
                    int radius = (int) Math.round(currentCircle[2]);
                    if(y_bounds[0]*h<=center.y&&y_bounds[1]*h>=center.y) {
                        sorted_circles.put(radius, center);
                    }
                    // Draw circle perimeter
                    Imgproc.circle(rgba, center, radius, new Scalar(0, 255, 0), 2);
                    // Draw small circle to indicate center
                    Imgproc.circle(rgba, center, 3, new Scalar(0, 0, 255), 2);
                }
            }
        }
        Object[] radii_sorted= sorted_circles.descendingKeySet().toArray(); //Sort the circles by radii

        int result=-1;//none
        if(radii_sorted.length > 0) {
            Point c0=sorted_circles.get(radii_sorted[0]);
            if(showUI) {
                Imgproc.circle(rgba, c0, (int) radii_sorted[0] + 10, new Scalar(0, 255, 255), 2);
            }
            if (radii_sorted.length > 1) {
                Point c1=sorted_circles.get(radii_sorted[1]);
                if(showUI) {
                    Imgproc.circle(rgba, c1, (int) radii_sorted[1] + 10, new Scalar(0, 255, 255), 2);
                }
                result=yellow_final<=c0.x?yellow_final>c1.x?1:0:yellow_final<c1.x?1:2; //Ordering.
            }else{
                if(yellow_final!=Integer.MAX_VALUE){
                    result=yellow_final<=c0.x?0:1; //Ordering.
                }
            }
        }
        if(showUI) {
            Imgproc.putText(rgba, "Y-bound: " + radii_sorted.length, new Point(0, 90), 1, 2.5, new Scalar(0, 255, 255), 3);
        }

        dTelemetry.addData("Y-Bound", radii_sorted.length);
        System.out.println("Y-Bound"+radii_sorted.length);

        //---CIRCLE GUI---
        if(showUI) {
            Imgproc.circle(rgba, new Point(45, h - 45), 30, new Scalar(255, 255, 255), 30);
            Imgproc.circle(rgba, new Point(w / 2, h - 45), 30, new Scalar(255, 255, 255), 30);
            Imgproc.circle(rgba, new Point(w - 45, h - 45), 30, new Scalar(255, 255, 255), 30);
        }
        dTelemetry.addData("Detector Result",result);
        System.out.println("Detector Result"+result);

        if(result==-1) {result = history;}else{history=result;}//History-result swap.

        Position=result;//After history. May change.

        if(showUI) {
            Imgproc.circle(rgba, new Point(result == 0 ? 45 : result == 1 ? w / 2 : w - 45, h - 45), 30, new Scalar(255, 255, 0), 30);
        }
        dTelemetry.addLine("---Detection Algorithm Ends---");
        System.out.println("---Detection Algorithm Ends---");

        return rgba; // display image seen by the camera

    }

}
