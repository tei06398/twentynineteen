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
    private final TwoStateServo pusherServo;
    private final TwoStateServo lockServo;
    private final Telemetry telemetry;

    GunnerFunction(HardwareMap hardwareMap, Telemetry telemetry) {
        // Load the needed devices from the hardware map
        ServoController servoController = hardwareMap.servoController.get("Servo Controller 1");
        servoController.pwmDisable();

        // TODO Template Initializations for Actual Motors and Servos
        // TODO Put in actual positions in place of 0
        this.pusherServo = new TwoStateServo(hardwareMap.servo.get("pusherServo"), 0, 0, 0, true);
        this.lockServo = new TwoStateServo(hardwareMap.servo.get("lockServo"), 0, 0, 0, true);

        this.telemetry = telemetry;

    }

    public static class ArmController {
        private final int WINCH_SLACKED = 0;
        private final int ARM_UP = -313;
        private final int ARM_DOWN = 180;
        private boolean isLocked = false;
        private boolean isArmUp = false;
        private DcMotor armMotor;
        private DcMotor winchMotor;
        private TwoStateServo lockServo;

        public ArmController(DcMotor armMotor, DcMotor winchMotor, TwoStateServo lockServo) {
            this.armMotor = armMotor;
            this.winchMotor = winchMotor;
            this.lockServo = lockServo;
            resetEncoders();
        }

        public void resetEncoders() {
            armMotor.setMode(DcMotor.RunMode.RESET_ENCODERS);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            winchMotor.setMode(DcMotor.RunMode.RESET_ENCODERS);
            winchMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        public void unlock() {
            lockServo.active();
            isLocked = false;
        }

        public void lock() {
            lockServo.passive();
            isLocked = true;
        }

        public boolean isLanded() {
            return (armMotor.getCurrentPosition() >= ARM_UP - 5 && armMotor.getCurrentPosition() <= ARM_UP + 5);
        }

        public void slackWinch() {
            winchMotor.setTargetPosition(WINCH_SLACKED);
            armMotor.setPower(0.5);
        }

        public void armUp() {
            armMotor.setTargetPosition(ARM_UP);
            armMotor.setPower(0.25);
            isArmUp = true;
        }

        public void armDown() {
            armMotor.setTargetPosition(ARM_DOWN);
            armMotor.setPower(0.25);
            isArmUp = false;
        }

        public void armStart() {
            armMotor.setTargetPosition(0);
            armMotor.setPower(0.25);
            isArmUp = false;
        }

        public boolean isLocked() {
            return isLocked;
        }

        public boolean isArmUp() {
            return isArmUp;
        }

        public void doTelemetry(Telemetry telemetry) {
            telemetry.addData("Arm Motor", armMotor.getCurrentPosition());
            telemetry.addData("Winch Motor", winchMotor.getCurrentPosition());
        }
    }

    // TODO Add actual code within setPower() and increment() functions

    public void extendPusher() {
        pusherServo.active();
        telemetry.log().add("Open Pusher");
    }

    public void retractPusher() {
        pusherServo.passive();
        telemetry.log().add("Close Pusher");
    }

    public void extendPusherFully() {
        pusherServo.getServo().setPosition(0);
        pusherServo.getServo().setPosition(1);
    }

    public void extendPusherIncremental() {
        pusherServo.incrementTowardsActive();
        pusherServo.incrementTowardsActive();
        telemetry.log().add("Extend Pusher Incremental");
    }

    public void retractPusherIncremental() {
        pusherServo.incrementTowardsPassive();
        pusherServo.incrementTowardsPassive();
        telemetry.log().add("Retract Pusher Incremental");
    }

    public void stopPusher() {
        //pusherServo.getServo().setPosition();
    }

    public void extendServoLock() {
        lockServo.active();
        telemetry.log().add("Extend Locking Servo");
    }

    public void retractServoLock() {
        lockServo.passive();
        telemetry.log().add("Retract Locking Servo");
    }

     public void extendServoLockIncremental() {
        pusherServo.incrementTowardsActive();
        pusherServo.incrementTowardsActive();
        telemetry.log().add("Extend Locking Servo Incremental");
    }

     public void retractServoLockIncremental() {
        pusherServo.incrementTowardsPassive();
        pusherServo.incrementTowardsPassive();
        telemetry.log().add("Retract Locking Servo Incremental");
    }

    public void toggleServoLock() {
        lockServo.toggle();
        telemetry.log().add("Toggle Locking Servo");
    }

    public void stopServoLock() {
        //lockServo.getServo().setPosition();
    }

    public void reset() {
        //closePusher();
        pusherServo.passive();
        lockServo.passive();
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


    public static class TwoStateServo {
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
