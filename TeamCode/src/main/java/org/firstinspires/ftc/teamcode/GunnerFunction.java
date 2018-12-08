package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * A utility class that controls all gunner functions (arm, ball sweeper, etc.)
 */
public class GunnerFunction {

    /*
    All the power and position constants are place here, for convenience
    We should probably pass them in instead, but this is way easier...
    */

    private final int ARM_UP_ABS = -313;
    private final int ARM_DOWN_ABS = 180;
    private final int ARM_OFFSET = -180; // -180 for fully down, 0 for level with top
    private final int ARM_UP = ARM_UP_ABS + ARM_OFFSET;
    private final int ARM_DOWN = ARM_DOWN_ABS + ARM_OFFSET;

    private final double ARM_MAX_SPEED_UP = 0.1;
    private final double ARM_MAX_SPEED_DOWN = 0.05;

    private final double WINCH_POWER = 0.5;

    private final double CHAIN_MOTOR_POWER = 0.15;

    private final double SWEEP_SERVO_CENTER = 0.5;
    private final double SWEEP_SERVO_POWER = 0.5; // Between 0 and 0.5

    // TODO: Update the min/max positions
    private final int SLIDE_POSITION_MIN = -500;
    private final int SLIDE_POSITION_MAX = 500;
    private final int SLIDE_POSITION_INCREMENT = 15;
    private final double SLIDE_MOTOR_POWER = 0.2;

    private int slidePosition = 0; // = SLIDE_POSITION_MIN;

    private boolean isLocked = false;
    private DcMotor winchMotor;
    private DcMotor chainMotor;
    private DcMotor slideMotor;
    private Servo sweepServo;
    private TwoStateServo lockServo;

    private SimplePositionMotor armMotor;

    public GunnerFunction(DcMotor armMotor, DcMotor winchMotor, TwoStateServo lockServo, Servo sweepServo, DcMotor chainMotor, DcMotor slideMotor) {
        this.armMotor = new SimplePositionMotor(armMotor);
        this.armMotor.setMaxSpeedForward(ARM_MAX_SPEED_DOWN); // Going down
        this.armMotor.setMaxSpeedReverse(ARM_MAX_SPEED_UP); // Going up
        this.winchMotor = winchMotor;
        this.lockServo = lockServo;
        this.sweepServo = sweepServo;
        this.chainMotor = chainMotor;
        this.slideMotor = slideMotor;
        resetEncoders();
    }

    public void resetEncoders() {
        armMotor.resetEncoder();
        winchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // winchMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION); // TODO: Update winch motor to use RUN_TO_POSITION
        winchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        chainMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        chainMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    // --- Winch Lock Servo ---

    public void unlock() {
        lockServo.active();
        isLocked = false;
    }

    public void lock() {
        lockServo.passive();
        isLocked = true;
    }

    public boolean isLocked() {
        return isLocked;
    }

    // --- Winch Motor ---

    public void winchForward() {
        winchMotor.setPower(WINCH_POWER);
    }

    public void winchReverse() {
        winchMotor.setPower(-1 * WINCH_POWER);
    }

    public void winchStop() {
        winchMotor.setPower(0);
    }

    public void slackWinch() {
        winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        winchMotor.setPower(0);
    }

    public void brakeWinch() {
        winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        winchMotor.setPower(0);
    }

    // --- Arm Motor ---

    public void armUp() {
        armMotor.setSetPoint(ARM_UP);
    }

    public void armDown() {
        armMotor.setSetPoint(ARM_DOWN);
    }

    public void armReset() {
        armMotor.setSetPoint(0);
    }

    public boolean isArmUp() {
        return (Math.abs(armMotor.getPosition() - ARM_UP) < 5);
    }

    public void toggleArm() {
        if (armMotor.getSetPoint() == ARM_UP) {
            armMotor.setSetPoint(ARM_DOWN);
        }
        else if (armMotor.getSetPoint() == ARM_DOWN) {
            armMotor.setSetPoint(ARM_UP);
        }
        // If the arm is not currently in up or down position
        else {
            armMotor.setSetPoint(ARM_UP);
        }
    }

    public void runArmMotorIteration() {
        armMotor.runIteration();
    }

    // --- Sweeper ---

    public void runChainMotor() {
        chainMotor.setPower(-1 * CHAIN_MOTOR_POWER);
    }

    public void runChainMotorReverse() {
        chainMotor.setPower(CHAIN_MOTOR_POWER);
    }

    public void stopChainMotor() {
        chainMotor.setPower(0);
    }

    // TODO: Consider whether the slide motor ever needs to be slacked (probably not...)

    public void powerSlideMotor() {
        slideMotor.setPower(SLIDE_MOTOR_POWER);
    }

    public void stopSlideMotor() {
        slideMotor.setPower(0);
    }

    public void incrementSlideMotor() {
        if (slidePosition + SLIDE_POSITION_INCREMENT <= SLIDE_POSITION_MAX) {
            slidePosition += SLIDE_POSITION_INCREMENT;
            // slideMotor.setTargetPosition(slidePosition);
        }
    }

    public void decrementSlideMotor() {
        if (slidePosition - SLIDE_POSITION_MAX >= SLIDE_POSITION_MIN) {
            slidePosition -= SLIDE_POSITION_INCREMENT;
            // slideMotor.setTargetPosition(slidePosition);
        }
    }

    public void runSlideMotorToTarget() {
        slideMotor.setTargetPosition(slidePosition);
    }

    public void sweepServoForward() {
        sweepServo.setPosition(SWEEP_SERVO_CENTER + SWEEP_SERVO_POWER);
    }

    public void sweepServoReverse() {
        sweepServo.setPosition(SWEEP_SERVO_CENTER - SWEEP_SERVO_POWER);
    }

    public void sweepServoStop() {
        sweepServo.setPosition(SWEEP_SERVO_CENTER);
    }

    // --- Telemetry ---

    public void doTelemetry(Telemetry telemetry) {
        telemetry.addData("Arm Motor", armMotor.getPosition());
        // telemetry.addData("Winch Motor Current", winchMotor.getCurrentPosition());
        telemetry.addData("Slide Motor Current Position", winchMotor.getCurrentPosition());
        telemetry.addData("Slide Motor Internal Target", winchMotor.getTargetPosition());
        telemetry.addData("Slide Motor External Target", slidePosition);
        telemetry.addData("isArmUp", isArmUp());
        // telemetry.addData("lockServo Value", lockServo.getServo().getPosition());
    }

    /**
     * Nested class that provides a servo that toggles between two positions
     */
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
