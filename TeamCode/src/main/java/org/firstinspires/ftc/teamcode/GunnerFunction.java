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

    // TODO Add actual code within setPower() and increment() functions
    public void upArm() {
        motorArm.setPower();
        telemetry.log().add("Raise Motor Arm");
    }

    public void downArm() {
        motorArm.setPower();
        telemetry.log().add("Lower Motor Arm");
    }

    public void stopArm() {
        motorArm.setPower();
        telemetry.log().add("Stop Motor Arm");
    }

    public void openGear() {
        servoGear.passive();
        telemetry.log().add("Open Gear");
    }

    public void closeGear() {
        servoGear.active();
        telemetry.log().add("Close Gear");
    }

    public void openGearFully() {
        servoGearLeft.getServo().setPosition(0);
        servoGearRight.getServo().setPosition(1);
    }

    public void openGearIncremental() {
        servoGearLeft.incrementTowardsPassive();
        servoGearRight.incrementTowardsPassive();
        telemetry.log().add("Open Gear Incremental");
    }

    public void closeGearIncremental() {
        servoGearLeft.incrementTowardsActive();
        servoGearRight.incrementTowardsActive();
        telemetry.log().add("Close Gear Incremental");
    }

    public void retractRelicSlide() {
        motorRelicSlide.setPower();
        telemetry.log().add("Expand Relic Slide");
    }

    public void extendRelicSlide() {
        motorRelicSlide.setPower();
        telemetry.log().add("Retract Relic Slide");
    }

    public void stopRelicSlide() {
        motorRelicSlide.setPower();
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

    public void extendAutonGear() {
        autonGear.active();
    }

    public void retractAutonGear() {
        autonGear.passive();
    }

    public void stopAutonGear() {
        autonGear.getServo().setPosition();
    }

    public void reset() {
        //closeGear();
        relicGrabber.passive();
        relicLifter.passive();
        servoJewelPusher.setPosition(GEAR_SERVO_STOPPED_POS);
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
