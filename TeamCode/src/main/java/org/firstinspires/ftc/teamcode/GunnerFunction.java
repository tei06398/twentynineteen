package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * A utility class that controls all the gunner functions (opening and closing the glyphter, rotating the glypter, etc).
 */
public class GunnerFunction {
    // TODO Temporary, current # of motors and servos as of 10/15/18
    private final DcMotor motorArm;
    private final Servo servoGear;
    private final Telemetry telemetry;

    GunnerFunction(HardwareMap hardwareMap, Telemetry telemetry) {
        // Load the needed devices from the hardware map
        ServoController servoController = hardwareMap.servoController.get("Servo Controller 1");
        servoController.pwmDisable();

        // TODO Template Initializations for Actual Motors and Servos
        this.motorArm = hardwareMap.dcMotor.get("armMotor");
        this.servoGear = hardwareMap.servo.get("servoGear");
        servoGear.setPosition(GEAR_SERVO_STOPPED_POS);

        this.telemetry = telemetry;

    }

    // TODO Add actual code within setPower() functions
    public void upArm() {
        motorArm.setPower();
        telemetry.log().add("Raise Glyphter Arm");
    }

    public void downArm() {
        motorArm.setPower();
        telemetry.log().add("Lower Glyphter Arm");
    }

    public void stopArm() {
        motorArm.setPower();
        telemetry.log().add("Stop Glyphter Arm");
    }

    public void openGlyphter() {
        servoGlyphterLeft.passive();
        servoGlyphterRight.passive();
        telemetry.log().add("Open Glyphter");
    }

    public void closeGlyphter() {
        servoGlyphterLeft.active();
        servoGlyphterRight.active();
        telemetry.log().add("Close Glyphter");
    }

    public void openGlyphterFully() {
        servoGlyphterLeft.getServo().setPosition(0);
        servoGlyphterRight.getServo().setPosition(1);
    }

    public void openGlyphterIncremental() {
        servoGlyphterLeft.incrementTowardsPassive();
        servoGlyphterRight.incrementTowardsPassive();
        telemetry.log().add("Open Glyphter Incremental");
    }

    public void closeGlyphterIncremental() {
        servoGlyphterLeft.incrementTowardsActive();
        servoGlyphterRight.incrementTowardsActive();
        telemetry.log().add("Close Glyphter Incremental");
    }

    public void extendJewelPusher() { servoJewelPusher.setPosition(JEWELPUSHER_SERVO_EXTEND_POS);telemetry.log().add("Extend Jewel Pusher"); }

    public void retractJewelPusher() { servoJewelPusher.setPosition(JEWELPUSHER_SERVO_RETRACT_POS);telemetry.log().add("Retract Jewel Pusher"); }

    public void stopJewelPusher() { servoJewelPusher.setPosition(JEWELPUSHER_SERVO_STOPPED_POS);telemetry.log().add("Stop Jewel Pusher");}

    public void retractRelicSlide() {
        motorRelicSlide.setPower(0.6);
        telemetry.log().add("Expand Relic Slide");
    }

    public void extendRelicSlide() {
        motorRelicSlide.setPower(-0.6);
        telemetry.log().add("Retract Relic Slide");
    }

    public void stopRelicSlide() {
        motorRelicSlide.setPower(0);
        telemetry.log().add("Stop Relic Slide");
    }

    public void toggleRelicGrabber() {
        relicGrabber.toggle();
        telemetry.log().add("Toggle Relic Grabber");
    }

    public void openRelicGrabberIncremental() {
        relicGrabber.incrementTowardsActive();
        telemetry.log().add("Open Relic Grabber Incremental");
    }

    public void releaseRelic() {
        relicGrabber.incrementTowardsActive();
        if (relicLifter.getServo().getPosition() > 0.7) {
            relicLifter.incrementTowardsPassive();
        }
        telemetry.log().add("Release Relic");
    }

    public void grabRelic() {
        relicGrabber.incrementTowardsPassive();
        motorRelicSlide.setPower(-0.3);
        telemetry.log().add("Grab Relic");
    }

    public void closeRelicGrabberIncremental() {
        relicGrabber.incrementTowardsPassive();
        telemetry.log().add("Close Relic Grabber Incremental");
    }

    public void toggleRelicLifter() {
        relicLifter.toggle();
        telemetry.log().add("Toggle Relic Lifter");
    }

    public void raiseRelicLifter() {
        relicLifter.passive();
    }

    public void lowerRelicLifter() {
        relicLifter.active();
    }

    public void extendAutonGlyphter() {
        autonGlyphter.active();
    }

    public void retractAutonGlyphter() {
        autonGlyphter.passive();
    }

    public void stopAutonGlyphter() {
        autonGlyphter.getServo().setPosition(0.5);
    }

    public void reset() {
        //closeGlyphter();
        relicGrabber.passive();
        relicLifter.passive();
        servoJewelPusher.setPosition(JEWELPUSHER_SERVO_STOPPED_POS);
        telemetry.log().add("Reset");
    }

    public void disablePwm(HardwareMap hardwareMap) {
        ServoController servoController = hardwareMap.servoController.get("Servo Controller 1");
        servoController.pwmDisable();
    }

    public void enablePwm(HardwareMap hardwareMap) {
        ServoController servoController = hardwareMap.servoController.get("Servo Controller 1");
        servoController.pwmEnable();
    }
}
