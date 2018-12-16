package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
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

    private final int ARM_UP_ABS = -240; // -313
    private final int ARM_DOWN_ABS = 180;
    private int ARM_OFFSET = 250; // -180 for fully down, 0 for level with top
    private int ARM_UP = ARM_UP_ABS + ARM_OFFSET;
    private int ARM_DOWN = ARM_DOWN_ABS + ARM_OFFSET;

    private final int IS_ARM_UP_THRESH = 5;

    private final double WINCH_POWER = 0.5;

    private final double CHAIN_MOTOR_POWER = 0.15;

    private final int SLIDE_POSITION_MIN = 0;
    private final int SLIDE_POSITION_MAX = 3150;
    private final double SLIDE_MOTOR_POWER = 0.3;

    private int slidePosition = 0;

    private double LOCK_SERVO_LOCKED = 0.35;
    private double LOCK_SERVO_UNLOCKED = 0.9;

    private boolean isLocked = false;
    private DcMotor winchMotor;
    private DcMotor chainMotor;
    private DcMotor slideMotor;
    private Servo rightSweepServo;
    private Servo leftSweepServo;
    private TwoStateServo lockServo;

    private DcMotor armMotor;

    private static final double ARM_MOTOR_POWER = 0.05;

    private Telemetry telemetry;

    public GunnerFunction(HardwareMap hardwareMap, Telemetry telemetry) {
        this.armMotor = hardwareMap.dcMotor.get("armMotor");
        this.winchMotor = hardwareMap.dcMotor.get("winchMotor");
        this.lockServo = new GunnerFunction.TwoStateServo(hardwareMap.servo.get("lockServo"), LOCK_SERVO_LOCKED, LOCK_SERVO_UNLOCKED, 0, true);
        this.rightSweepServo = hardwareMap.servo.get("leftSweepServo");
        this.leftSweepServo = hardwareMap.servo.get("rightSweepServo");
        this.chainMotor = hardwareMap.dcMotor.get("chainMotor");
        this.slideMotor = hardwareMap.dcMotor.get("slideMotor");
        resetEncoders();
        this.telemetry = telemetry;
        armZero();
    }

    public void resetEncoders() {
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        winchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // winchMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        winchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        chainMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        chainMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
        armMotor.setTargetPosition(ARM_UP);
    }

    public void armDown() {
        armMotor.setTargetPosition(ARM_DOWN);
    }

    public void armZero() {
        armMotor.setTargetPosition(0);
    }

    public void toggleArm() {
        if (armMotor.getTargetPosition() == ARM_UP) {
            armMotor.setTargetPosition(ARM_DOWN);
        }
        else if (armMotor.getTargetPosition() == ARM_DOWN) {
            armMotor.setTargetPosition(ARM_UP);
        }
        else {
            armMotor.setTargetPosition(ARM_UP); // If we only just initialized
        }
    }

    public boolean isArmUp() {
        return (Math.abs(armMotor.getCurrentPosition() - ARM_UP) < IS_ARM_UP_THRESH);
    }

    public void powerArmMotor() {
        armMotor.setPower(ARM_MOTOR_POWER);
    }

    public void zeroPowerArmMotor() {
        armMotor.setPower(0);
    }

    public void toggleArmPower() {
        if (armMotor.getPower() == 0) {
            armMotor.setPower(ARM_MOTOR_POWER);
        }
        else {
            armMotor.setPower(0);
        }
    }

    /*
    Drive the arm all the way down, and then reset the encoder
    This is pretty janky - will probably lock up the tele-op for ~4s if it works
     */
    public void reZeroArm(RR2TeleOp rr2TeleOp) {
        // Set arm runmode to RUN_WITHOUT_ENCODERS
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Drive arm down
        armMotor.setPower(0.5);

        try {
            rr2TeleOp.wait(4000);
        }
        // If this happened, there would be a problem...
        catch (java.lang.InterruptedException e) {
            e.printStackTrace();
            armMotor.setPower(0);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(ARM_MOTOR_POWER);
            return;
        }

        // Set new arm offset
        ARM_OFFSET = -180; // TODO: Get better value

        // Update arm positions
        ARM_UP = ARM_UP_ABS + ARM_OFFSET;
        ARM_DOWN = ARM_DOWN_ABS + ARM_OFFSET;

        // Revert mode and hold position
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armMotor.setPower(ARM_MOTOR_POWER);
        armMotor.setTargetPosition(armMotor.getCurrentPosition());
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

    public void runSlideMotor() {
        if (slideMotor.getCurrentPosition() < SLIDE_POSITION_MAX) {
            slideMotor.setPower(SLIDE_MOTOR_POWER);
        }
        else {
            slideMotor.setPower(0);
        }
    }

    public void runSlideMotorReverse() {
        if (slideMotor.getCurrentPosition() > SLIDE_POSITION_MIN) {
            slideMotor.setPower(-1 * SLIDE_MOTOR_POWER);
        }
        else {
            slideMotor.setPower(0);
        }
    }

    public void stopSlideMotor() {
        slideMotor.setPower(0);
    }

    public double getSlideMotorPosition() {
        return slideMotor.getCurrentPosition();
    }

    public void sweepServoForward() {
        rightSweepServo.setPosition(1);
        leftSweepServo.setPosition(0);    }

    public void sweepServoReverse() {
        rightSweepServo.setPosition(0);
        leftSweepServo.setPosition(1);
    }

    public void sweepServoStop() {
        rightSweepServo.setPosition(0.46);
        leftSweepServo.setPosition(0.54);
    }

    // --- Telemetry ---

    public void doTelemetry() {
        telemetry.addData("Winch Motor Position", winchMotor.getCurrentPosition());
        telemetry.addData("Winch Motor Power", winchMotor.getPower());
        telemetry.addData("Slide Motor Current Position", slideMotor.getCurrentPosition());
        telemetry.addData("Slide Motor Internal Target", slideMotor.getTargetPosition());
        telemetry.addData("Slide Motor External Target", slidePosition);
        telemetry.addData("Arm Motor Position", armMotor.getCurrentPosition());
        telemetry.addData("Arm Motor Power", armMotor.getPower());
        telemetry.addData("isArmUp", isArmUp());
        telemetry.addData("Arm Setpoint", armMotor.getTargetPosition());
        telemetry.addData("Arm Reached Busy", armMotor.isBusy());
        telemetry.addData("lockServo Position", lockServo.getServo().getPosition());
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
