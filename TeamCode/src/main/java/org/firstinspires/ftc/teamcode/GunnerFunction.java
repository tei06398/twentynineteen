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
        this.lockServo = new TwoStateServo(hardwareMap.servo.get("lockServo"), .35, 1, 0, true);

        this.telemetry = telemetry;

    }

    public static class ArmController {
        private final int WINCH_SLACKED = 0;
        private final int ARM_UP = -313;
        private final int ARM_DOWN = 180;

        private boolean isLocked = false;
        public boolean isStart = true;
        private DcMotor armMotor;
        private DcMotor winchMotor;
        private DcMotor chainMotor;
        private DcMotor slideMotor;
        private Servo sweepServo;
        private TwoStateServo lockServo;

        private int slide = 0;


        public ArmController(DcMotor armMotor, DcMotor winchMotor, TwoStateServo lockServo, Servo sweepServo, DcMotor chainMotor, DcMotor slideMotor) {
            this.armMotor = armMotor;
            this.winchMotor = winchMotor;
            this.lockServo = lockServo;
            this.sweepServo = sweepServo;
            this.chainMotor = chainMotor;
            this.slideMotor = slideMotor;
            resetEncoders();
        }

        public void resetEncoders() {
            armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            winchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            winchMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            chainMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            chainMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        public void unlock() {
            lockServo.active();
            isLocked = false;
        }

        public void lock() {
            lockServo.passive();
            isLocked = true;
        }

        // public boolean isArmUp() {
        //     return (armMotor.getCurrentPosition() >= ARM_UP - 5 && armMotor.getCurrentPosition() <= ARM_UP + 5);
        // }

        public void slackWinch() {
            winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            winchMotor.setPower(0);
        }

        public void brakeWinch() {
            winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            winchMotor.setPower(0);
        }

        public void armUp() {
            armMotor.setTargetPosition(ARM_UP);
            armMotor.setPower(0.15);
            // isArmUp = true;
        }

        public void armDown() {
            armMotor.setTargetPosition(ARM_DOWN);
            armMotor.setPower(0.15);
            // isArmUp = false;
        }

        public void armReset() {
            armMotor.setTargetPosition(0);
            armMotor.setPower(0.15);
        }

        public void slackArm() {
            armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            armMotor.setPower(0);
        }

        public void runChainMotor() {
            chainMotor.setPower(.15);
        }

        public void stopChainMotor() {
            chainMotor.setPower(0);
        }

        public void incrementSlideMotor() {
            slideMotor.setTargetPosition(slide+=15);
        }

        public void decrementSlideMotor() {
            if (slide - 15 >= 0) {
                slideMotor.setTargetPosition(slide -= 15);
            } else {
                slideMotor.setTargetPosition(0);
            }
        }

        public void doTelemetry(Telemetry telemetry) {
            telemetry.addData("Arm Motor", armMotor.getCurrentPosition());
            telemetry.addData("Winch Motor", winchMotor.getCurrentPosition());
            telemetry.addData("Slide Motor", slideMotor.getCurrentPosition());
            telemetry.addData("isArmUp", isArmUp());
            // telemetry.addData("lockServo Value", lockServo.getServo().getPosition());
        }

        public boolean isLocked() {
            return isLocked;
        }

        public boolean isArmUp() {
            return (armMotor.getCurrentPosition() >= ARM_UP - 5 && armMotor.getCurrentPosition() <= ARM_UP + 5);
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
        //TODO: Revise the Jeffrey Code to make this method possible
    }

    public void reset() {
        //closePusher();
        pusherServo.passive();
        lockServo.passive(); //TODO: Replace this with something that uses the ArmController Class
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
