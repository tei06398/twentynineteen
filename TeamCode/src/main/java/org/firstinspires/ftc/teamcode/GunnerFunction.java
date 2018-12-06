package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoController;

/**
 * A utility class that controls all gunner functions (arm, ball sweeper, etc.)
 */
public class GunnerFunction {

    /*
    GunnerFunction(HardwareMap hardwareMap, Telemetry telemetry) {
        ServoController servoController = hardwareMap.servoController.get("Servo Controller 1");
        servoController.pwmDisable();
    }
    */

    private final int WINCH_SLACKED = 0;
    private final int ARM_UP = -313;
    private final int ARM_DOWN = 180;

    private boolean isLocked = false;
    private DcMotor armMotor;
    private DcMotor winchMotor;
    private DcMotor chainMotor;
    private DcMotor slideMotor;
    private Servo sweepServo;
    private TwoStateServo lockServo;

    public GunnerFunction(DcMotor armMotor, DcMotor winchMotor, TwoStateServo lockServo, Servo sweepServo, DcMotor chainMotor, DcMotor slideMotor) {

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

    /*
    public boolean isArmUp() {
        return (armMotor.getCurrentPosition() >= ARM_UP - 5 && armMotor.getCurrentPosition() <= ARM_UP + 5);
    }
    */

    public void slackWinch() {
        winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        winchMotor.setPower(0);
    }

    public void armUp() {
        armMotor.setTargetPosition(ARM_UP);
        armMotor.setPower(0.20);
        // isArmUp = true;
    }

    public void armDown() {
        armMotor.setTargetPosition(ARM_DOWN);
        armMotor.setPower(0.20);
        // isArmUp = false;
    }

    public void armReset() {
        armMotor.setTargetPosition(0);
        armMotor.setPower(0.20);
    }

    public void slackArm() {
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        armMotor.setPower(0);
    }

    public void runChainMotor() {
        chainMotor.setPower(.15);
    }

    public void doTelemetry(Telemetry telemetry) {
        telemetry.addData("Arm Motor", armMotor.getCurrentPosition());
        telemetry.addData("Winch Motor", winchMotor.getCurrentPosition());
        telemetry.addData("isArmUp", isArmUp());
        // telemetry.addData("lockServo Value", lockServo.getServo().getPosition());
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isArmUp() {
        return (armMotor.getCurrentPosition() >= ARM_UP - 5 && armMotor.getCurrentPosition() <= ARM_UP + 5);
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
                servo.setPosition(clipRange(passivePosition, activePosition, servoPos - incrementalSpeed));
            } else if (servoPos < desiredValue){
                servo.setPosition(clipRange(passivePosition, activePosition, servoPos + incrementalSpeed));
            }
        }

        public static double clipRange(double min, double max, double value) {
            // Fix swapped ordering
            if (min > max) {
                double oldMax = max;
                max = min;
                min = oldMax;
            }
            return Math.min(max, Math.max(value, min));
        }

        public Servo getServo() {
            return servo;
        }
    }

}
