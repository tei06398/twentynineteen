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

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class NavTargetDectector extends OpenCVPipeline{
    // Declare mats here to avoid re-instantiating on every call to processFrame
    private Mat hsv = new Mat();

    private Mat targetImg ;

    // Hough circles settings.
    private int minDist = 50;
    private int cannyUpperThreshold = 120;
    private int accumulator = 10;
    private int minRadius = 30;
    private int maxRadius = 80;

    private static final double[] y_bounds={0.35,0.65};
    private static int history= 1;

    private int Position; //Final Output

    //UI
    private Telemetry dTelemetry;

    private boolean showUI;

    NavTargetDectector(Telemetry tele,boolean ui, String fileloc){
        super();
        dTelemetry=tele;
        showUI=ui;
        targetImg = imread(fileloc);

    }

    /*public synchronized List<MatOfPoint> getContours() {
        // Copy the contour list so that the client class doesn't throw errors if the actual contour list gets updated
        List<MatOfPoint> whiteContoursCopy = new ArrayList<>();
        for (MatOfPoint contourMat : whiteContours) {
            MatOfPoint tempMat = new MatOfPoint();
            contourMat.copyTo(tempMat);
            whiteContoursCopy.add(tempMat);
        }
        return whiteContoursCopy;
    }*/
    //Idk what this does



    public int getPosition(){
        // Called every time we need output
        // Called from the Auton/TeleOp.
        return Position;
    }

    @Override
    public Mat processFrame(Mat rgba, Mat gray) {
        // Called every camera frame.
        // Used within the OCV pipeline.
        dTelemetry.addLine("---Detection Algorithm Begins---");
        System.out.println("---Detection Algorithm Begins---");
        Size this_size=rgba.size();
        double w=this_size.width;
        double h=this_size.height;

        // Clear contour lists
        //THING.clear()

        Imgproc.matchTemplate(targetImg,rgba,gray,Imgproc.TM_CCORR_NORMED);

        dTelemetry.addLine("---Detection Algorithm Ends---");
        System.out.println("---Detection Algorithm Ends---");

        return gray; // display image seen by the camera

    }

}

