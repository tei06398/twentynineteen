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
    private final TwoStateServo servoPusher;
    private final TwoStateServo servoLock;
    private final Telemetry telemetry;

    GunnerFunction(HardwareMap hardwareMap, Telemetry telemetry) {
        // Load the needed devices from the hardware map
        ServoController servoController = hardwareMap.servoController.get("Servo Controller 1");
        servoController.pwmDisable();

        // TODO Template Initializations for Actual Motors and Servos
        this.motorArm = hardwareMap.dcMotor.get("armMotor");
        // TODO Put in actual positions in place of X
        this.servoPusher = new TwoStateServo(hardwareMap.servo.get("servoPusher"), x, x, x, true);
        this.servoLock = new TwoStateServo(hardwareMap.servo.get("servoLock"), X, X, X, true);

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

    public void extendPusher() {
        servoPusher.active();
        telemetry.log().add("Open Pusher");
    }

    public void retractPusher() {
        servoPusher.passive();
        telemetry.log().add("Close Pusher");
    }

    public void extendPusherFully() {
        servoPusher.getServo().setPosition(0);
        servoPusher.getServo().setPosition(1);
    }

    public void extendPusherIncremental() {
        servoPusher.incrementTowardsActive();
        servoPusher.incrementTowardsActive();
        telemetry.log().add("Extend Pusher Incremental");
    }

    public void retractPusherIncremental() {
        servoPusher.incrementTowardsPassive();
        servoPusher.incrementTowardsPassive();
        telemetry.log().add("Retract Pusher Incremental");
    }

    public void stopPusher() {
        servoPusher.getServo().setPosition();
    }

    public void extendServoLock() {
        servoLock.active();
        telemetry.log().add("Extend Locking Servo");
    }

    public void retractServoLock() {
        servoLock.passive();
        telemetry.log().add("Retract Locking Servo");
    }

     public void extendServoLockIncremental() {
        servoPusher.incrementTowardsActive();
        servoPusher.incrementTowardsActive();
        telemetry.log().add("Extend Locking Servo Incremental");
    }

     public void retractServoLockIncremental() {
        servoPusher.incrementTowardsPassive();
        servoPusher.incrementTowardsPassive();
        telemetry.log().add("Retract Locking Servo Incremental");
    }

    public void toggleServoLock() {
        servoLock.toggle();
        telemetry.log().add("Toggle Locking Servo");
    }

    public void stopServoLock() {
        servoLock.getServo().setPosition();
    }

    public void reset() {
        //closePusher();
        servoPusher.passive();
        servoLock.passive();
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


    private class TwoStateServo {
        private Servo servo;
        private double passivePosition;
        private double activePosition;

        private boolean isActive;
        private double incrementalSpeed;

        public TwoStateServo(Servo servo, double passivePosition, double activePosition) {
            this(servo, passivePosition, activePosition, 1, false);
        }

        public TwoStateServo(Servo servo, double passivePosition, double activePosition, double incrementalSpeed) {
            this(servo, passivePosition, activePosition, incrementalSpeed, false);
        }

        public TwoStateServo(Servo servo, double passivePosition, double activePosition, double incrementalSpeed, boolean startAsActive) {
            this.servo = servo;
            this.passivePosition = passivePosition;
            this.activePosition = activePosition;
            this.incrementalSpeed = incrementalSpeed;
            isActive = startAsActive;
            updatePosition();
        }

        public void passive() {
            updatePosition(false);
        }

        public void active() {
            updatePosition(true);
        }

        public void toggle() {
            isActive = !isActive;
            updatePosition();
        }

        private void updatePosition() {
            servo.setPosition(isActive ? activePosition : passivePosition);
        }

        private void updatePosition(boolean isActive) {
            this.isActive = isActive;
            updatePosition();
        }

        public void incrementTowardsPassive() {
            incrementTowards(passivePosition);
        }

        public void incrementTowardsActive() {
            incrementTowards(activePosition);
        }

        private void incrementTowards(double desiredValue) {
            double servoPos = servo.getPosition();
            if (servoPos > desiredValue) {
                servo.setPosition(RobotUtil.clipRange(passivePosition, activePosition, servoPos - incrementalSpeed));
            } else if (servoPos < desiredValue){
                servo.setPosition(RobotUtil.clipRange(passivePosition, activePosition, servoPos + incrementalSpeed));
            }
        }

        public Servo getServo() {
            return servo;
        }
    }

    public static class RobotUtil {
        public static double clipRange(double min, double max, double value) {
            // Fix swapped ordering
            if (min > max) {
                double oldMax = max;
                max = min;
                min = oldMax;
            }
            return Math.min(max, Math.max(value, min));
        }
    }
}
